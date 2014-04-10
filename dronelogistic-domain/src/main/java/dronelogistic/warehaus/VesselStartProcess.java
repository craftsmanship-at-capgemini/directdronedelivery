package dronelogistic.warehaus;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateful;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import dronelogistic.dronflightcontrol.AnswerFromDrone;
import dronelogistic.dronflightcontrol.CheckStartList;
import dronelogistic.dronflightcontrol.DeliveryRoute;
import dronelogistic.dronflightcontrol.DronFlightControlService;
import dronelogistic.dronflightcontrol.Drone;
import dronelogistic.dronflightcontrol.DroneCommunicationService;
import dronelogistic.dronflightcontrol.DroneNotFoundException;
import dronelogistic.dronflightcontrol.DroneStatus;
import dronelogistic.dronflightcontrol.DroneTechnicalService;
import dronelogistic.orderinformations.OrderAndCargoInformation;
import dronelogistic.orderinformations.OrdersInformationService;
import dronelogistic.warehaus.cargoloadprocess.DroneLoadedEvent;

@Stateful
@LocalBean
public class VesselStartProcess {
    
    @EJB OrdersInformationService ordersInformationService;
    @EJB DronFlightControlService droneFlightControlService;
    @EJB DroneCommunicationService droneCommunicationService;
    @EJB DroneTechnicalService droneTechnicalService;
    @Inject Event<VesselStartedEvent> vesselStartedEvent;
    
    /**
     * The VesselStartProcess is triggered via the Event VesselLoadedEvent. This
     * Process calculates the delivery route, makes an upload to the Vessel and
     * if upload succeeds starts the start procedure
     * 
     * @param droneLoadedEvent
     *            Event that Vessel is loaded
     * @throws DroneNotFoundException
     */
    public void vesselLoaded(@Observes DroneLoadedEvent droneLoadedEvent) throws DroneNotFoundException {
        Integer cargoId = droneLoadedEvent.getCargoID();
        Integer droneId = droneLoadedEvent.getDroneID();
        
        OrderAndCargoInformation orderAndCargoInformation = ordersInformationService
                .getOrderAndCargoInformation(cargoId);
        
        DeliveryRoute route = droneFlightControlService
                .calculateDeliveryRoute(orderAndCargoInformation.getDeliveryAddress());
        
        Drone drone = droneFlightControlService.findDrone(droneId);
        
        AnswerFromDrone answer = droneCommunicationService.uploadDeliveryRoute(drone, route);
        
        if (answer.getDrone().getStatus() == DroneStatus.UPLOAD_FAILED) {
            droneTechnicalService.createErrorTicket(answer.getDrone(), answer.getErrors());
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
    private void performStartProcedure(Drone drone) {
        AnswerFromDrone answer = droneCommunicationService.performStartCheckList(drone, createCheckStartList(drone));
        
        if (answer.getDrone().getStatus() == DroneStatus.HOUSTON_WE_HAVE_A_PROBLEM) {
            droneTechnicalService.createErrorTicket(answer.getDrone(), answer.getErrors());
        } else if (answer.getDrone().getStatus() == DroneStatus.READY_FOR_TAKE_OFF) {
            vesselStartedEvent.fire(new VesselStartedEvent(drone));
            
        }
    }
    
    private CheckStartList createCheckStartList(Drone drone) {
        return CheckStartList.newCheckStartList(drone.getDroneType());
    }
    
}
