package directdronedelivery.cargo;

import java.util.Collections;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode(of = "consignmentID")
@ToString
public class ConsignmentAggregate {
    
    @Getter protected int consignmentID;
    protected List<CargoAggregate> cargosInConsignment;
    
    protected ConsignmentAggregate() {
    }
    
    public List<CargoAggregate> getCargosInConsignment() {
        return Collections.unmodifiableList(cargosInConsignment);
    }
}
