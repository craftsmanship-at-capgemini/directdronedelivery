package directdronedelivery.warehouse.process;

import javax.ejb.EJB;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import directdronedelivery.cargo.CargoAggregate;
import directdronedelivery.cargo.OrdersInformationService;
import directdronedelivery.drone.DroneAggregate;
import directdronedelivery.drone.DroneRepository;
import directdronedelivery.drone.management.DronControlService;
import directdronedelivery.warehouse.BoxStockRepository;
import directdronedelivery.warehouse.BoxType;
import directdronedelivery.warehouse.businessrules.BoxChooseRule;
import directdronedelivery.warehouse.employee.CargoLoadTask;
import directdronedelivery.warehouse.employee.TaskAbort;
import directdronedelivery.warehouse.employee.TaskDone;
import directdronedelivery.warehouse.employee.WarehouseEmployeeTaskService;

/**
 * This service supports the load process of the cargo on drones. The process
 * consists mainly of the automatically steps but also of the manually action,
 * which have to be done by a human (warehouse employee).
 * 
 * The service has 3 business methods: choose a Box, confirm load and report
 * problems.
 * 
 * The choose a Box method choose a box for the cargo according to the box
 * specification and delegate the task creation for a warehouse employee via
 * Warehouse Employee Service.
 * 
 * After the task "Load the cargo" has been completed, the warehouse employee
 * confirms , that the cargo is loaded on the drone. The box is physically
 * applied to the drone and the the task will be closed (also via Warehouse
 * Employee Service). At the end service fires the event, that the cargo is
 * loaded.
 * 
 * The warehouse employee has also a possibility to report a problem (technical
 * or logistical). In this case service notify the DronFlightControlService,
 * which is responsible for the processing of the reported problem. The load
 * process is aborted.
 * 
 * @author Grzesiek
 * 
 */
public class CargoLoadProcessService {
    
    @EJB DronControlService dronFlightControlService;
    @EJB VesselChooseProcessService vesselChooseProcess;
    @EJB OrdersInformationService ordersInformationService;
    @EJB WarehouseEmployeeTaskService warehouseEmployeeService;
    
    @Inject BoxStockRepository boxStockRepository;
    @Inject DroneRepository droneRepository;
    @Inject Event<DroneLoadedEvent> droneLoadedEvent;
    
    public void startCargoLoadProcess(@Observes DroneDeliveryDecisionEvent droneTakeOffDecision) {
        Integer droneID = droneTakeOffDecision.getDroneID();
        DroneAggregate drone = droneRepository.findDrone(droneID);
        
        CargoAggregate orderAndCargoInformation = ordersInformationService
                .getOrderAndCargoInformation(droneTakeOffDecision.getCargoID());
        
        BoxChooseRule boxSpecification = new BoxChooseRule(orderAndCargoInformation);
        BoxType boxType = boxStockRepository.decrementStockOfAppropriateBoxes(boxSpecification);
        
        warehouseEmployeeService.addCargoLoadTask(orderAndCargoInformation, drone, boxType);
    }
    
    public void confirmManuallCargoLoadDone(@Observes @TaskDone CargoLoadTask task) {
        DroneAggregate drone = droneRepository.findDrone(task.getDroneID());
        drone.attachCargo(task.getCargoID());
        
        droneLoadedEvent.fire(new DroneLoadedEvent(task.getDroneID(), task.getCargoID()));
        
        warehouseEmployeeService.closeTask(task.getTaskID());
    }
    
    public void abortManuallCargoLoadTask(@Observes @TaskAbort CargoLoadTask task) {
        if (task.hasProblems()) {
            // handle possible problem types, maybe:
            // - cargo can't be delivered with drone for some reasons
            // (temporary/permanent)
            // - drone can't fly for some reasons
            //
            vesselChooseProcess.handleCargoProblems(task.getCargoID(), task.getProblems());
            dronFlightControlService.handleDroneProblems(task.getDroneID(), task.getProblems());
        }
        boxStockRepository.revertStockOfBoxes(task.getBoxType());
        warehouseEmployeeService.closeTask(task.getTaskID());
        
    }
    
}
