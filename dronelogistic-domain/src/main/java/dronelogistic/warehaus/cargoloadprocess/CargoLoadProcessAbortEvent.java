package dronelogistic.warehaus.cargoloadprocess;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
@AllArgsConstructor
public class CargoLoadProcessAbortEvent {
    
    @Getter Integer droneID;
    @Getter Integer cargoID;
    @Getter Integer boxID;
    
}
