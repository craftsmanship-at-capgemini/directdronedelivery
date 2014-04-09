package dronelogistic.warehaus.cargoloadprocess;

import javax.enterprise.event.Observes;

import dronelogistic.comandcenter.DroneTakeOffDecision;
import dronelogistic.dronflightcontrol.DroneProblemType;
import dronelogistic.warehaus.Box;

public interface CargoLoadService {
    
    public void startCargoLoadProcess(@Observes DroneTakeOffDecision droneTakeOffDecision);
    
    public void confirmLoad(Integer droneID, Integer cargoID, Integer taskID, Integer boxID);
    
    public void reportProblem(DroneProblemType problemType, String log, Integer droneID);
    
}
