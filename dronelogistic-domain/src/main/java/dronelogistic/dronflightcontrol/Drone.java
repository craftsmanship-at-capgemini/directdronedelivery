package dronelogistic.dronflightcontrol;

import dronelogistic.warehaus.Box;
import dronelogistic.warehaus.Terminal;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
@AllArgsConstructor
public class Drone {
    
    @Getter int droneID;
    @Getter String droneType;
    @Getter Terminal terminal;
    @Getter Box box;
    
    public Drone(int droneID, String droneType) {
        this.droneID = droneID;
        this.droneType = droneType;
    }
    
    public void atachBox(Box box) {
        this.box = box;
    }
    
    public void deattachkBox() {
        this.box = null;
    }
    
    public void dockInTerminal(Terminal terminal) {
        this.terminal = terminal;
    }
    
    public void undock() {
        this.terminal = null;
    }
}
