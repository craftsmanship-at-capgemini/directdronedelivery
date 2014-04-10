package directdronedelivery.warehouse.process;

import java.util.List;

import directdronedelivery.cargo.AcceptableDeliveryTime;
import directdronedelivery.drone.DroneType;
import directdronedelivery.warehouse.Problem;
import directdronedelivery.warehouse.businessrules.DeliveryTimeAcceptanceStrategy;

public interface VesselChooseProcessCargoStateRepository {
    
    VesselChooseProcessCargoState newProcessState(Integer warehausId, Integer cargoID,
            AcceptableDeliveryTime acceptableDeliveryTime);
    
    VesselChooseProcessCargoState findProcessState(Integer cargoID);
    
    List<VesselChooseProcessCargoState> findPositiveDecisions(DroneType droneTyp,
            DeliveryTimeAcceptanceStrategy deliveryTimeAcceptanceStrategy, int countLimit);
    
    VesselChooseProcessCargoIndependentState getCargoIndependentSubDecisions();
    
    void saveCargoProblems(Integer cargoID, List<Problem> cargoProblems);
    
}
