package directdronedelivery.drone.management;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
@AllArgsConstructor
public class DroneStartedEvent {
    
    @Getter private Integer droneID;
    
}
