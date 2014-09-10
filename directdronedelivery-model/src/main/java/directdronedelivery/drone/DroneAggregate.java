package directdronedelivery.drone;

import org.joda.time.DateTime;

import directdronedelivery.drone.management.communication.AnswerFromDrone;
import directdronedelivery.drone.management.communication.DroneCallbacks;
import directdronedelivery.drone.management.communication.StartCheckList;
import directdronedelivery.drone.management.communication.DeliveryRoute;
import directdronedelivery.drone.management.communication.DroneCommunicationProtocol;
import directdronedelivery.warehouse.TerminalEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode(of = "droneID")
@ToString
public class DroneAggregate {
    
    transient protected DroneCommunicationProtocol droneCommunicationProtocol;
    
    @Getter protected Integer droneID;
    @Getter protected DroneType droneType;
    @Getter protected String ipAddress;
    @Getter protected String firmware;
    
    protected DeliveryRoute route;
    protected TerminalEntity terminal;
    protected Integer cargoID;
    protected boolean cargoDeliveryDenied;
    private DateTime lastPositivCheckOfStartCheckList = new DateTime(0);
    
    protected DroneCallbacks droneCallbacks = new DroneCallbacks() {
        @Override
        public void batteryLow(double level) {
            recheckOfStartCheckListNeeded();
        }
        
        @Override
        public void positionChanged(int position) {
        }
        
        @Override
        public void allert(String info) {
            recheckOfStartCheckListNeeded();
        }
    };
    
    protected DroneAggregate() {
    }
    
    public void attachCargo(Integer cargoID) {
        recheckOfStartCheckListNeeded();
        this.cargoID = cargoID;
    }
    
    public void detachCargo() {
        recheckOfStartCheckListNeeded();
        this.cargoID = null;
    }
    
    public boolean hasCargoAttached() {
        return cargoID != null;
    }
    
    public void dockInTerminal(TerminalEntity terminal) {
        recheckOfStartCheckListNeeded();
        this.terminal = terminal;
        this.route = null;
    }
    
    public void undock() {
        this.terminal = null;
    }
    
    public boolean isDocked() {
        return terminal != null;
    }
    
    public TerminalEntity getDockingTerminal() {
        return terminal;
    }
    
    public boolean ping() {
        return droneCommunicationProtocol.ping(ipAddress);
    }
    
    public AnswerFromDrone uploadDeliveryRoute(DeliveryRoute newRoute) {
        AnswerFromDrone answer = droneCommunicationProtocol.uploadDeliveryRoute(ipAddress, newRoute);
        if (answer.isPositiv()) {
            route = newRoute;
        } else {
            route = null;
        }
        return answer;
    }
    
    public boolean isRouteUploaded() {
        return route != null;
    }
    
    public AnswerFromDrone performStartCheckList(StartCheckList checkList) {
        AnswerFromDrone answer = droneCommunicationProtocol.performStartCheckList(ipAddress, checkList);
        if (answer.isPositiv()) {
            lastPositivCheckOfStartCheckList = DateTime.now();
        }
        return answer;
    }
    
    public boolean isStartCheckListPositive() {
        return lastPositivCheckOfStartCheckList.isAfter(DateTime.now().minusMinutes(5));
    }
    
    public boolean isReadyForTakeOff() {
        if (isDocked() && isRouteUploaded() && isStartCheckListPositive() && ping()) {
            return true;
        } else {
            return false;
        }
    }
    
    public int getPosition() {
        if (isDocked()) {
            return terminal.getPosition();
        } else {
            return droneCommunicationProtocol.currentGPSPosition(ipAddress);
        }
    }
    
    public boolean isExcludedFromCargoDelivery() {
        return cargoDeliveryDenied;
    }
    
    public void denyCargoDelivery() {
        this.cargoDeliveryDenied = true;
    }
    
    public void allowCargoDelivery() { 
        this.cargoDeliveryDenied = false;
    }
    
    private void recheckOfStartCheckListNeeded() {
        lastPositivCheckOfStartCheckList = new DateTime(0);
    }
    
}
