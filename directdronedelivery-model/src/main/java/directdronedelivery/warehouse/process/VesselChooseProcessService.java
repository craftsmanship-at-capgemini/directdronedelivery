package directdronedelivery.warehouse.process;

import java.util.LinkedList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import directdronedelivery.cargo.CargoAggregate;
import directdronedelivery.cargo.ConsignmentChangedEvent;
import directdronedelivery.cargo.ConsignmentAggregate;
import directdronedelivery.cargo.OrderUpdatedEvent;
import directdronedelivery.cargo.CargoRepository;
import directdronedelivery.drone.DroneAggregate;
import directdronedelivery.drone.DroneType;
import directdronedelivery.drone.management.AvailableDrones;
import directdronedelivery.drone.management.DronControlService;
import directdronedelivery.drone.management.DroneAvaliableEvent;
import directdronedelivery.drone.management.DroneNotAvaliableException;
import directdronedelivery.warehouse.Problem;
import directdronedelivery.warehouse.WarehouseAggregate;
import directdronedelivery.warehouse.WarehouseRepository;
import directdronedelivery.warehouse.businessrules.CargoSpecyfication;
import directdronedelivery.warehouse.businessrules.DeliveryTimeAcceptanceStrategy;
import directdronedelivery.warehouse.businessrules.OrderPriorityCalculator;
import directdronedelivery.warehouse.businessrules.PlaceOfDeliverySpecyfication;
import directdronedelivery.warehouse.businessrules.ProfitabilityAndPriorityAcceptanceStrategy;
import directdronedelivery.warehouse.businessrules.ProfitabilityCalculator;
import directdronedelivery.warehouse.businessrules.WeatherSpecyfication;
import directdronedelivery.weather.Weather;
import directdronedelivery.weather.WeatherService;

/**
 * The Vessel Choose Process Service is the first one in the whole warehouse
 * processes chain.
 * 
 * The process checks and defines if delivery of a cargo with a
 * drone is possible and profitable. If it is not, then the cargo is assigned to
 * a consignment and delivered with conventional means of transport (e.g. a
 * truck).
 * 
 * There are some factors which can influence the decision about the way
 * of delivery. These are the cargo specification (size and weight of the
 * cargo), weather conditions, the profitability or the drone availability.
 * 
 * If a positive decision about delivery with a drone is taken, the Vessel Choose
 * Process Service broadcasts the event DroneDeliveryDeciosonEvent which can be
 * received by further processes of the process chain. The cargo is ready for
 * the load process.
 * 
 */
@Stateless
@LocalBean
public class VesselChooseProcessService {
    
    @EJB CargoRepository cargoRepository;
    @EJB WarehouseRepository warehouseRepository;
    @EJB WeatherService weatherService;
    @EJB DronControlService dronControlService;
    @EJB VesselChooseProcessStateRepository vesselChooseProcessStateRepository;
    @Inject Event<DroneDeliveryDecisionEvent> droneDeliveryDecisionEvent;
    
    @Inject CargoSpecyfication cargoSpecification;
    @Inject PlaceOfDeliverySpecyfication placeOfDeliverySpecification;
    @Inject ProfitabilityCalculator profitabilityCalculator;
    @Inject OrderPriorityCalculator orderPriorityCalculator;
    @Inject ProfitabilityAndPriorityAcceptanceStrategy profitabilityAndPriorityAcceptanceStrategy;
    @Inject DeliveryTimeAcceptanceStrategy deliveryTimeAcceptanceStrategy;
    @Inject WeatherSpecyfication weatherSpecyfication;
    
    public void newCargoInWarehaus(@Observes NewCargoInWarehausEvent newCargoInWarehausEvent) {
        Integer cargoId = newCargoInWarehausEvent.getCargoID();
        Integer warehausId = newCargoInWarehausEvent.getWarehouseID();
        
        CargoAggregate cargo = cargoRepository.findCargo(cargoId);
        
        VesselChooseProcessCargoState processState = vesselChooseProcessStateRepository.newProcessState(
                warehausId,
                cargoId,
                cargo.getOrder().getAcceptableDeliveryTime());
        processState.setPossibleDronTypes(
                cargoSpecification.isSatisfiedForDronTypes(cargo));
        processState.setPlaceOfDeliveryAccepted(
                placeOfDeliverySpecification.isSatisfiedBy(cargo));
        
        // no information about profitability and priority taken into account
        // yet ...
    }
    
    public void orderUpdated(@Observes OrderUpdatedEvent orderUpdatedEvent) {
        Integer cargoId = orderUpdatedEvent.getCargoID();
        
        CargoAggregate cargo = cargoRepository.findCargo(cargoId);
        
        VesselChooseProcessCargoState processState = vesselChooseProcessStateRepository.findProcessState(cargoId);
        boolean originalDecision = processState.isPositive(deliveryTimeAcceptanceStrategy);
        processState.setPossibleDronTypes(
                cargoSpecification.isSatisfiedForDronTypes(cargo));
        processState.setPlaceOfDeliveryAccepted(
                placeOfDeliverySpecification.isSatisfiedBy(cargo));
        
        if (!originalDecision && processState.isPositive(deliveryTimeAcceptanceStrategy)) {
            takeOffIfDronAvaliable(processState);
        }
    }
    
