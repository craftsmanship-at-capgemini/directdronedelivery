package dronelogistic.warehouse.employee;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
@AllArgsConstructor
public class WarehouseEmployee {
    
    @Getter int employeeID;
    @Getter String surname;
    @Getter String name;
    @Getter List<Task> taskList;
    
}
