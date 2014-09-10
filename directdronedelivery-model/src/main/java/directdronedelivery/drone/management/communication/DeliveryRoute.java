package directdronedelivery.drone.management.communication;

import java.util.Collections;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class DeliveryRoute {
    
    @Getter List<Waypoint> checkpoints;
    
    private DeliveryRoute() {
    }
    
    public static DeliveryRoute newDeliveryRoute(List<Waypoint> checkpoints) {
        DeliveryRoute route = new DeliveryRoute();
        route.checkpoints = Collections.unmodifiableList(checkpoints);
        
        return route;
    }
    
}
