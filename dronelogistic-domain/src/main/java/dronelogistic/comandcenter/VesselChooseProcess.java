package dronelogistic.comandcenter;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Schedule;
import javax.ejb.Stateful;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import dronelogistic.comandcenter.businessrules.CargoSpecyfication;
import dronelogistic.comandcenter.businessrules.DeliveryTimeAcceptanceStrategy;
import dronelogistic.comandcenter.businessrules.OrderPriorityCalculator;
import dronelogistic.comandcenter.businessrules.PlaceOfDeliverySpecyfication;
import dronelogistic.comandcenter.businessrules.ProfitabilityAndPriorityAcceptanceStrategy;
import dronelogistic.comandcenter.businessrules.ProfitabilityCalculator;
import dronelogistic.comandcenter.businessrules.WeatherSpecyfication;
import dronelogistic.dronflightcontrol.AvaliableDrones;
import dronelogistic.dronflightcontrol.DronFlightControlService;
import dronelogistic.dronflightcontrol.Drone;
import dronelogistic.dronflightcontrol.DroneAvaliableEvent;
import dronelogistic.dronflightcontrol.DroneNotAvaliableException;
import dronelogistic.orderinformations.ConsignmentChangedEvent;
import dronelogistic.orderinformations.ConsignmentInformation;
import dronelogistic.orderinformations.OrderAndCargoInformation;
import dronelogistic.orderinformations.OrderUpdatedEvent;
import dronelogistic.orderinformations.OrdersInformationService;
import dronelogistic.warehaus.NewCargoInWarehausEvent;
import dronelogistic.weather.Weather;
import dronelogistic.weather.WeatherService;

@Stateful
@LocalBean
public class VesselChooseProcess {
    
    @EJB OrdersInformationService ordersInformationService;
    @EJB WeatherService weatherService;
    @EJB DronFlightControlService dronFlightControlService;
    @EJB TakeOffDecisionRepository takeOffDecisionRepository;
    @Inject Event<DroneTakeOffDecision> droneTakeOffDecisionEvent;
    
    @Inject CargoSpecyfication cargoSpecyfication;
    @Inject PlaceOfDeliverySpecyfication placeOfDeliverySpecyfication;
    @Inject ProfitabilityCalculator profitabilityCalculator;
    @Inject OrderPriorityCalculator orderPriorityCalculator;
    @Inject ProfitabilityAndPriorityAcceptanceStrategy profitabilityAndPriorityAcceptanceStrategy;
    @Inject DeliveryTimeAcceptanceStrategy deliveryTimeAcceptanceStrategy;
    @Inject WeatherSpecyfication weatherSpecyfication;
    
    public void newCargoInWarehaus(@Observes NewCargoInWarehausEvent newCargoInWarehausEvent) {
        Integer cargoId = newCargoInWarehausEvent.getCargoID();
        Integer warehausId = newCargoInWarehausEvent.getWarehausID();
        
        OrderAndCargoInformation orderAndCargoInformation = ordersInformationService
                .getOrderAndCargoInformation(cargoId);
        
        TakeOffDecision takeOffDecision = takeOffDecisionRepository.newDecision(warehausId, cargoId, orderAndCargoInformation.getAcceptableDeliveryTime());
        takeOffDecision.setPossibleDronTypes(
                cargoSpecyfication.possibleDronTypes(orderAndCargoInformation));
        takeOffDecision.setPlaceOfDeliveryAccepted(
                placeOfDeliverySpecyfication.isAcceptable(orderAndCargoInformation));
        takeOffDecisionRepository.save(takeOffDecision);
        
        // no information about profitability and priority taken into account
        // yet ...
    }
    
    public void orderUpdated(@Observes OrderUpdatedEvent orderUpdatedEvent) {
        Integer cargoId = orderUpdatedEvent.getCargoID();
        
        OrderAndCargoInformation orderAndCargoInformation = ordersInformationService
                .getOrderAndCargoInformation(cargoId);
        
        TakeOffDecision takeOffDecision = takeOffDecisionRepository.get(cargoId);
        boolean originalDecision = takeOffDecision.isPositive(deliveryTimeAcceptanceStrategy);
        takeOffDecision.setPossibleDronTypes(
                cargoSpecyfication.possibleDronTypes(orderAndCargoInformation));
        takeOffDecision.setPlaceOfDeliveryAccepted(
                placeOfDeliverySpecyfication.isAcceptable(orderAndCargoInformation));
        takeOffDecisionRepository.save(takeOffDecision);
        
        if (!originalDecision && takeOffDecision.isPositive(deliveryTimeAcceptanceStrategy)) {
            takeOffIfDronAvaliable(takeOffDecision);
        }
    }
    
