package dronelogistic.dronflightcontrol;

import dronelogistic.warehaus.Terminal;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class Drone {
    
    @Getter Integer droneID;
    @Getter DroneType droneType;
    
    @Getter DeliveryRoute route;
    @Getter DroneStatus status;
    
    Terminal terminal;
    Integer cargoID;
    
    Drone(Integer droneID, DroneType droneType) {
        this.droneID = droneID;
        this.droneType = droneType;
        this.status = DroneStatus.READY_FOR_TAKE_OFF;
    }
    
    public void attachCargo(Integer cargoID) {
        this.cargoID = cargoID;
    }
    
    public void detachCargo() {
        this.cargoID = null;
    }
    
    public void dockInTerminal(Terminal terminal) {
        this.terminal = terminal;
    }
    
    public void undock() {
        this.terminal = null;
    }
}
