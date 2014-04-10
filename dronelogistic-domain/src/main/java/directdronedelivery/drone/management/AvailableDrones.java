package directdronedelivery.drone.management;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import directdronedelivery.drone.DroneType;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class AvailableDrones {
    
    public List<DroneType> droneTypes;
    public Map<DroneType, Integer> droneCounts;
    
    protected AvailableDrones(List<DroneType> droneTypesInAscSizeOrder, Map<DroneType, Integer> droneCounts) {
        this.droneTypes = droneTypesInAscSizeOrder;
        this.droneCounts = droneCounts;
    }
    
    public List<DroneType> getDroneTypesInAscSizeOrder() {
        return Collections.unmodifiableList(droneTypes);
    }
    
    public Integer getCount(DroneType droneTyp) {
        if (droneCounts.containsKey(droneTyp)) {
            return droneCounts.get(droneTyp);
        } else {
            return 0;
        }
    }
    
}
