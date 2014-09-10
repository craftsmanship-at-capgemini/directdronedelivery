package directdronedelivery.warehouse.process;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import lombok.ToString;
import directdronedelivery.cargo.AcceptableDeliveryTime;
import directdronedelivery.drone.DroneType;
import directdronedelivery.warehouse.Problem;
import directdronedelivery.warehouse.businessrules.DeliveryTimeAcceptanceStrategy;
import directdronedelivery.warehouse.process.VesselChooseProcessWarehouseState;
import directdronedelivery.warehouse.process.VesselChooseProcessCargoState;
import directdronedelivery.warehouse.process.VesselChooseProcessStateRepository;

@ToString
class VesselChooseProcessStateRepositoryInMem implements VesselChooseProcessStateRepository {
    
    private Map<Integer, VesselChooseProcessWarehouseState> warehouseStates = new HashMap<>();
    private Map<Integer, VesselChooseProcessCargoState> cargoStates = new HashMap<>();
    
    public static Configurator configure(VesselChooseProcessStateRepository instance) {
        return ((VesselChooseProcessStateRepositoryInMem) instance).new Configurator();
    }
    
    public class Configurator {
        
        public Configurator withWarehouseState(Integer warehouseID,
                VesselChooseProcessWarehouseState warehouseState) {
            warehouseStates.put(warehouseID, warehouseState);
            return this;
        }
        
        public Configurator withPositiveWarehouseState(Integer warehouseID) {
            VesselChooseProcessWarehouseState warehouseState = new VesselChooseProcessWarehouseState();
            warehouseState.allowFlights();
            warehouseState.setWeatherAcceptable(true);
            warehouseStates.put(warehouseID, warehouseState);
            return this;
        }
        
        public Configurator withWeatherAcceptable(boolean b) {
            for (VesselChooseProcessWarehouseState warehouseState : warehouseStates.values()) {
                warehouseState.setWeatherAcceptable(true);
            }
            return this;
        }
        
        public Configurator withCargoState(Integer warehouseID, Integer cargoID,
                AcceptableDeliveryTime acceptableDeliveryTime) {
            newProcessState(warehouseID, cargoID, acceptableDeliveryTime);
            return this;
        }
    }
    
    @Override
    public VesselChooseProcessCargoState newProcessState(Integer warehouseID, Integer cargoID,
            AcceptableDeliveryTime acceptableDeliveryTime) {
        VesselChooseProcessCargoState processState = new VesselChooseProcessCargoState(cargoID, warehouseID,
                acceptableDeliveryTime, warehouseStates.get(warehouseID));
        cargoStates.put(processState.getCargoID(), processState);
        return processState;
    }
    
    @Override
    public List<VesselChooseProcessCargoState> findPositiveDecisions(DroneType droneTyp,
            DeliveryTimeAcceptanceStrategy deliveryTimeAcceptanceStrategy, int countLimit) {
        List<VesselChooseProcessCargoState> positive = new LinkedList<>();
        for (VesselChooseProcessCargoState processState : cargoStates.values()) {
            if (processState.isPositive(deliveryTimeAcceptanceStrategy)
                    && processState.getPossibleDronTypes().contains(droneTyp)) {
                positive.add(processState);
            }
            if (positive.size() >= countLimit) {
                break;
            }
        }
        return positive;
    }
    
    @Override
    public VesselChooseProcessWarehouseState findWarehouseState(Integer warehouseID) {
        return warehouseStates.get(warehouseID);
    }
    
    @Override
    public VesselChooseProcessCargoState findProcessState(Integer cargoID) {
        return cargoStates.get(cargoID);
    }
    
    @Override
    public void saveCargoProblems(Integer cargoID, List<Problem> cargoProblems) {
        // not used in tests
    }
}
