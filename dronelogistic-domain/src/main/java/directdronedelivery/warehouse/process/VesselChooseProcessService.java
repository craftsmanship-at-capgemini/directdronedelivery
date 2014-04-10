package directdronedelivery.warehouse.process;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Schedule;
import javax.ejb.Stateful;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import directdronedelivery.cargo.CargoAggregate;
import directdronedelivery.cargo.ConsignmentChangedEvent;
import directdronedelivery.cargo.ConsignmentInformation;
import directdronedelivery.cargo.OrderUpdatedEvent;
import directdronedelivery.cargo.OrdersInformationService;
import directdronedelivery.drone.DroneAggregate;
import directdronedelivery.drone.DroneType;
import directdronedelivery.drone.management.AvailableDrones;
import directdronedelivery.drone.management.DronControlService;
import directdronedelivery.drone.management.DroneAvaliableEvent;
import directdronedelivery.drone.management.DroneNotAvaliableException;
import directdronedelivery.warehouse.Problem;
import directdronedelivery.warehouse.businessrules.CargoSpecyfication;
import directdronedelivery.warehouse.businessrules.DeliveryTimeAcceptanceStrategy;
import directdronedelivery.warehouse.businessrules.OrderPriorityCalculator;
import directdronedelivery.warehouse.businessrules.PlaceOfDeliverySpecyfication;
import directdronedelivery.warehouse.businessrules.ProfitabilityAndPriorityAcceptanceStrategy;
import directdronedelivery.warehouse.businessrules.ProfitabilityCalculator;
import directdronedelivery.warehouse.businessrules.WeatherSpecyfication;
import directdronedelivery.weather.Weather;
import directdronedelivery.weather.WeatherService;

//TODO GST: process description like in DroneLoadProcessService

@Stateful
@LocalBean
public class VesselChooseProcessService {
    
    @EJB OrdersInformationService ordersInformationService;
    @EJB WeatherService weatherService;
    @EJB DronControlService dronFlightControlService;
    @EJB VesselChooseProcessCargoStateRepository vesselChooseProcessStateRepository;
    @Inject Event<DroneDeliveryDecisionEvent> droneTakeOffDecisionEvent;
    
    @Inject CargoSpecyfication cargoSpecification;
    @Inject PlaceOfDeliverySpecyfication placeOfDeliverySpecification;
    @Inject ProfitabilityCalculator profitabilityCalculator;
    @Inject OrderPriorityCalculator orderPriorityCalculator;
    @Inject ProfitabilityAndPriorityAcceptanceStrategy profitabilityAndPriorityAcceptanceStrategy;
    @Inject DeliveryTimeAcceptanceStrategy deliveryTimeAcceptanceStrategy;
    @Inject WeatherSpecyfication weatherSpecyfication;
    
    public void newCargoInWarehaus(@Observes NewCargoInWarehausEvent newCargoInWarehausEvent) {
        Integer cargoId = newCargoInWarehausEvent.getCargoID();
        Integer warehausId = newCargoInWarehausEvent.getWarehausID();
        
        CargoAggregate orderAndCargoInformation = ordersInformationService
                .getOrderAndCargoInformation(cargoId);
        
        VesselChooseProcessCargoState processState = vesselChooseProcessStateRepository.newDecision(warehausId, cargoId,
                orderAndCargoInformation.getOrder().getAcceptableDeliveryTime());
        processState.setPossibleDronTypes(
                cargoSpecification.possibleDronTypes(orderAndCargoInformation));
        processState.setPlaceOfDeliveryAccepted(
                placeOfDeliverySpecification.isAcceptable(orderAndCargoInformation));
        vesselChooseProcessStateRepository.save(processState);
        
        // no information about profitability and priority taken into account
        // yet ...
    }
    
    public void orderUpdated(@Observes OrderUpdatedEvent orderUpdatedEvent) {
        Integer cargoId = orderUpdatedEvent.getCargoID();
        
        CargoAggregate orderAndCargoInformation = ordersInformationService
                .getOrderAndCargoInformation(cargoId);
        
        VesselChooseProcessCargoState processState = vesselChooseProcessStateRepository.get(cargoId);
        boolean originalDecision = processState.isPositive(deliveryTimeAcceptanceStrategy);
        processState.setPossibleDronTypes(
                cargoSpecification.possibleDronTypes(orderAndCargoInformation));
        processState.setPlaceOfDeliveryAccepted(
                placeOfDeliverySpecification.isAcceptable(orderAndCargoInformation));
        vesselChooseProcessStateRepository.save(processState);
        
        if (!originalDecision && processState.isPositive(deliveryTimeAcceptanceStrategy)) {
            takeOffIfDronAvaliable(processState);
        }
    }
    
