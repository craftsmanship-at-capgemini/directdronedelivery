package dronelogistic.warehouse.employee;


public interface WarehouseEmployeeService {
    
    public Integer addNewTask(Integer cargoID, Integer terminalID, Integer boxID);
    
    public void closeTask(Integer taskID);
    
}
