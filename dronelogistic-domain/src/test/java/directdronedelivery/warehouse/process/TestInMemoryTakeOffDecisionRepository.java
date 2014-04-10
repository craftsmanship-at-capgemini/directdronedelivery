package directdronedelivery.warehouse.process;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import lombok.ToString;
import directdronedelivery.cargo.AcceptableDeliveryTime;
import directdronedelivery.drone.DroneType;
import directdronedelivery.warehouse.businessrules.DeliveryTimeAcceptanceStrategy;
import directdronedelivery.warehouse.process.VesselChooseProcessCargoIndependentState;
import directdronedelivery.warehouse.process.VesselChooseProcessCargoState;
import directdronedelivery.warehouse.process.VesselChooseProcessCargoStateRepository;

@ToString
class TestInMemoryTakeOffDecisionRepository implements VesselChooseProcessCargoStateRepository {
    
    private VesselChooseProcessCargoIndependentState cargoIndependentSubDecisions = new VesselChooseProcessCargoIndependentState();
    private Map<Integer, VesselChooseProcessCargoState> inMemoryStore = new HashMap<>();
    
    public static Configurator configure(VesselChooseProcessCargoStateRepository instance) {
        return ((TestInMemoryTakeOffDecisionRepository) instance).new Configurator();
    }
    
    public class Configurator {
        
        public Configurator withCargoIndependentSubDecisions(
                VesselChooseProcessCargoIndependentState cargoIndependentSubDecisions) {
            TestInMemoryTakeOffDecisionRepository.this.cargoIndependentSubDecisions = new VesselChooseProcessCargoIndependentState();
            return this;
        }
        
        public Configurator withPositiveCargoIndependentSubDecisions() {
            cargoIndependentSubDecisions.allowFlights();
            cargoIndependentSubDecisions.setWeatherAcceptable(true);
            return this;
        }
        
        public Configurator withWeatherAcceptable(boolean b) {
            cargoIndependentSubDecisions.setWeatherAcceptable(true);
            return this;
        }
        
        public Configurator withStoredTakeOffDecision(Integer warehausID, Integer cargoID,
                AcceptableDeliveryTime acceptableDeliveryTime) {
            save(newDecision(warehausID, cargoID, acceptableDeliveryTime));
            return this;
        }
    }
    
    @Override
    public void save(VesselChooseProcessCargoState takeOffDecision) {
        inMemoryStore.put(takeOffDecision.getCargoID(), takeOffDecision);
    }
    
    @Override
    public VesselChooseProcessCargoState newDecision(Integer warehausID, Integer cargoID,
            AcceptableDeliveryTime acceptableDeliveryTime) {
        return new VesselChooseProcessCargoState(cargoID, warehausID, acceptableDeliveryTime, cargoIndependentSubDecisions);
    }
    
    @Override
    public List<VesselChooseProcessCargoState> getPositiveDecisions(DroneType droneTyp,
            DeliveryTimeAcceptanceStrategy deliveryTimeAcceptanceStrategy, int countLimit) {
        List<VesselChooseProcessCargoState> positive = new LinkedList<>();
        for (VesselChooseProcessCargoState takeOffDecision : inMemoryStore.values()) {
            if (takeOffDecision.isPositive(deliveryTimeAcceptanceStrategy)
                    && takeOffDecision.getPossibleDronTypes().contains(droneTyp)) {
                positive.add(takeOffDecision);
            }
            if (positive.size() >= countLimit) {
                break;
            }
        }
        return positive;
    }
    
    @Override
    public VesselChooseProcessCargoIndependentState getCargoIndependentSubDecisions() {
        return cargoIndependentSubDecisions;
    }
    
    @Override
    public VesselChooseProcessCargoState get(Integer cargoID) {
        return inMemoryStore.get(cargoID);
    }
}
