package dronelogistic.dronflightcontrol;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class DroneAvaliableEvent {
    
    private String droneTyp;
    
    public String getDroneTyp() {
        return droneTyp;
    }
    
}
