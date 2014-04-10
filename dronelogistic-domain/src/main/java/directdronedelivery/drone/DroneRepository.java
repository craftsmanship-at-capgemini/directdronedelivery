package directdronedelivery.drone;

public interface DroneRepository {
    
    public DroneAggregate findDrone(int droneID);
    
}
