package directdronedelivery.warehouse.process;

import java.util.List;

import directdronedelivery.cargo.AcceptableDeliveryTime;
import directdronedelivery.drone.DroneType;
import directdronedelivery.warehouse.businessrules.DeliveryTimeAcceptanceStrategy;

public interface VesselChooseProcessCargoStateRepository {
    
    VesselChooseProcessCargoState newDecision(Integer warehausId, Integer cargoID, AcceptableDeliveryTime acceptableDeliveryTime);
    
    void save(VesselChooseProcessCargoState takeOffDecision);
    
    VesselChooseProcessCargoState get(Integer cargoID);
    
    List<VesselChooseProcessCargoState> getPositiveDecisions(DroneType droneTyp,
            DeliveryTimeAcceptanceStrategy deliveryTimeAcceptanceStrategy, int countLimit);
    
    VesselChooseProcessCargoIndependentState getCargoIndependentSubDecisions();
    
}
