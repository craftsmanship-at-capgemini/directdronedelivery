package dronelogistic.dronflightcontrol;

import java.util.List;

import dronelogistic.orderinformations.DeliveryAddress;

public interface DronFlightControlService {
    
    AvaliableDrones getAvaliableDrones();
    
    Drone reserveDrone(DroneType droneTyp) throws DroneNotAvaliableException;
    
    void handleDroneProblems(Integer droneID, List<DroneProblem> problems);
    
    Drone findDrone(Integer droneId) throws DroneNotFoundException;
    
    DeliveryRoute calculateDeliveryRoute(DeliveryAddress address);
}
