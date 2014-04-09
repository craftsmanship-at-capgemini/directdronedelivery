package dronelogistic.dronflightcontrol;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
@AllArgsConstructor
public class DroneProblem {
    
    @Getter DroneProblemType droneProblemType;
    @Getter String log;
    
}
