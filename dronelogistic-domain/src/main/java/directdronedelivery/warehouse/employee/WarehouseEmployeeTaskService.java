package directdronedelivery.warehouse.employee;

import directdronedelivery.cargo.CargoAggregate;
import directdronedelivery.drone.DroneAggregate;
import directdronedelivery.warehouse.BoxType;

public interface WarehouseEmployeeTaskService {
    
    void addCargoLoadTask(CargoAggregate cargo, DroneAggregate drone, BoxType boxType);
    
    void closeTask(Integer taskID);
    
}
