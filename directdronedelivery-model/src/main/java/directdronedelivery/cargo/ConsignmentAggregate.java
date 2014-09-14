package directdronedelivery.cargo;

import java.util.Collections;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode(of = "consignmentID")
@ToString
public class ConsignmentAggregate {
    
    @Getter protected Integer consignmentID;
    @Getter protected List<CargoAggregate> cargosInConsignment;
    
    protected ConsignmentAggregate() {
    }
    
    protected ConsignmentAggregate(Integer consignmentID, List<CargoAggregate> cargosInConsignment) {
        this.consignmentID = consignmentID;
        this.cargosInConsignment = Collections.unmodifiableList(cargosInConsignment);
    }
    
}
