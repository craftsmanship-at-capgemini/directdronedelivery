package directdronedelivery.cargo;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
@AllArgsConstructor
public class OrderUpdatedEvent {
    
    @Getter private Integer cargoID;
    
}
