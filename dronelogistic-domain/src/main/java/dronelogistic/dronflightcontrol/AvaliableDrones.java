package dronelogistic.dronflightcontrol;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class AvaliableDrones {
    
    public List<String> droneTypes;
    public Map<String, Integer> droneCounts;
    
    protected AvaliableDrones(List<String> droneTypesInAscSizeOrder, Map<String, Integer> droneCounts) {
        this.droneTypes = droneTypesInAscSizeOrder;
        this.droneCounts = droneCounts;
    }
    
    public List<String> getDroneTypesInAscSizeOrder() {
        return Collections.unmodifiableList(droneTypes);
    }
    
    public Integer getCount(String droneTyp) {
        if (droneCounts.containsKey(droneTyp)) {
            return droneCounts.get(droneTyp);
        } else {
            return 0;
        }
    }
    
}
