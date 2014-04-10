package directdronedelivery.drone.management.communication;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

// TODO PL: check name: checpoint navipiont fixpoint ...
// TODO PL: it will be nice to have coordinates
@EqualsAndHashCode
@ToString
public class Checkpoint {
    
    @Getter protected int length;
    @Getter protected int latitude;
    
    private Checkpoint() {
    }
    
    public static Checkpoint newCheckpoint(int length, int latitude) {
        Checkpoint checkPoint = new Checkpoint();
        checkPoint.length = length;
        checkPoint.latitude = latitude;
        
        return checkPoint;
    }
    
}
