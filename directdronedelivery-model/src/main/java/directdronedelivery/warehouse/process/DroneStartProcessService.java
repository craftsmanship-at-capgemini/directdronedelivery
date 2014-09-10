package directdronedelivery.warehouse.process;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.enterprise.event.Observes;

import directdronedelivery.cargo.CargoAggregate;
import directdronedelivery.cargo.CargoRepository;
import directdronedelivery.drone.DroneAggregate;
import directdronedelivery.drone.DroneRepository;
import directdronedelivery.drone.management.DronControlService;
import directdronedelivery.drone.management.communication.AnswerFromDrone;
import directdronedelivery.drone.management.communication.StartCheckList;
import directdronedelivery.drone.management.communication.DeliveryRoute;

/**
 * The last element of the whole process in the warehouse is the Drone Start
 * Process. The process is triggered by the event DroneLoadedEvent.
 * 
 * The process calculates the route for the delivery in the first step and
 * uploads it into the drone system. The upload occurs via Drone Communication
 * Protocol which is responsible for the whole communication between the
 * warehouse system and the onboard system of the drones.
 * 
 * If the upload of the route succeeded, the start procedure begins. Before the
 * drone takes off, the check list must be completed. The possible problems are
 * forwarded to the Drone Control Service, which handles it.
 * 
 */
@Stateless
@LocalBean
public class DroneStartProcessService {
    
    @EJB CargoRepository cargoRepository;
    @EJB DroneRepository droneRepository;
    @EJB DronControlService droneControlService;
    
    /**
     * The DroneStartProcess is triggered via the Event DroneLoadedEvent. This
     * Process calculates the delivery route, makes an upload to the Vessel and
     * if upload succeeds starts the start procedure
     * 
     * @param cargoLoadedEvent
     *            Event that Vessel is loaded
     */
    public void initDroneStartProcess(@Observes CargoLoadedEvent cargoLoadedEvent) {
        Integer cargoId = cargoLoadedEvent.getCargoID();
        Integer droneId = cargoLoadedEvent.getDroneID();
        
        CargoAggregate cargo = cargoRepository.findCargo(cargoId);
        DroneAggregate drone = droneRepository.findDrone(droneId);
        
        DeliveryRoute route = droneControlService
                .calculateDeliveryRoute(drone.getPosition(), cargo.getOrder().getDeliveryAddress());
        
        AnswerFromDrone answer = drone.uploadDeliveryRoute(route);
        
        if (answer.isPositiv()) {
            performStartProcedure(drone);
        } else {
            droneControlService.handleDroneProblems(answer.getDroneID(), answer.getProblems());
        }
    }
    
    /**
     * Method Checks if the Pre-Conditons to start the Vessel are fulfilled, if
     * yes drone take off.
     * 
     * @param drone
     */
    private void performStartProcedure(DroneAggregate drone) {
        AnswerFromDrone answer = drone.performStartCheckList(StartCheckList.newStartCheckList(drone.getDroneType()));
        
        if (answer.isPositiv()) {
            droneControlService.takeOff(drone);
        } else {
            droneControlService.handleDroneProblems(answer.getDroneID(), answer.getProblems());
        }
    }
    
}
