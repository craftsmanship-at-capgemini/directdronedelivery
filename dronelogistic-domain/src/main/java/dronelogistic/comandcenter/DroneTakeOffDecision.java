package dronelogistic.comandcenter;

import dronelogistic.dronflightcontrol.Drone;

public class DroneTakeOffDecision {
    
    private Drone drone;
    private Integer cargoID;
    
    public DroneTakeOffDecision(Drone drone, Integer cargoID) {
        this.drone = drone;
        this.cargoID = cargoID;
    }
    
    public Drone getDrone() {
        return drone;
    }
    
    public Integer getCargoID() {
        return cargoID;
    }
    
}
