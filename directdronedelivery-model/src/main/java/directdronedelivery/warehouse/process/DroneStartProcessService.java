package directdronedelivery.warehouse.process;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import directdronedelivery.cargo.CargoAggregate;
import directdronedelivery.cargo.CargoRepository;
import directdronedelivery.drone.DroneAggregate;
import directdronedelivery.drone.DroneRepository;
import directdronedelivery.drone.DroneStatus;
import directdronedelivery.drone.management.DronControlService;
import directdronedelivery.drone.management.communication.AnswerFromDrone;
import directdronedelivery.drone.management.communication.CheckStartList;
import directdronedelivery.drone.management.communication.DeliveryRoute;
import directdronedelivery.drone.management.communication.DroneCommunicationProtocol;

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
    // TODO MM: pack protocol to drone implementation
    @EJB DroneCommunicationProtocol droneCommunicationProtocol;
    @Inject Event<DroneStartedEvent> droneStartedEvent;
    
    /**
     * The DroneStartProcess is triggered via the Event DroneLoadedEvent. This
     * Process calculates the delivery route, makes an upload to the Vessel and
     * if upload succeeds starts the start procedure
     * 
     * @param droneLoadedEvent
     *            Event that Vessel is loaded
     */
    public void initDroneStartProcess(@Observes DroneLoadedEvent droneLoadedEvent) {
        Integer cargoId = droneLoadedEvent.getCargoID();
        Integer droneId = droneLoadedEvent.getDroneID();
        
        CargoAggregate cargo = cargoRepository.findCargo(cargoId);
        
        DeliveryRoute route = droneControlService
                .calculateDeliveryRoute(cargo.getOrder().getDeliveryAddress());
        
        DroneAggregate drone = droneRepository.findDrone(droneId);
        
        AnswerFromDrone answer = droneCommunicationProtocol.uploadDeliveryRoute(drone, route);
        
        if (answer.getDrone().getStatus() == DroneStatus.UPLOAD_FAILED) {
            droneControlService.handleDroneProblems(answer.getDrone().getDroneID(), answer.getProblems());
        } else {
            performStartProcedure(drone);
        }
    }
    
    /**
     * Method Checks if the Pre-Conditons to start the Vessel are fulfilled, if
     * yes the Event VesselStartedEvent is fired.
     * 
     * @param drone
     */
    private void performStartProcedure(DroneAggregate drone) {
        AnswerFromDrone answer = droneCommunicationProtocol.performStartCheckList(drone, createCheckStartList(drone));
        
        if (answer.getDrone().getStatus() == DroneStatus.HOUSTON_WE_HAVE_A_PROBLEM) {
            droneControlService.handleDroneProblems(answer.getDrone().getDroneID(), answer.getProblems());
        } else if (answer.getDrone().getStatus() == DroneStatus.READY_FOR_TAKE_OFF) {
            droneStartedEvent.fire(new DroneStartedEvent(drone.getDroneID()));
        }
    }
    
    private CheckStartList createCheckStartList(DroneAggregate drone) {
        return CheckStartList.newCheckStartList(drone.getDroneType());
    }
    
}
