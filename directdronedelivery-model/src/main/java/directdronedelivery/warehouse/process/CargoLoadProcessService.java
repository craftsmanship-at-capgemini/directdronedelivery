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
import directdronedelivery.drone.management.DronControlService;
import directdronedelivery.warehouse.BoxStockRepository;
import directdronedelivery.warehouse.BoxType;
import directdronedelivery.warehouse.businessrules.BoxChooseSpecification;
import directdronedelivery.warehouse.employee.CargoLoadTask;
import directdronedelivery.warehouse.employee.TaskAbort;
import directdronedelivery.warehouse.employee.TaskDone;
import directdronedelivery.warehouse.employee.WarehouseEmployeeTaskService;

/**
 * This service supports the load process of the cargo on drones. The process
 * consists mainly of the automatic steps but contains some manual actions as
 * well, which have to be done by a warehouse employee.
 * 
 * The Cargo Load Process begins with choosing an appropriate box for the cargo.
 * The system chooses a box for the cargo according to the box specification and
 * delegates creation of a task (load the cargo) for a warehouse employee to the
 * Warehouse Employee Service.
 * 
 * After the task "load the cargo" has been completed, the warehouse employee
 * confirms that the cargo has been loaded on the drone. The box is physically
 * attached to the drone and the load task is closed (also via Warehouse
 * Employee Service).
 * 
 * At the end the service fires an event that the cargo is loaded. The warehouse
 * employee has also a possibility to report a problem. In this case service
 * notifies the Drone Control Service, which is responsible for processing of
 * the reported problem. The load process is aborted. The Vessel Choose Process
 * Service handles the problem, if it concerns the cargo.
 * 
 */
@Stateless
@LocalBean
public class CargoLoadProcessService {
    
    @EJB DronControlService dronControlService;
    @EJB VesselChooseProcessService vesselChooseProcessService;
    @EJB CargoRepository cargoRepository;
    @EJB DroneRepository droneRepository;
    @EJB WarehouseEmployeeTaskService warehouseEmployeeTaskService;
    
    @Inject BoxStockRepository boxStockRepository;
    @Inject Event<DroneLoadedEvent> droneLoadedEvent;
    
    public void startCargoLoadProcess(@Observes DroneDeliveryDecisionEvent droneDeliveryDecisionEvent) {
        Integer droneID = droneDeliveryDecisionEvent.getDroneID();
        DroneAggregate drone = droneRepository.findDrone(droneID);
        
        CargoAggregate cargo = cargoRepository
                .findCargo(droneDeliveryDecisionEvent.getCargoID());
        
        BoxChooseSpecification boxChooseRule = new BoxChooseSpecification(cargo);
        BoxType boxType = boxStockRepository.decrementStockOfAppropriateBoxes(boxChooseRule);
        
        warehouseEmployeeTaskService.addCargoLoadTask(cargo, drone, boxType);
    }
    
    public void confirmManuallCargoLoadDone(@Observes @TaskDone CargoLoadTask task) {
        DroneAggregate drone = droneRepository.findDrone(task.getDroneID());
        drone.attachCargo(task.getCargoID());
        
        droneLoadedEvent.fire(new DroneLoadedEvent(task.getDroneID(), task.getCargoID()));
        
        warehouseEmployeeTaskService.closeTask(task.getTaskID());
    }
    
    public void abortManuallCargoLoadTask(@Observes @TaskAbort CargoLoadTask task) {
        if (task.hasProblems()) {
            // handle possible problem types, maybe:
            // - cargo can't be delivered with drone for some reasons
            // (temporary/permanent)
            // - drone can't fly for some reasons
            // - no more boxes
            // ..
            vesselChooseProcessService.handleCargoProblems(task.getCargoID(), task.getProblems());
            dronControlService.handleDroneProblems(task.getDroneID(), task.getProblems());
        }
        boxStockRepository.revertStockOfBoxes(task.getBoxType());
        warehouseEmployeeTaskService.closeTask(task.getTaskID());
    }
    
}
