package dronelogistic.comandcenter;

import java.util.List;

import dronelogistic.comandcenter.businessrules.DeliveryTimeAcceptanceStrategy;
import dronelogistic.dronflightcontrol.DroneType;
import dronelogistic.orderinformations.AcceptableDeliveryTime;

public interface TakeOffDecisionRepository {
    
    TakeOffDecision newDecision(Integer warehausId, Integer cargoID, AcceptableDeliveryTime acceptableDeliveryTime);
    
    void save(TakeOffDecision takeOffDecision);
    
    TakeOffDecision get(Integer cargoID);
    
    List<TakeOffDecision> getPositiveDecisions(DroneType droneTyp,
            DeliveryTimeAcceptanceStrategy deliveryTimeAcceptanceStrategy, int countLimit);
    
    CargoIndependentSubDecisions getCargoIndependentSubDecisions();
    
}
