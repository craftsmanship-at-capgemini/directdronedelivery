package directdronedelivery.drone;

public interface DroneRepository {
    
    DroneAggregate findDrone(int droneID);
    
}
