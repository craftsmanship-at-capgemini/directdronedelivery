package directdronedelivery.warehouse;

import java.util.Arrays;

public class WarehouseTopologyFactory {
    
    public static WarehouseAggregate newWarehouseInBielanyWroclawskie() {
        WarehouseAggregate underConstruction = new WarehouseAggregate();
        underConstruction.warehouseID = 1;
        underConstruction.location = "Poland/Wroclaw";
        underConstruction.terminals = Arrays.asList(
                newTerminal(1),
                newTerminal(2),
                newTerminal(3)
                );
        return underConstruction;
    }
    
    public static TerminalEntity newTerminal(Integer terminalID) {
        TerminalEntity underConstruction = new TerminalEntity();
        underConstruction.terminalID = terminalID;
        underConstruction.position = 42;
        return underConstruction;
    }
}
