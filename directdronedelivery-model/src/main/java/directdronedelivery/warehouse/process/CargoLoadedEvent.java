package directdronedelivery.warehouse.process;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
@AllArgsConstructor
public class CargoLoadedEvent {
    
    @Getter private Integer droneID;
    @Getter private Integer cargoID;
    
}
