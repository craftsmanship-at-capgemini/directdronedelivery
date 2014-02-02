package dronelogistic.comandcenter;

import java.util.List;

import dronelogistic.comandcenter.businessrules.DeliveryTimeAcceptanceStrategy;

public interface TakeOffDecisionRepository {
    
    TakeOffDecision newDecision(Integer warehausId, Integer cargoId);
    
    void save(TakeOffDecision takeOffDecision);
    
    TakeOffDecision get(Integer cargoId);
    
    List<TakeOffDecision> getPositiveDecisions(String droneTyp,
            DeliveryTimeAcceptanceStrategy deliveryTimeAcceptanceStrategy, int countLimit);
    
    CargoIndependentSubDecisions getCargoIndependentSubDecisions();
    
}
