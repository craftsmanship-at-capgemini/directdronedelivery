package dronelogistic.dronflightcontrol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class AvailableDronesBuilder {
    
    private static AtomicInteger nextDroneID = new AtomicInteger(0);
    
    private AvaliableDrones underConstruction = null;
    
    private List<DroneType> typicalDroneTypes = Arrays.asList(DroneType.values());
    
    private AvailableDronesBuilder() {
    }
    
    public static Drone newDrone(int droneID, DroneType droneType) {
        return new Drone(droneID, droneType);
    }
    
    public static Drone newDrone(DroneType droneType) {
        return new Drone(nextDroneID.incrementAndGet(), droneType);
    }
    
    public static Drone newDrone() {
        return new Drone(nextDroneID.incrementAndGet(), DroneType.SMALL_FOUR_ROTORS);
    }
    
    public static AvailableDronesBuilder anAvaliableDrones() {
        AvailableDronesBuilder builder = new AvailableDronesBuilder();
        builder.underConstruction = new AvaliableDrones(new ArrayList<DroneType>(), new HashMap<DroneType, Integer>());
        return builder;
    }
    
    public AvailableDronesBuilder likeNoDronesAvaliable() {
        withDroneTypes(typicalDroneTypes);
        withNoDroneAvaliable();
        return this;
    }
    
    public AvailableDronesBuilder but() {
        return this;
    }
    
    public AvailableDronesBuilder withDroneTypes(List<DroneType> typicalDroneTypes) {
        underConstruction.droneTypes = typicalDroneTypes;
        return this;
    }
    
    public AvailableDronesBuilder withDroneCounts(Map<DroneType, Integer> counts) {
        underConstruction.droneCounts = counts;
        return this;
    }
    
    public AvailableDronesBuilder withDroneCount(DroneType droneTypes, int count) {
        underConstruction.droneCounts.put(droneTypes, count);
        return this;
    }
    
    public AvailableDronesBuilder withDrone(Drone drone) {
        int count = underConstruction.droneCounts.get(drone.getDroneType());
        underConstruction.droneCounts.put(drone.getDroneType(), ++count);
        return this;
    }
    
    public AvailableDronesBuilder withNoDroneAvaliable() {
        withDroneCounts(new HashMap<DroneType, Integer>());
        for (DroneType droneType : underConstruction.droneTypes) {
            withDroneCount(droneType, 0);
        }
        return this;
    }
    
    public AvaliableDrones build() {
        AvaliableDrones builded = underConstruction;
        underConstruction = new AvaliableDrones(typicalDroneTypes, new HashMap<DroneType, Integer>());
        return builded;
    }
    
}