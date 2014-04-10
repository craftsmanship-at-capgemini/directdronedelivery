package dronelogistic.dronflightcontrol;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class AvaliableDrones {
    
    public List<DroneType> droneTypes;
    public Map<DroneType, Integer> droneCounts;
    
    protected AvaliableDrones(List<DroneType> droneTypesInAscSizeOrder, Map<DroneType, Integer> droneCounts) {
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
