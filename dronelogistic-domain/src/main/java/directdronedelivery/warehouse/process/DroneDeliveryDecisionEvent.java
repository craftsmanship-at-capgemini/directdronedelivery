package directdronedelivery.warehouse.process;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class DroneDeliveryDecisionEvent {
    
    @Getter private Integer droneID;
    @Getter private Integer cargoID;
    
    public DroneDeliveryDecisionEvent(Integer droneID, Integer cargoID) {
        this.droneID = droneID;
        this.cargoID = cargoID;
    }
    
}
