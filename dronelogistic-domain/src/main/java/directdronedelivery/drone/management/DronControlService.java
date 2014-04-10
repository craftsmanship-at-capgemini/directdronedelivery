package directdronedelivery.drone.management;

import java.util.List;

import directdronedelivery.cargo.DeliveryAddress;
import directdronedelivery.drone.DroneAggregate;
import directdronedelivery.drone.DroneType;
import directdronedelivery.drone.management.communication.DeliveryRoute;
import directdronedelivery.warehouse.Problem;

public interface DronControlService {
    
    AvailableDrones getAvailableDrones();
    
    DroneAggregate reserveDrone(DroneType droneTyp) throws DroneNotAvaliableException;
    
    void handleDroneProblems(Integer droneID, List<Problem> problems);
    
    DroneAggregate findDrone(Integer droneId);
    
    DeliveryRoute calculateDeliveryRoute(DeliveryAddress address);
}
