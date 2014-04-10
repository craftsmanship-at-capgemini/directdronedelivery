package directdronedelivery.drone.management.communication;

import directdronedelivery.drone.DroneType;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class CheckStartList {
    
    boolean isBatteryConditionFulfilled = true;
    boolean isWindConditionFulfilled = true;
    boolean isMinBatteryChargeFulfilled = true;
    boolean batteryCheck;
    boolean windCheck;
    int minBatteryCharge;
    
    public static CheckStartList newCheckStartList(DroneType droneType) {
        CheckStartList startList = new CheckStartList();
        if (DroneType.SMALL_FOUR_ROTORS == droneType) {
            startList.batteryCheck = true;
            startList.windCheck = true;
            startList.minBatteryCharge = 60;
        } else if (DroneType.BIG_SIX_ROTORS == droneType) {
            startList.batteryCheck = true;
            startList.minBatteryCharge = 80;
        }
        return startList;
    }
    
    public CheckStartList checkConditons() {
        if (batteryCheck)
            checkBattery();
        if (windCheck)
            checkWind();
        return this;
    }
    
    private void checkBattery() {
        isBatteryConditionFulfilled = true;
    }
    
    private void checkWind() {
        isWindConditionFulfilled = true;
    }
    
    public boolean startConditionsFullfilled() {
        return isBatteryConditionFulfilled && isWindConditionFulfilled && isMinBatteryChargeFulfilled;
    }
    
}
