public class VesselChooseProcess {
    
    OrdersInformationService ordersInformationService;
    ConsignementInformationService consignementInformationService;
    WeatherService weatherService;
    
    DronFlightControlService dronFlightControlService;
    TakeOffDecisionRepository takeOffDecisionRepository;
    
    CargoSpecyfication cargoSpecyfication;
    PlaceOfDeliverySpecyfication placeOfDeliverySpecyfication;
    ProfitabilitySpecyfication profitabilitySpecyfication;
    OrderPrioritySpecyfication orderPrioritySpecyfication;
    WeatherSpecyfication weatherSpecyfication;
    
    public void newCargoInWarehaus(NewCargoInWarehausEvent newCargoInWarehausEvent) {
        Integer cargoId = newCargoInWarehausEvent.getCargoID();
        Integer warehausId = newCargoInWarehausEvent.getWarehausID();
        
        OrderAndCargoInformations orderAndCargoInformations = ordersInformationService
                .getOrderAndCargoInformations(cargoId);
        
        TakeOffDecision takeOffDecision = takeOffDecisionRepository.newDecision(warehausId, cargoId)
                .withPossibleDronTypes(
                        cargoSpecyfication.possibleDronTypes(orderAndCargoInformations))
                .placeOfDeliveryAccepted(
                        placeOfDeliverySpecyfication.matches(orderAndCargoInformations));
        takeOffDecisionRepository.save(takeOffDecision);
        
        if (takeOffDecision.isPositiv()) {
            // TODO: no information about consignements taked into account...
            // can we start? If not compute Step 2. of decision process or wait
            // for assignment to consignment
            
        }
    }
    
    public void orderUpdated(OrderUpdatedEvent orderUpdatedEvent) {
        // TODO: like newCargoInWarehaus ? refactor
    }
    
    public void consignementChanged(ConsignementChangedEvent consignementChangedEvent) {
        // TODO: go through all cargos in new/changed consignement and recompute
        // partial decisions
        
    }
    
    public void periodicallyWeatherCheck() {
        boolean currentWeatherConditionsDecision = takeOffDecisionRepository.getCurrentWeatherConditionsDecision();
        
        ActualWeather actualWeather = weatherService.getActualWeather();
        boolean newWeatherConditionsDecision = weatherSpecyfication.matches(actualWeather);
        
        if (currentWeatherConditionsDecision != newWeatherConditionsDecision) {
            takeOffDecisionRepository.saveCurrentWeatherConditionsDecision(newWeatherConditionsDecision);
            
            if (newWeatherConditionsDecision) {
                // TODO: takeoff all available Drones
            }
        }
    }
    
    public void droneAvaliable(Drone drone) {
        // TODO: handle available drone
    }
}
