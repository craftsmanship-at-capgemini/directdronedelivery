package directdronedelivery.warehouse;

import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class Warehouse {
    
    @Getter Integer warehouseID;
    @Getter List<Terminal> terminals;
    
}
