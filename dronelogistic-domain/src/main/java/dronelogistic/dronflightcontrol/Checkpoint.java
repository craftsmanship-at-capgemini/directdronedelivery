package dronelogistic.dronflightcontrol;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class Checkpoint {
    
    @Getter int length;
    @Getter int latitude;
    
    private Checkpoint() {
    }
    
    public static Checkpoint newCheckpoint(int length, int latitude) {
        Checkpoint checkPoint = new Checkpoint();
        checkPoint.length = length;
        checkPoint.latitude = latitude;
        
        return checkPoint;
    }
    
}
