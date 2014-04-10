package directdronedelivery.drone;

import directdronedelivery.drone.management.communication.DeliveryRoute;
import directdronedelivery.warehouse.TerminalEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode(of = "droneID")
@ToString
public class DroneAggregate {
    
    @Getter protected Integer droneID;
    @Getter protected DroneType droneType;
    @Getter protected DroneStatus status;
    
    @Getter protected DeliveryRoute route;
    // TODO MS: currentPosition
    
    protected TerminalEntity terminal;
    protected Integer cargoID;
    
    protected DroneAggregate() {
    }
    
    public void attachCargo(Integer cargoID) {
        this.cargoID = cargoID;
    }
    
    public void detachCargo() {
        this.cargoID = null;
    }
    
    public void dockInTerminal(TerminalEntity terminal) {
        this.terminal = terminal;
    }
    
    public void undock() {
        this.terminal = null;
    }
    
    public boolean isDocked() {
        return this.terminal != null;
    }
}
