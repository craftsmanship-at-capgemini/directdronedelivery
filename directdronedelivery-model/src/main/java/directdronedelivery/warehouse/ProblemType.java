package directdronedelivery.warehouse;

import lombok.Getter;

public enum ProblemType {
    
    DATA_UPLOAD_IMPOSSIBLE(false, true),
    PYLON_JAMMED(false, true),
    BATTERY_LOW(false, true),
    ROTORS_JAMMED(false, true),
    NO_BOXES(true, true),
    CARGO_MISSING(true, false),
    DRONE_MISSING(false, true),
    OTHER_TECHNICAL(false, true);
    
    ProblemType(boolean isDroneOperational, boolean isCargoDeliverableWithDrone) {
        this.isDroneOperational = isDroneOperational;
        this.isCargoDeliverableWithDrone = isCargoDeliverableWithDrone;
    }
    
    @Getter private boolean isDroneOperational;
    @Getter private boolean isCargoDeliverableWithDrone;
    
}
