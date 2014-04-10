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
import directdronedelivery.warehouse.process.VesselChooseProcessCargoIndependentState;
import directdronedelivery.warehouse.process.VesselChooseProcessCargoState;
import directdronedelivery.warehouse.process.VesselChooseProcessCargoStateRepository;

@ToString
class VesselChooseProcessCargoStateRepositoryInMem implements VesselChooseProcessCargoStateRepository {
    
    private VesselChooseProcessCargoIndependentState cargoIndependentState = new VesselChooseProcessCargoIndependentState();
    private Map<Integer, VesselChooseProcessCargoState> inMemoryStore = new HashMap<>();
    
    public static Configurator configure(VesselChooseProcessCargoStateRepository instance) {
        return ((VesselChooseProcessCargoStateRepositoryInMem) instance).new Configurator();
    }
    
    public class Configurator {
        
        public Configurator withCargoIndependentSubDecisions(
                VesselChooseProcessCargoIndependentState cargoIndependentSubDecisions) {
            VesselChooseProcessCargoStateRepositoryInMem.this.cargoIndependentState = new VesselChooseProcessCargoIndependentState();
            return this;
        }
        
        public Configurator withPositiveCargoIndependentSubDecisions() {
            cargoIndependentState.allowFlights();
            cargoIndependentState.setWeatherAcceptable(true);
            return this;
        }
        
        public Configurator withWeatherAcceptable(boolean b) {
            cargoIndependentState.setWeatherAcceptable(true);
            return this;
        }
        
        public Configurator withStoredTakeOffDecision(Integer warehausID, Integer cargoID,
                AcceptableDeliveryTime acceptableDeliveryTime) {
            newProcessState(warehausID, cargoID, acceptableDeliveryTime);
            return this;
        }
    }
    
    @Override
    public VesselChooseProcessCargoState newProcessState(Integer warehausID, Integer cargoID,
            AcceptableDeliveryTime acceptableDeliveryTime) {
        VesselChooseProcessCargoState processState = new VesselChooseProcessCargoState(cargoID, warehausID,
                acceptableDeliveryTime, cargoIndependentState);
        inMemoryStore.put(processState.getCargoID(), processState);
        return processState;
    }
    
    @Override
    public List<VesselChooseProcessCargoState> findPositiveDecisions(DroneType droneTyp,
            DeliveryTimeAcceptanceStrategy deliveryTimeAcceptanceStrategy, int countLimit) {
        List<VesselChooseProcessCargoState> positive = new LinkedList<>();
        for (VesselChooseProcessCargoState processState : inMemoryStore.values()) {
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
    public VesselChooseProcessCargoIndependentState getCargoIndependentSubDecisions() {
        return cargoIndependentState;
    }
    
    @Override
    public VesselChooseProcessCargoState findProcessState(Integer cargoID) {
        return inMemoryStore.get(cargoID);
    }
    
    @Override
    public void saveCargoProblems(Integer cargoID, List<Problem> cargoProblems) {
    }
}
