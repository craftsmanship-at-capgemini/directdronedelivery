package dronelogistic.dronflightcontrol;

import java.util.List;

public interface DronFlightControlService {
    
    AvaliableDrones getAvaliableDrones();
    
    Drone reserveDrone(String droneTyp) throws DroneNotAvaliableException;
    
    void handleDroneProblems(Integer droneID, List<DroneProblem> problems);
    
}
