package dronelogistic.orderinformations;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class OrderUpdatedEvent {
    
    @Getter private Integer cargoID;
    
}