    public void consignmentChanged(@Observes ConsignmentChangedEvent consignmentChangedEvent) {
        Integer consignmentID = consignmentChangedEvent.getConsignmentID();
        
        ConsignmentInformation consignmentInformation = ordersInformationService
                .getConsignmentInformation(consignmentID);
        
        for (OrderAndCargoInformation orderAndCargoInformation : consignmentInformation.getCargosInConsignment()) {
            
            TakeOffDecision takeOffDecision = takeOffDecisionRepository.get(orderAndCargoInformation.getCargoID());
            boolean originalDecision = takeOffDecision.isPositive(deliveryTimeAcceptanceStrategy);
            takeOffDecision.setProfitabilityAndPriorityAcceptance(
                    profitabilityAndPriorityAcceptanceStrategy.isPositive(
                            profitabilityCalculator.evaluateProfitability(orderAndCargoInformation,
                                    consignmentInformation),
                            orderPriorityCalculator.evaluatePriority(orderAndCargoInformation,
                                    consignmentInformation)));
            takeOffDecisionRepository.save(takeOffDecision);
            
            if (!originalDecision && takeOffDecision.isPositive(deliveryTimeAcceptanceStrategy)) {
                takeOffIfDronAvaliable(takeOffDecision);
            }
        }
    }
    
    @Schedule(minute = "*/15")
    public void periodicalWeatherCheck() {
        CargoIndependentSubDecisions cargoIndependentSubDecisions = takeOffDecisionRepository
                .getCargoIndependentSubDecisions();
        boolean currentWeatherConditionsDecision = cargoIndependentSubDecisions.isWeatherAcceptable();
        
        Weather actualWeather = weatherService.getActualWeather();
        boolean newWeatherConditionsDecision = weatherSpecyfication.isAcceptable(actualWeather);
        
        if (currentWeatherConditionsDecision != newWeatherConditionsDecision) {
            cargoIndependentSubDecisions.setWeatherAcceptable(newWeatherConditionsDecision);
            
            if (newWeatherConditionsDecision) {
                takeOffAllAvaliableDrones();
            }
        }
    }
    
    @Schedule(minute = "01,31")
    public void periodicalDeliveryTimeAcceptanceCheck() {
        takeOffAllAvaliableDrones();
    }
    
    public void droneAvaliable(@Observes DroneAvaliableEvent droneAvaliableEvent) {
        try {
            String droneTyp = droneAvaliableEvent.getDroneTyp();
            List<TakeOffDecision> takeOffDecisions = takeOffDecisionRepository.getPositiveDecisions(droneTyp,
                    deliveryTimeAcceptanceStrategy, 1);
            
            if (!takeOffDecisions.isEmpty()) {
                Drone drone = dronFlightControlService.reserveDrone(droneTyp);
                droneTakeOffDecisionEvent.fire(new DroneTakeOffDecision(drone, takeOffDecisions.get(0).getCargoID()));
            }
        } catch (DroneNotAvaliableException e) {
        }
    }
    
    private void takeOffIfDronAvaliable(TakeOffDecision takeOffDecision) {
        AvaliableDrones avaliableDrones = dronFlightControlService.getAvaliableDrones();
        
        for (String droneTyp : takeOffDecision.getPossibleDronTypes()) {
            Integer countLimit = avaliableDrones.getCount(droneTyp);
            if (countLimit > 0) {
                try {
                    Drone drone = dronFlightControlService.reserveDrone(droneTyp);
                    droneTakeOffDecisionEvent.fire(new DroneTakeOffDecision(drone, takeOffDecision.getCargoID()));
                    break;
                } catch (DroneNotAvaliableException e) {
                    continue;
                }
            }
        }
    }
    
    private void takeOffAllAvaliableDrones() {
        AvaliableDrones avaliableDrones = dronFlightControlService.getAvaliableDrones();
        for (String droneTyp : avaliableDrones.getDroneTypesInAscSizeOrder()) {
            Integer droneCount = avaliableDrones.getCount(droneTyp);
            if (droneCount == 0) {
                continue;
            }
            List<TakeOffDecision> takeOffDecisions = takeOffDecisionRepository
                    .getPositiveDecisions(droneTyp, deliveryTimeAcceptanceStrategy, droneCount);
            try {
                for (TakeOffDecision takeOffDecision : takeOffDecisions) {
                    Drone drone = dronFlightControlService.reserveDrone(droneTyp);
                    droneTakeOffDecisionEvent
                            .fire(new DroneTakeOffDecision(drone, takeOffDecision.getCargoID()));
                }
            } catch (DroneNotAvaliableException e) {
                continue;
            }
        }
    }
}
