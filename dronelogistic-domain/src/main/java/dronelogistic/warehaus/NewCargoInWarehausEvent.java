package dronelogistic.warehaus;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class NewCargoInWarehausEvent {
    
    @Getter private Integer cargoID;
    @Getter private Integer warehausID;
    
}
