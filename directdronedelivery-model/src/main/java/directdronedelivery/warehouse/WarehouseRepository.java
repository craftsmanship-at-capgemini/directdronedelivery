package directdronedelivery.warehouse;

import java.util.List;

public interface WarehouseRepository {
    
    WarehouseAggregate findWarehous(Integer warehousID);
    
    TerminalEntity findTerminal(Integer terminalID);
    
    List<TerminalEntity> findAllTerminals(Integer warehouseID);
    
}
