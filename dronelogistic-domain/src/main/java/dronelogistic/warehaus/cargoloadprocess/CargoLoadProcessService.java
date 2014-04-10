package dronelogistic.warehaus.cargoloadprocess;

import javax.ejb.EJB;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import dronelogistic.comandcenter.DroneTakeOffDecision;
import dronelogistic.comandcenter.VesselChooseProcess;
import dronelogistic.dronflightcontrol.DronFlightControlService;
import dronelogistic.dronflightcontrol.Drone;
import dronelogistic.dronflightcontrol.DroneRepository;
import dronelogistic.orderinformations.OrderAndCargoInformation;
import dronelogistic.orderinformations.OrdersInformationService;
import dronelogistic.warehaus.BoxStockRepository;
import dronelogistic.warehaus.BoxSpecification;
import dronelogistic.warehaus.BoxType;
import dronelogistic.warehouse.employee.CargoLoadTask;
import dronelogistic.warehouse.employee.TaskAbort;
import dronelogistic.warehouse.employee.TaskDone;
import dronelogistic.warehouse.employee.WarehouseEmployeeService;

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
    
    @EJB DronFlightControlService dronFlightControlService;
    @EJB VesselChooseProcess vesselChooseProcess;
    @EJB OrdersInformationService ordersInformationService;
    @EJB WarehouseEmployeeService warehouseEmployeeService;
    
    @Inject BoxStockRepository boxStockRepository;
    @Inject DroneRepository droneRepository;
    @Inject Event<DroneLoadedEvent> droneLoadedEvent;
    
    public void startCargoLoadProcess(@Observes DroneTakeOffDecision droneTakeOffDecision) {
        Drone drone = droneTakeOffDecision.getDrone();
        // TODO: split to Cargo with reference to Order
        OrderAndCargoInformation orderAndCargoInformation = ordersInformationService
                .getOrderAndCargoInformation(droneTakeOffDecision.getCargoID());
        
        BoxSpecification boxSpecification = new BoxSpecification(orderAndCargoInformation);
        BoxType boxType = boxStockRepository.decrementStockOfAppropriateBoxes(boxSpecification);
        
        warehouseEmployeeService.addCargoLoadTask(orderAndCargoInformation, drone, boxType);
    }
    
    public void confirmManuallCargoLoadDone(@Observes @TaskDone CargoLoadTask task) {
        Drone drone = droneRepository.findDrone(task.getDroneID());
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
