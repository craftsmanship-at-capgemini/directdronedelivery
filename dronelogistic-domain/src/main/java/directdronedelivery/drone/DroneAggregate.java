package directdronedelivery.drone;

import directdronedelivery.drone.management.communication.DeliveryRoute;
import directdronedelivery.warehouse.Terminal;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class DroneAggregate {
    
    @Getter Integer droneID;
    @Getter DroneType droneType;
    @Getter DroneStatus status;
    
    @Getter DeliveryRoute route;
    // TODO MS: currentPosition
    
    Terminal terminal;
    Integer cargoID;
    
    public DroneAggregate(Integer droneID, DroneType droneType) {
        this.droneID = droneID;
        this.droneType = droneType;
        this.status = DroneStatus.LOOKING_FOR_A_JOB;
    }
    
    public DroneAggregate() {
        // TODO MM: clean
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
