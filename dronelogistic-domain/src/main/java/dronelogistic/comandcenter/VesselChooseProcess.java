package dronelogistic.comandcenter;

import java.util.List;

@Stateful
@LocalBean
public class VesselChooseProcess {
    
    @EJB OrdersInformationService ordersInformationService;
    @EJB WeatherService weatherService;
    @Inject Event<DroneTakeOffDecision> droneTakeOffDecisionEvent;
    
    @EJB DronFlightControlService dronFlightControlService;
    @EJB TakeOffDecisionRepository takeOffDecisionRepository;
    
    @Inject CargoSpecyfication cargoSpecyfication;
    @Inject PlaceOfDeliverySpecyfication placeOfDeliverySpecyfication;
    @Inject ProfitabilitySpecyfication profitabilitySpecyfication;
    @Inject OrderPrioritySpecyfication orderPrioritySpecyfication;
    @Inject WeatherSpecyfication weatherSpecyfication;
    
    public void newCargoInWarehaus(@Observes NewCargoInWarehausEvent newCargoInWarehausEvent) {
        Integer cargoId = newCargoInWarehausEvent.getCargoID();
        Integer warehausId = newCargoInWarehausEvent.getWarehausID();
        
        OrderAndCargoInformation orderAndCargoInformation = ordersInformationService
                .getOrderAndCargoInformation(cargoId);
        
        TakeOffDecision takeOffDecision = takeOffDecisionRepository.newDecision(warehausId, cargoId)
                .withPossibleDronTypes(
                        cargoSpecyfication.possibleDronTypes(orderAndCargoInformation))
                .placeOfDeliveryAccepted(
                        placeOfDeliverySpecyfication.matches(orderAndCargoInformation));
        takeOffDecisionRepository.save(takeOffDecision);
        
        // no information about consignements taked into account yet ...
    }
    
    public void orderUpdated(@Observes OrderUpdatedEvent orderUpdatedEvent) {
        Integer cargoId = orderUpdatedEvent.getCargoID();
        
        OrderAndCargoInformation orderAndCargoInformation = ordersInformationService
                .getOrderAndCargoInformation(cargoId);
        
        TakeOffDecision takeOffDecision = takeOffDecisionRepository.get(cargoId);
        takeOffDecision
                .withPossibleDronTypes(
                        cargoSpecyfication.evaluatePossibleDronTypes(orderAndCargoInformation))
                .placeOfDeliveryAccepted(
                        placeOfDeliverySpecyfication.isDeliveryPossible(orderAndCargoInformation));
        takeOffDecisionRepository.save(takeOffDecision);
        
        if (takeOffDecision.isPositive()) {
            takeOffIfDronAvaliable(takeOffDecision);
        }
    }
    
    public void consignementChanged(@Observes ConsignementChangedEvent consignementChangedEvent) {
        Integer consignementID = consignementChangedEvent.getConsignementID();
        ConsignementInformation consignementInformation = ordersInformationService
                .getOrderAndCargoInformation(consignementID);
        
        for (OrderAndCargoInformation orderAndCargoInformation : consignementInformation.getCargosInConsignement()) {
            TakeOffDecision takeOffDecision = takeOffDecisionRepository.get(cargoId);
            boolean originalDecision = takeOffDecision.isPositive();
            if (takeOffDecision.isCargoAndOrderAcceptable()) {
                takeOffDecision.profitabilityEvaluation(
                        profitabilitySpecyfication.evaluateProfitability(orderAndCargoInformation,
                                consignementInformation));
                takeOffDecision.orderPriorityEvaluation(
                        orderPrioritySpecyfication.evaluatePriority(orderAndCargoInformation, consignementInformation));
                takeOffDecisionRepository.save(takeOffDecision);
                
                if (!originalDecision && takeOffDecision.isPositive()) {
                    takeOffIfDronAvaliable(takeOffDecision);
                }
            }
        }
    }
    
    @Schedule(minute = "*/15")
    private void periodicalWeatherCheck() {
        boolean currentWeatherConditionsDecision = takeOffDecisionRepository.getCurrentWeatherConditionsDecision();
        
        ActualWeather actualWeather = weatherService.getActualWeather();
        boolean newWeatherConditionsDecision = weatherSpecyfication.matches(actualWeather);
        
        if (currentWeatherConditionsDecision != newWeatherConditionsDecision) {
            takeOffDecisionRepository.saveCurrentWeatherConditionsDecision(newWeatherConditionsDecision);
            
            if (newWeatherConditionsDecision) {
                AvaliableDrones avaliableDrones = dronFlightControlService.getAvaliableDrones();
                for (String droneTyp : avaliableDrones.getDroneTypesInAscSizeOrder()) {
                    Integer countLimit = avaliableDrones.getCount(droneTyp);
                    List<TakeOffDecision> takeOffDecisions = takeOffDecisionRepository.getPositiveDecisions(droneTyp,
                            countLimit);
                    for (TakeOffDecision takeOffDecision : takeOffDecisions) {
                        Drone drone = dronFlightControlService.reserveDrone(droneTyp);
                        droneTakeOffDecisionEvent.emit(new DroneTakeOffDecision(drone, takeOffDecision.getCargoID()));
                    }
                }
            }
        }
    }
    
    public void droneAvaliable(@Observes DroneAvaliableEvent droneAvaliableEvent) {
        String droneTyp = droneAvaliableEvent.getDroneTyp();
        List<TakeOffDecision> takeOffDecisions = takeOffDecisionRepository.getPositiveDecisions(drone.getDroneTyp(), 1);
        
        if (!takeOffDecisions.isEmpty()) {
            Drone drone = dronFlightControlService.reserveDrone(droneTyp);
            droneTakeOffDecisionEvent.emit(new DroneTakeOffDecision(drone, takeOffDecisions.get(0).getCargoID()));
        }
    }
    
    private void takeOffIfDronAvaliable(TakeOffDecision takeOffDecision) {
        assert takeOffDecision.isPositive();
        
        AvaliableDrones avaliableDrones = dronFlightControlService
                .getAvaliableDrones(takeOffDecision.getPossibleDronTypes());
        
        for (String droneTyp : avaliableDrones.getDroneTypesInAscSizeOrder()) {
            Integer countLimit = avaliableDrones.getCount(droneTyp);
            if (countLimit > 0) {
                Drone drone = dronFlightControlService.reserveDrone(droneTyp);
                droneTakeOffDecisionEvent.emit(new DroneTakeOffDecision(drone, takeOffDecision.getCargoID()));
            }
        }
    }
}
