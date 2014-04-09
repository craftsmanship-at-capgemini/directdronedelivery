package dronelogistic.dronflightcontrol;

public interface DronFlightControlService {
    
    AvaliableDrones getAvaliableDrones();
    
    Drone reserveDrone(String droneTyp) throws DroneNotAvaliableException;
    
    // TODO MM: simplify with DroneProblem
    void notifyDroneProblem(Integer droneID, DroneProblemType droneProblemType, String log);
    
}