    public void consignmentChanged(@Observes ConsignmentChangedEvent consignmentChangedEvent) {
        Integer consignmentID = consignmentChangedEvent.getConsignmentID();
        
        ConsignmentInformation consignmentInformation = ordersInformationService
                .getConsignmentInformation(consignmentID);
        
        for (CargoAggregate orderAndCargoInformation : consignmentInformation.getCargosInConsignment()) {
            
            VesselChooseProcessCargoState processState = vesselChooseProcessStateRepository.get(orderAndCargoInformation.getCargoID());
            boolean originalDecision = processState.isPositive(deliveryTimeAcceptanceStrategy);
            processState.setProfitabilityAndPriorityAcceptance(
                    profitabilityAndPriorityAcceptanceStrategy.isPositive(
                            profitabilityCalculator.evaluateProfitability(orderAndCargoInformation,
                                    consignmentInformation),
                            orderPriorityCalculator.evaluatePriority(orderAndCargoInformation,
                                    consignmentInformation)));
            vesselChooseProcessStateRepository.save(processState);
            
            if (!originalDecision && processState.isPositive(deliveryTimeAcceptanceStrategy)) {
                takeOffIfDronAvaliable(processState);
            }
        }
    }
    
    @Schedule(minute = "*/15")
    public void periodicalWeatherCheck() {
        VesselChooseProcessCargoIndependentState cargoIndependentSubDecisions = vesselChooseProcessStateRepository
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
            DroneType droneTyp = droneAvaliableEvent.getDroneTyp();
            List<VesselChooseProcessCargoState> processStates = vesselChooseProcessStateRepository.getPositiveDecisions(droneTyp,
                    deliveryTimeAcceptanceStrategy, 1);
            
            if (!processStates.isEmpty()) {
                DroneAggregate drone = dronFlightControlService.reserveDrone(droneTyp);
                droneTakeOffDecisionEvent.fire(new DroneDeliveryDecisionEvent(drone.getDroneID(), processStates.get(0).getCargoID()));
            }
        } catch (DroneNotAvaliableException e) {
        }
    }
    
    private void takeOffIfDronAvaliable(VesselChooseProcessCargoState processState) {
        AvailableDrones avaliableDrones = dronFlightControlService.getAvailableDrones();
        
        for (DroneType droneTyp : processState.getPossibleDronTypes()) {
            Integer countLimit = avaliableDrones.getCount(droneTyp);
            if (countLimit > 0) {
                try {
                    DroneAggregate drone = dronFlightControlService.reserveDrone(droneTyp);
                    droneTakeOffDecisionEvent.fire(new DroneDeliveryDecisionEvent(drone.getDroneID(), processState.getCargoID()));
                    break;
                } catch (DroneNotAvaliableException e) {
                    continue;
                }
            }
        }
    }
    
    private void takeOffAllAvaliableDrones() {
        AvailableDrones avaliableDrones = dronFlightControlService.getAvailableDrones();
        for (DroneType droneTyp : avaliableDrones.getDroneTypesInAscSizeOrder()) {
            Integer droneCount = avaliableDrones.getCount(droneTyp);
            if (droneCount == 0) {
                continue;
            }
            List<VesselChooseProcessCargoState> processStates = vesselChooseProcessStateRepository
                    .getPositiveDecisions(droneTyp, deliveryTimeAcceptanceStrategy, droneCount);
            try {
                for (VesselChooseProcessCargoState takeOffDecision : processStates) {
                    DroneAggregate drone = dronFlightControlService.reserveDrone(droneTyp);
                    droneTakeOffDecisionEvent
                            .fire(new DroneDeliveryDecisionEvent(drone.getDroneID(), takeOffDecision.getCargoID()));
                }
            } catch (DroneNotAvaliableException e) {
                continue;
            }
        }
    }
    
    public void handleCargoProblems(int cargoID, List<Problem> problems) {
        // TODO MM: VesselChooseProcess.handleCargoProblems
        
    }
}
