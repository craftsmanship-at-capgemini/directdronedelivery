package directdronedelivery.drone;

import java.util.concurrent.atomic.AtomicInteger;

import directdronedelivery.drone.DroneAggregate;
import directdronedelivery.drone.DroneStatus;
import directdronedelivery.drone.DroneType;
import directdronedelivery.warehouse.TerminalEntity;
import directdronedelivery.warehouse.WarehouseTopologyFactory;

public class DroneBuilder {
    
    private static AtomicInteger nextDroneID = new AtomicInteger(0);
    
    private DroneAggregate drone;
    private TerminalEntity terminal = WarehouseTopologyFactory.newTerminal(1);
    
    public static DroneBuilder aDrone() {
        DroneBuilder builder = new DroneBuilder();
        builder.drone = new DroneAggregate();
        return builder;
    }
    
    public DroneBuilder likeDocked4RotorsDrone() {
        withDroneType(DroneType.SMALL_FOUR_ROTORS);
        withDroneID(nextDroneID.incrementAndGet());
        withDroneStatus(DroneStatus.DOCKED);
        dockedInTerminal(terminal);
        return this;
    }
    
    public DroneBuilder withDroneType(DroneType droneType) {
        this.drone.droneType = droneType;
        return this;
    }
    
    public DroneBuilder withDroneID(int droneID) {
        this.drone.droneID = droneID;
        return this;
    }
    
    public DroneBuilder withDroneStatus(DroneStatus status) {
        this.drone.status = status;
        return this;
    }
    
    public DroneBuilder dockedInTerminal(TerminalEntity terminal) {
        this.drone.dockInTerminal(terminal);
        return this;
    }
    
    public DroneAggregate build() {
        DroneAggregate builded = this.drone;
        this.drone = new DroneAggregate();
        this.drone.droneType = builded.droneType;
        return builded;
    }
    
}
