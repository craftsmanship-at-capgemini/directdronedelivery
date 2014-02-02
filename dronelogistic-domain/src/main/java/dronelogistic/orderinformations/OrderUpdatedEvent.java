package dronelogistic.orderinformations;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class OrderUpdatedEvent {
    
    private Integer cargoID;
    
    public Integer getCargoID() {
        return cargoID;
    }
    
}
