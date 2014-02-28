package dronelogistic.dronflightcontrol;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
@AllArgsConstructor
public class DroneAvaliableEvent {
    
    @Getter private String droneTyp;
    
}
