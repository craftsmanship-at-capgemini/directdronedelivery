package dronelogistic.warehaus;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import dronelogistic.dronflightcontrol.Drone;

@EqualsAndHashCode
@ToString
public class VesselStartedEvent {
    
    @Getter private Drone drone;
    
    public VesselStartedEvent(Drone drone) {
        this.drone = drone;
    }
}
