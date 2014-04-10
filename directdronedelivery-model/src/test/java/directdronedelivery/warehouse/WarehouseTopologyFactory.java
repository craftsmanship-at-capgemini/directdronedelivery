package directdronedelivery.warehouse;

import java.util.Arrays;

public class WarehouseTopologyFactory {
    
    public static WarehouseAggregate newWarehouseInBielanyWroclawskie() {
        WarehouseAggregate underConstruction = new WarehouseAggregate();
        underConstruction.terminals = Arrays.asList(
                new TerminalEntity(1),
                new TerminalEntity(2),
                new TerminalEntity(3)
                );
        return underConstruction;
    }
    
    public static TerminalEntity newTerminal(Integer terminalID) {
        return new TerminalEntity(terminalID);
    }
}
