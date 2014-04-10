package directdronedelivery.drone.management;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import directdronedelivery.drone.DroneAggregate;
import directdronedelivery.drone.DroneType;
import directdronedelivery.drone.management.AvailableDrones;

public class AvailableDronesBuilder {
    
    private static AtomicInteger nextDroneID = new AtomicInteger(0);
    
    private AvailableDrones underConstruction = null;
    
    private List<DroneType> typicalDroneTypes = Arrays.asList(DroneType.values());
    
    private AvailableDronesBuilder() {
    }
    
    public static DroneAggregate newDrone(int droneID, DroneType droneType) {
        return new DroneAggregate(droneID, droneType);
    }
    
    public static DroneAggregate newDrone(DroneType droneType) {
        return new DroneAggregate(nextDroneID.incrementAndGet(), droneType);
    }
    
    public static DroneAggregate newDrone() {
        return new DroneAggregate(nextDroneID.incrementAndGet(), DroneType.SMALL_FOUR_ROTORS);
    }
    
    public static AvailableDronesBuilder anAvaliableDrones() {
        AvailableDronesBuilder builder = new AvailableDronesBuilder();
        builder.underConstruction = new AvailableDrones(new ArrayList<DroneType>(), new HashMap<DroneType, Integer>());
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
    
    public AvailableDronesBuilder withDrone(DroneAggregate drone) {
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
    
    public AvailableDrones build() {
        AvailableDrones builded = underConstruction;
        underConstruction = new AvailableDrones(typicalDroneTypes, new HashMap<DroneType, Integer>());
        return builded;
    }
    
}