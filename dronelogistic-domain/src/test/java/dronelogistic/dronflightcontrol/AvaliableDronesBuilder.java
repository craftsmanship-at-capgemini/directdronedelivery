package dronelogistic.dronflightcontrol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class AvaliableDronesBuilder {
    
    private static AtomicInteger nextDroneID = new AtomicInteger(0);
    
    private AvaliableDrones underConstruction = null;
    private List<String> typicalDroneTypes = Arrays.asList("T4 v1", "T8 v1");
    
    private AvaliableDronesBuilder() {
    }
    
    public static Drone newDrone(int droneID, String droneType) {
        return new Drone(droneID, droneType);
    }
    
    public static Drone newDrone(String droneType) {
        return new Drone(nextDroneID.incrementAndGet(), droneType);
    }
    
    public static Drone newDrone() {
        return new Drone(nextDroneID.incrementAndGet(), "T4 v1");
    }
    
    public static AvaliableDronesBuilder anAvaliableDrones() {
        AvaliableDronesBuilder builder = new AvaliableDronesBuilder();
        builder.underConstruction = new AvaliableDrones(new ArrayList<String>(), new HashMap<String, Integer>());
        return builder;
    }
    
    public AvaliableDronesBuilder likeNoDronesAvaliable() {
        withDroneTypes(typicalDroneTypes);
        withNoDroneAvaliable();
        return this;
    }
    
    public AvaliableDronesBuilder but() {
        return this;
    }
    
    public AvaliableDronesBuilder withDroneTypes(List<String> droneTypes) {
        underConstruction.droneTypes = droneTypes;
        return this;
    }
    
    public AvaliableDronesBuilder withDroneCounts(Map<String, Integer> counts) {
        underConstruction.droneCounts = counts;
        return this;
    }
    
    public AvaliableDronesBuilder withDroneCount(String droneTypes, int count) {
        underConstruction.droneCounts.put(droneTypes, count);
        return this;
    }
    
    public AvaliableDronesBuilder withDrone(Drone drone) {
        int count = underConstruction.droneCounts.get(drone.getDroneType());
        underConstruction.droneCounts.put(drone.getDroneType(), ++count);
        return this;
    }
    
    public AvaliableDronesBuilder withNoDroneAvaliable() {
        withDroneCounts(new HashMap<String, Integer>());
        for (String droneType : underConstruction.droneTypes) {
            withDroneCount(droneType, 0);
        }
        return this;
    }
    
    public AvaliableDrones build() {
        AvaliableDrones builded = underConstruction;
        underConstruction = new AvaliableDrones(Arrays.asList("T4 v1", "T8 v1"), new HashMap<String, Integer>());
        return builded;
    }
    
}
