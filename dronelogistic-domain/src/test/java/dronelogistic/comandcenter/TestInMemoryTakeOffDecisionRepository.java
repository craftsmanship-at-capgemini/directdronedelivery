package dronelogistic.comandcenter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import lombok.ToString;
import dronelogistic.comandcenter.businessrules.DeliveryTimeAcceptanceStrategy;
import dronelogistic.orderinformations.AcceptableDeliveryTime;

@ToString
class TestInMemoryTakeOffDecisionRepository implements TakeOffDecisionRepository {
    
    private CargoIndependentSubDecisions cargoIndependentSubDecisions = new CargoIndependentSubDecisions();
    private Map<Integer, TakeOffDecision> inMemoryStore = new HashMap<>();
    
    public static Configurator configure(TakeOffDecisionRepository instance) {
        return ((TestInMemoryTakeOffDecisionRepository) instance).new Configurator();
    }
    
    public class Configurator {
        
        public Configurator withCargoIndependentSubDecisions(
                CargoIndependentSubDecisions cargoIndependentSubDecisions) {
            TestInMemoryTakeOffDecisionRepository.this.cargoIndependentSubDecisions = new CargoIndependentSubDecisions();
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
    public void save(TakeOffDecision takeOffDecision) {
        inMemoryStore.put(takeOffDecision.getCargoID(), takeOffDecision);
    }
    
    @Override
    public TakeOffDecision newDecision(Integer warehausID, Integer cargoID,
            AcceptableDeliveryTime acceptableDeliveryTime) {
        return new TakeOffDecision(cargoID, warehausID, acceptableDeliveryTime, cargoIndependentSubDecisions);
    }
    
    @Override
    public List<TakeOffDecision> getPositiveDecisions(String droneTyp,
            DeliveryTimeAcceptanceStrategy deliveryTimeAcceptanceStrategy, int countLimit) {
        List<TakeOffDecision> positive = new LinkedList<>();
        for (TakeOffDecision takeOffDecision : inMemoryStore.values()) {
            if (takeOffDecision.isPositive(deliveryTimeAcceptanceStrategy)) {
                positive.add(takeOffDecision);
            }
        }
        return positive;
    }
    
    @Override
    public CargoIndependentSubDecisions getCargoIndependentSubDecisions() {
        return cargoIndependentSubDecisions;
    }
    
    @Override
    public TakeOffDecision get(Integer cargoID) {
        return inMemoryStore.get(cargoID);
    }
}
