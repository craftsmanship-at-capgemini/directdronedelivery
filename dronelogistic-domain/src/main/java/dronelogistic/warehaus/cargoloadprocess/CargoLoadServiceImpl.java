package dronelogistic.warehaus.cargoloadprocess;

import javax.ejb.EJB;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import dronelogistic.comandcenter.DroneTakeOffDecision;
import dronelogistic.dronflightcontrol.DronFlightControlService;
import dronelogistic.dronflightcontrol.Drone;
import dronelogistic.dronflightcontrol.DroneLoadedEvent;
import dronelogistic.dronflightcontrol.DroneProblemType;
import dronelogistic.dronflightcontrol.DroneRepository;
import dronelogistic.orderinformations.OrderAndCargoInformation;
import dronelogistic.orderinformations.OrdersInformationService;
import dronelogistic.warehaus.Box;
import dronelogistic.warehaus.BoxRepository;
import dronelogistic.warehaus.BoxSpecification;
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
public class CargoLoadServiceImpl implements CargoLoadService {
    
    @EJB DronFlightControlService dronFlightControlService;
    @EJB OrdersInformationService ordersInformationService;
    @EJB WarehouseEmployeeService warehouseEmployeeService;
    
    @Inject BoxRepository boxRepository;
    @Inject DroneRepository droneRepository;
    @Inject Event<DroneLoadedEvent> droneLoadedEvent;
    @Inject Event<CargoLoadProcessAbortEvent> loadProcessAbortEvent;
    
    @Override
    public void startCargoLoadProcess(@Observes DroneTakeOffDecision droneTakeOffDecision) {
        
        Integer cargoID = droneTakeOffDecision.getCargoID();
        Drone drone = droneTakeOffDecision.getDrone();
        Integer terminalID = drone.getTerminal().getTerminalID();
        
        // Order and cargo information for the cargo with cargoID
        // TODO: Pytanie: OrderAndCargoInformation, nie mieszamy tu modeli?
        // Nazwa skaldajaca sie z dwoch nazw encji biznesowych nie jest
        // moim zdaniem najlepsza. Proponuje nazwe Delivery
        OrderAndCargoInformation orderAndCargoInformation = ordersInformationService
                .getOrderAndCargoInformation(cargoID);
        
        // define box specification according to the cargo information
        BoxSpecification boxSpecification = new BoxSpecification(orderAndCargoInformation.getSize(),
                orderAndCargoInformation.getWeightInGrams());
        
        // find appropriate box for the cargo according to the box specification
        Box box = boxRepository.findAppropriateBox(boxSpecification);
        
        warehouseEmployeeService.addNewTask(cargoID, terminalID, box.getBoxID());
    }
    
    @Override
    public void confirmLoad(Integer droneID, Integer cargoID, Integer taskID, Integer boxID) {
        
        // Attach box to the drone
        Drone drone = droneRepository.findDrone(droneID);
        Box box = boxRepository.findBox(boxID);
        drone.atachBox(box);
        
        // fire event: drone loaded
        droneLoadedEvent.fire(new DroneLoadedEvent(droneID, cargoID));
        
        // close the task
        warehouseEmployeeService.closeTask(taskID);
        
    }
    
    @Override
    public void reportProblem(DroneProblemType problemType, String log, Integer droneID) {
        
        dronFlightControlService.notifyDroneProblem(droneID, problemType, log);
        
        Drone drone = droneRepository.findDrone(droneID);
        Integer boxID = drone.getBox() != null ? drone.getBox().getBoxID() : null;
        // TODO: w zadnym miejscu nie mamy zdefiniowanego powiazania pomiedzy
        // dronem a cargiem, ktorego dron transportuje
        Integer cargoID = null; // drone.getCargo.getCargoID();
        loadProcessAbortEvent.fire(new CargoLoadProcessAbortEvent(droneID, cargoID, boxID));
        
    }
    
}
