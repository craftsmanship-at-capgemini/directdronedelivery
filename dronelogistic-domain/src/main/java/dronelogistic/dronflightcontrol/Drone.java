package dronelogistic.dronflightcontrol;

import dronelogistic.warehaus.Terminal;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
@AllArgsConstructor
public class Drone {
    
    @Getter Integer droneID;
    @Getter String droneType;
    Terminal terminal;
    Integer cargoID;
    
    Drone(Integer droneID, String droneType) {
        this.droneID = droneID;
        this.droneType = droneType;
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
