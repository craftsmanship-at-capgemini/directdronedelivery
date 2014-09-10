package directdronedelivery.drone.management.communication;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

// TODO PL: check name: checpoint navipiont fixpoint ... it will be nice to have real coordinates: longitude;latitude+altitude
@EqualsAndHashCode
@ToString
public class Waypoint {
    @Getter protected int length;
    @Getter protected int altitude;
    
    private Waypoint() {
    }
    
    public static Waypoint newCheckpoint(int length, int altitude) {
        Waypoint checkPoint = new Waypoint();
        checkPoint.length = length;
        checkPoint.altitude = altitude;
        
        return checkPoint;
    }
    
}
