package dronelogistic.dronflightcontrol;

public interface DronFlightControlService {
    
    AvaliableDrones getAvaliableDrones();
    
    Drone reserveDrone(String droneTyp) throws DroneNotAvaliableException;
    
}
