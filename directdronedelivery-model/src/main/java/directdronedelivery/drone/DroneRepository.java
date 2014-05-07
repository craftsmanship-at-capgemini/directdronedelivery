package directdronedelivery.drone;

public interface DroneRepository {
    
    DroneAggregate findDrone(Integer droneID);
    
}
