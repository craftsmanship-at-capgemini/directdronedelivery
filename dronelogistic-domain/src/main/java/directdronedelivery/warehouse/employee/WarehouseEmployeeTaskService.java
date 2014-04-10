package directdronedelivery.warehouse.employee;

import directdronedelivery.cargo.CargoAggregate;
import directdronedelivery.drone.DroneAggregate;
import directdronedelivery.warehouse.BoxType;

public interface WarehouseEmployeeTaskService {
    
    public void addCargoLoadTask(CargoAggregate orderAndCargoInformation, DroneAggregate drone, BoxType boxType);
    
    public void closeTask(Integer taskID);
    
}
