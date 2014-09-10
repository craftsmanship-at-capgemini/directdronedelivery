package directdronedelivery.warehouse.businessrules;

import directdronedelivery.cargo.CargoAggregate;
import directdronedelivery.drone.DroneAggregate;
import directdronedelivery.warehouse.BoxType;

public class BoxChooseSpecification {
    
    public BoxChooseSpecification(CargoAggregate cargo, DroneAggregate drone) {
        
    }
    
    public BoxType preferredBoxTyp() {
        return null;
    }
    
}
