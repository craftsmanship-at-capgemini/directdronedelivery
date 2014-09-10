package directdronedelivery.warehouse.businessrules;

import static directdronedelivery.warehouse.BoxType.BIG;
import static directdronedelivery.warehouse.BoxType.BIG_FRAGILE;
import static directdronedelivery.warehouse.BoxType.SMALL;
import static directdronedelivery.warehouse.BoxType.SMALL_FRAGILE;
import directdronedelivery.cargo.CargoAggregate;
import directdronedelivery.drone.DroneAggregate;
import directdronedelivery.drone.DroneType;
import directdronedelivery.warehouse.BoxType;

public class BoxChooseSpecification {
    
    private boolean fragileCommodity;
    private DroneType droneType;
    
    public BoxChooseSpecification(CargoAggregate cargo, DroneAggregate drone) {
        fragileCommodity = cargo.isFragileCommodity();
        droneType = drone.getDroneType();
    }
    
    public BoxType preferredBoxTyp() {
        switch (droneType) {
        case BIG_SIX_ROTORS:
            if (fragileCommodity) {
                return BIG_FRAGILE;
            } else {
                return BIG;
            }
        case SMALL_FOUR_ROTORS:
            if (fragileCommodity) {
                return SMALL_FRAGILE;
            } else {
                return SMALL;
            }
        }
        throw new AssertionError("Drone type: " + droneType + " is unsuported in current BoxChooseSpecification");
    }
    
}
