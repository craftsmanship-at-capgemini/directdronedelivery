package dronelogistic.dronflightcontrol;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class ErrorInformation {
    
    @Getter protected Integer errorId;
    @Getter protected String errorDescription;
    
}
