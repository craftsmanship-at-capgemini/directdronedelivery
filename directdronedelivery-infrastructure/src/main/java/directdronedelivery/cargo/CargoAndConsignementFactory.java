package directdronedelivery.cargo;

import java.util.List;

public class CargoAndConsignementFactory {

    public ConsignmentAggregate createConsignment(Integer consignmentID,
            List<CargoAggregate> cargosInConsignment) {
        ConsignmentAggregate consignment = new ConsignmentAggregate(consignmentID, cargosInConsignment);
        return consignment;
    }
    
}
