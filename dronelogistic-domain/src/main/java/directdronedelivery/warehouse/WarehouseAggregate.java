package directdronedelivery.warehouse;

import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode(of = "warehouseID")
@ToString
public class WarehouseAggregate {
    
    @Getter protected Integer warehouseID;
    @Getter protected List<TerminalEntity> terminals;
    
    protected WarehouseAggregate() {
    }
}
