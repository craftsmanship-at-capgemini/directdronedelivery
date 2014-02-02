package dronelogistic.comandcenter;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import dronelogistic.dronflightcontrol.Drone;

@EqualsAndHashCode
@ToString
public class DroneTakeOffDecision {
    
    @Getter private Drone drone;
    @Getter private Integer cargoID;
    
    public DroneTakeOffDecision(Drone drone, Integer cargoID) {
        this.drone = drone;
        this.cargoID = cargoID;
    }
    
}
