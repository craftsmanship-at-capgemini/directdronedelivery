package directdronedelivery.warehouse.process;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
@AllArgsConstructor
public class NewCargoInWarehausEvent {
    
    @Getter private Integer cargoID;
    @Getter private Integer warehouseID;
    
}
