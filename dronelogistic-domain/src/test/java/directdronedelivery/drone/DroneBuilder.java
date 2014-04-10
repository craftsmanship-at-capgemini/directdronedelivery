package directdronedelivery.drone;

import java.util.concurrent.atomic.AtomicInteger;

import directdronedelivery.drone.DroneAggregate;
import directdronedelivery.drone.DroneStatus;
import directdronedelivery.drone.DroneType;
import directdronedelivery.warehouse.Terminal;

public class DroneBuilder {
    
    private static AtomicInteger nextDroneID = new AtomicInteger(0);
    
    private DroneAggregate drone;
    private Terminal terminal = new Terminal(13);
    
    public static DroneBuilder aDrone() {
        DroneBuilder builder = new DroneBuilder();
        builder.drone = new DroneAggregate();
        return builder;
    }
    
    public DroneBuilder like4RotorsDroneDocked() {
        withDroneID(nextDroneID.incrementAndGet());
        withDroneStatus(DroneStatus.LOOKING_FOR_A_JOB);
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
    
    public DroneBuilder dockedInTerminal(Terminal terminal) {
        this.drone.terminal = terminal;
        return this;
    }
    
    public DroneAggregate build() {
        DroneAggregate builded = this.drone;
        this.drone = new DroneAggregate();
        this.drone.droneType = builded.droneType;
        return builded;
    }
    
}
