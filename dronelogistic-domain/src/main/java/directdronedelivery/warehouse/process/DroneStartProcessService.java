package directdronedelivery.warehouse.process;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateful;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import directdronedelivery.cargo.CargoAggregate;
import directdronedelivery.cargo.OrdersInformationService;
import directdronedelivery.drone.DroneAggregate;
import directdronedelivery.drone.DroneStatus;
import directdronedelivery.drone.management.DronControlService;
import directdronedelivery.drone.management.communication.AnswerFromDrone;
import directdronedelivery.drone.management.communication.CheckStartList;
import directdronedelivery.drone.management.communication.DeliveryRoute;
import directdronedelivery.drone.management.communication.DroneCommunicationService;

// TODO GST: process description like in DroneLoadProcessService
@Stateful
@LocalBean
public class DroneStartProcessService {
    
    @EJB OrdersInformationService ordersInformationService;
    @EJB DronControlService droneControlService;
    @EJB DroneCommunicationService droneCommunicationService;
    @Inject Event<DroneStartedEvent> vesselStartedEvent;
    
    /**
     * The VesselStartProcess is triggered via the Event VesselLoadedEvent. This
     * Process calculates the delivery route, makes an upload to the Vessel and
     * if upload succeeds starts the start procedure
     * 
     * @param droneLoadedEvent
     *            Event that Vessel is loaded
     */
    public void vesselLoaded(@Observes DroneLoadedEvent droneLoadedEvent) {
        Integer cargoId = droneLoadedEvent.getCargoID();
        Integer droneId = droneLoadedEvent.getDroneID();
        
        CargoAggregate orderAndCargoInformation = ordersInformationService
                .getOrderAndCargoInformation(cargoId);
        
        DeliveryRoute route = droneControlService
                .calculateDeliveryRoute(orderAndCargoInformation.getOrder().getDeliveryAddress());
        
        DroneAggregate drone = droneControlService.findDrone(droneId);
        
        AnswerFromDrone answer = droneCommunicationService.uploadDeliveryRoute(drone, route);
        
        if (answer.getDrone().getStatus() == DroneStatus.UPLOAD_FAILED) {
            droneControlService.handleDroneProblems(answer.getDrone().getDroneID(), answer.getErrors());
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
        AnswerFromDrone answer = droneCommunicationService.performStartCheckList(drone, createCheckStartList(drone));
        
        if (answer.getDrone().getStatus() == DroneStatus.HOUSTON_WE_HAVE_A_PROBLEM) {
            droneControlService.handleDroneProblems(answer.getDrone().getDroneID(), answer.getErrors());
        } else if (answer.getDrone().getStatus() == DroneStatus.READY_FOR_TAKE_OFF) {
            vesselStartedEvent.fire(new DroneStartedEvent(drone.getDroneID()));
            
        }
    }
    
    private CheckStartList createCheckStartList(DroneAggregate drone) {
        return CheckStartList.newCheckStartList(drone.getDroneType());
    }
    
}
