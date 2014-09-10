package directdronedelivery.drone;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

import org.mockito.Mockito;

import directdronedelivery.drone.DroneAggregate;
import directdronedelivery.drone.DroneType;
import directdronedelivery.drone.management.communication.AnswerFromDrone;
import directdronedelivery.drone.management.communication.DeliveryRoute;
import directdronedelivery.drone.management.communication.DroneCommunicationProtocol;
import directdronedelivery.drone.management.communication.StartCheckList;
import directdronedelivery.warehouse.Problem;
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
        withMockedDroneCommunicationProtocol();
        withDroneType(DroneType.SMALL_FOUR_ROTORS);
        withDroneID(nextDroneID.incrementAndGet());
        withIPAddress("194.13.82.11");
        withFirmware("UAV_DRONECORP_SMALL2013_V14.2.13");
        withDeliveryRoute(null);
        dockedInTerminal(terminal);
        withCargoDettached();
        withCargoDeliveryDenied(false);
        return this;
    }
    
    public DroneBuilder but() {
        return this;
    }
    
    /**
     * Do not change ipAddress of drone after setting custom protocol instance,
     * in other case droneCallbacks can be registered not properly.
     */
    public DroneBuilder withDroneCommunicationProtocol(DroneCommunicationProtocol droneCommunicationProtocol, String ipAddress) {
        this.drone.droneCommunicationProtocol = droneCommunicationProtocol;
        withIPAddress(ipAddress);
        droneCommunicationProtocol.addCallbacks(ipAddress, this.drone.droneCallbacks);
        return this;
    }
    
    public DroneBuilder withMockedDroneCommunicationProtocol() {
        DroneCommunicationProtocol droneCommunicationProtocol = Mockito.mock(DroneCommunicationProtocol.class);
        Mockito.when(
                droneCommunicationProtocol.performStartCheckList(Mockito.anyString(), Mockito.any(StartCheckList.class)))
                .thenReturn(AnswerFromDrone.newAnswer(drone, Collections.<Problem> emptyList()));
        
        Mockito.when(
                droneCommunicationProtocol.uploadDeliveryRoute(Mockito.anyString(), Mockito.any(DeliveryRoute.class)))
                .thenReturn(AnswerFromDrone.newAnswer(drone, Collections.<Problem> emptyList()));
        
        Mockito.when(droneCommunicationProtocol.ping(Mockito.anyString())).thenReturn(true);
        
        this.drone.droneCommunicationProtocol = droneCommunicationProtocol;
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
    
    public DroneBuilder withIPAddress(String ipAddress) {
        this.drone.ipAddress = ipAddress;
        return this;
    }
    
    public DroneBuilder withFirmware(String firmware) {
        this.drone.firmware = firmware;
        return this;
    }
    
    public DroneBuilder withDeliveryRoute(DeliveryRoute route) {
        this.drone.route = route;
        return this;
    }
    
    public DroneBuilder withoutDeliveryRoute() {
        this.drone.route = null;
        return this;
    }
    
    public DroneBuilder dockedInTerminal(TerminalEntity terminal) {
        this.drone.dockInTerminal(terminal);
        return this;
    }
    
    public DroneBuilder withCargoAttached(Integer cargoID) {
        this.drone.attachCargo(cargoID);
        return this;
    }
    
    public DroneBuilder withCargoDettached() {
        this.drone.detachCargo();
        return this;
    }
    
    public DroneBuilder withCargoDeliveryDenied(boolean denied) {
        drone.cargoDeliveryDenied = denied;
        return this;
    }
    
    public DroneAggregate build() {
        DroneAggregate builded = this.drone;
        this.drone = new DroneAggregate();
        this.drone.droneType = builded.droneType;
        return builded;
    }
    
}
