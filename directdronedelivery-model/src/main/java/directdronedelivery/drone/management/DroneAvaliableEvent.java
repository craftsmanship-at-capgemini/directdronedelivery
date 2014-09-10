package directdronedelivery.drone.management;

import directdronedelivery.drone.DroneType;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
@AllArgsConstructor
public class DroneAvaliableEvent {
    
    @Getter private Integer warehouseID;
    @Getter private DroneType droneTyp;
    
}
