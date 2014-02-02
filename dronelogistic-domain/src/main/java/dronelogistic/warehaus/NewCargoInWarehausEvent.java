package dronelogistic.warehaus;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class NewCargoInWarehausEvent {
    
    private Integer cargoID;
    private Integer warehausID;
    
    public Integer getCargoID() {
        return cargoID;
    }
    
    public Integer getWarehausID() {
        return warehausID;
    }
    
}
