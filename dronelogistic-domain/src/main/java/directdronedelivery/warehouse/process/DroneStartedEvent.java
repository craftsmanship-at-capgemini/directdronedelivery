package directdronedelivery.warehouse.process;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class DroneStartedEvent {
    
    @Getter private Integer droneID;
    
    public DroneStartedEvent(Integer droneID) {
        this.droneID = droneID;
    }
}
