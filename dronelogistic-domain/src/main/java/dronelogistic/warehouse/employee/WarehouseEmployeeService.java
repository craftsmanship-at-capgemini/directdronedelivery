package dronelogistic.warehouse.employee;

import dronelogistic.dronflightcontrol.Drone;
import dronelogistic.orderinformations.OrderAndCargoInformation;
import dronelogistic.warehaus.BoxType;


public interface WarehouseEmployeeService {
    
    public void addCargoLoadTask(OrderAndCargoInformation orderAndCargoInformation, Drone drone, BoxType boxType);
    
    public void closeTask(Integer taskID);
    
}
