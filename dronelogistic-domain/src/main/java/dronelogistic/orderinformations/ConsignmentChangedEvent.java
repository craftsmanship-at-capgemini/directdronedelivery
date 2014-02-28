package dronelogistic.orderinformations;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
@AllArgsConstructor
public class ConsignmentChangedEvent {
    
    @Getter private Integer consignmentID;
    
}
