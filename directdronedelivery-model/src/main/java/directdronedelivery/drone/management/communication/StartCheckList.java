package directdronedelivery.drone.management.communication;

import directdronedelivery.drone.DroneType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class StartCheckList {
    
    @Getter boolean sensorsCheck;
    @Getter boolean rottorsCheck;
    @Getter boolean pylonsCheck;
    @Getter boolean batteryCheck;
    @Getter int minBatteryCharge;
    
    public static StartCheckList newStartCheckList(DroneType droneType) {
        StartCheckList checkList = new StartCheckList();
        checkList.sensorsCheck = true;
        checkList.rottorsCheck = true;
        checkList.pylonsCheck = true;
        
        checkList.batteryCheck = true;
        switch (droneType) {
            case QUADROCOPTER:
                checkList.minBatteryCharge = 60;
            break;
        case HEXACOPTER:
            checkList.minBatteryCharge = 80;
            break;
        }
        
        return checkList;
    }
    
}
