package dronelogistic.warehouse.employee;

import dronelogistic.warehaus.BoxType;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
@AllArgsConstructor
public class Task {
    
    @Getter private int taskID;
    @Getter private int cargoID;
    @Getter private int terminalID;
    @Getter private BoxType boxType;
    
}
