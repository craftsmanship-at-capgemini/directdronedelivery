package directdronedelivery.drone.management;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import directdronedelivery.cargo.DeliveryAddress;
import directdronedelivery.drone.DroneAggregate;
import directdronedelivery.drone.DroneRepository;
import directdronedelivery.drone.DroneType;
import directdronedelivery.drone.management.communication.DeliveryRoute;
import directdronedelivery.warehouse.Problem;

@Stateless
@LocalBean
public class DronControlService {
    
    @EJB DroneRepository droneRepository;
    
    public AvailableDrones getAvailableDrones(Integer warehouseID) {
        return null;
    }
    
    public DroneAggregate reserveDrone(Integer warehouseID, DroneType droneTyp) throws DroneNotAvaliableException {
        return null;
    }
    
    public void cancelDroneReservation(DroneAggregate drone) {
    }
    
    public DeliveryRoute calculateDeliveryRoute(int point, DeliveryAddress address) {
        return null;
    }
    
    public void takeOff(DroneAggregate drone) {
    }
    
    public void handleDroneProblems(Integer droneID, List<Problem> problems) {
    }
    
}
