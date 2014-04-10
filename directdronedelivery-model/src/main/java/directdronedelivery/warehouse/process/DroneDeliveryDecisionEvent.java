package directdronedelivery.warehouse.process;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
@AllArgsConstructor
public class DroneDeliveryDecisionEvent {
    
    @Getter private Integer droneID;
    @Getter private Integer cargoID;
    
}
