package directdronedelivery.cargo;

import java.util.Collections;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class ConsignmentInformation {
    
    @Getter protected int consignmentID;
    protected List<CargoAggregate> cargosInConsignment;
    
    public List<CargoAggregate> getCargosInConsignment() {
        return Collections.unmodifiableList(cargosInConsignment);
    }
    
    ConsignmentInformation() {
    }
}
