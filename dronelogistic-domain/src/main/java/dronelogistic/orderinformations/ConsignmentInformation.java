package dronelogistic.orderinformations;

import java.util.Collections;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class ConsignmentInformation {
    
    @Getter protected int consignmentID;
    protected List<OrderAndCargoInformation> cargosInConsignment;
    
    public List<OrderAndCargoInformation> getCargosInConsignment() {
        return Collections.unmodifiableList(cargosInConsignment);
    }
    
    ConsignmentInformation() {
    }
}