    public void consignmentChanged(@Observes ConsignmentChangedEvent consignmentChangedEvent) {
        Integer consignmentID = consignmentChangedEvent.getConsignmentID();
        
        ConsignmentAggregate consignment = cargoRepository.findConsignment(consignmentID);
        
        for (CargoAggregate cargo : consignment.getCargosInConsignment()) {
            
            VesselChooseProcessCargoState processState = vesselChooseProcessStateRepository
                    .findProcessState(cargo.getCargoID());
            boolean originalDecision = processState.isPositive(deliveryTimeAcceptanceStrategy);
            processState.setProfitabilityAndPriorityAcceptance(
                    profitabilityAndPriorityAcceptanceStrategy.isPositive(
                            profitabilityCalculator.evaluateProfitability(cargo, consignment),
                            orderPriorityCalculator.evaluatePriority(cargo, consignment)));
            
            if (!originalDecision && processState.isPositive(deliveryTimeAcceptanceStrategy)) {
                takeOffIfDronAvaliable(processState);
            }
        }
    }
    
    public void truckDeliveryStarted(@Observes TruckDeliveryStartedEvent truckDeliveryStartedEvent) {
        Integer consignmentID = truckDeliveryStartedEvent.getConsignmentID();
        
        ConsignmentAggregate consignment = cargoRepository.findConsignment(consignmentID);
        
        for (CargoAggregate cargo : consignment.getCargosInConsignment()) {
            
            VesselChooseProcessCargoState processState = vesselChooseProcessStateRepository
                    .findProcessState(cargo.getCargoID());
            
            processState.setAlreadyDeliveredWithTruck(true);
        }
    }
    
    @Schedule(minute = "*/15")
    protected void periodicalWeatherCheck() {
        List<WarehouseAggregate> warehouses = warehouseRepository.findAllWarehouses();
        for (WarehouseAggregate warehouse : warehouses) {
            VesselChooseProcessWarehouseState warehouseState = 
                    vesselChooseProcessStateRepository.findWarehouseState(warehouse.getWarehouseID());
            boolean currentWeatherConditionsDecision = warehouseState.isWeatherAcceptable();
            
            Weather actualWeather = weatherService.getActualWeather(warehouse.getLocation());
            boolean newWeatherConditionsDecision = weatherSpecyfication.isSatisfiedBy(actualWeather);
            
            if (currentWeatherConditionsDecision != newWeatherConditionsDecision) {
                warehouseState.setWeatherAcceptable(newWeatherConditionsDecision);
                if (newWeatherConditionsDecision) {
                    takeOffAllAvaliableDrones(warehouse);
                }
            }
        }
    }
    
    @Schedule(minute = "01,31")
    protected void periodicalDeliveryTimeAcceptanceCheck() {
        List<WarehouseAggregate> warehouses = warehouseRepository.findAllWarehouses();
        for (WarehouseAggregate warehouse : warehouses) {
            takeOffAllAvaliableDrones(warehouse);
        }
    }
    
    public void handleCargoProblems(Integer cargoID, List<Problem> problems) {
        List<Problem> cargoProblems = new LinkedList<>();
        for (Problem problem : problems) {
            if (!problem.getProblemType().isCargoDeliverableWithDrone()) {
                cargoProblems.add(problem);
            }
        }
        if (!cargoProblems.isEmpty()) {
            VesselChooseProcessCargoState processState = vesselChooseProcessStateRepository
                    .findProcessState(cargoID);
            processState.denyDroneDelivery();
            vesselChooseProcessStateRepository.saveCargoProblems(cargoID, cargoProblems);
        }
    }
    
    public void droneAvaliable(@Observes DroneAvaliableEvent droneAvaliableEvent) {
        try {
            DroneType droneTyp = droneAvaliableEvent.getDroneTyp();
            List<VesselChooseProcessCargoState> processStates = vesselChooseProcessStateRepository
                    .findPositiveDecisions(droneTyp,
                            deliveryTimeAcceptanceStrategy, 1);
            
            if (!processStates.isEmpty()) {
                DroneAggregate drone = dronControlService.reserveDrone(droneAvaliableEvent.getWarehouseID(), droneTyp);
                droneDeliveryDecisionEvent.fire(new DroneDeliveryDecisionEvent(drone.getDroneID(), processStates.get(0)
                        .getCargoID()));
            }
        } catch (DroneNotAvaliableException e) {
        }
    }
    
    private void takeOffIfDronAvaliable(VesselChooseProcessCargoState processState) {
        AvailableDrones avaliableDrones = dronControlService.getAvailableDrones(processState.getWarehouseID());
        
        for (DroneType droneTyp : processState.getPossibleDronTypes()) {
            Integer countLimit = avaliableDrones.getCount(droneTyp);
            if (countLimit > 0) {
                try {
                    DroneAggregate drone = dronControlService.reserveDrone(processState.getWarehouseID(), droneTyp);
                    droneDeliveryDecisionEvent.fire(new DroneDeliveryDecisionEvent(drone.getDroneID(), processState
                            .getCargoID()));
                    break;
                } catch (DroneNotAvaliableException e) {
                    continue;
                }
            }
        }
    }
    
    private void takeOffAllAvaliableDrones(WarehouseAggregate warehouse) {
        AvailableDrones avaliableDrones = dronControlService.getAvailableDrones(warehouse.getWarehouseID());
        for (DroneType droneTyp : avaliableDrones.getDroneTypesInAscSizeOrder()) {
            Integer droneCount = avaliableDrones.getCount(droneTyp);
            if (droneCount == 0) {
                continue;
            }
            List<VesselChooseProcessCargoState> processStates = vesselChooseProcessStateRepository
                    .findPositiveDecisions(droneTyp, deliveryTimeAcceptanceStrategy, droneCount);
            try {
                for (VesselChooseProcessCargoState takeOffDecision : processStates) {
                    DroneAggregate drone = dronControlService.reserveDrone(warehouse.getWarehouseID(), droneTyp);
                    droneDeliveryDecisionEvent
                            .fire(new DroneDeliveryDecisionEvent(drone.getDroneID(), takeOffDecision.getCargoID()));
                }
            } catch (DroneNotAvaliableException e) {
                continue;
            }
        }
    }
}
