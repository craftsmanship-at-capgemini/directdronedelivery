package directdronedelivery.warehouse.process;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class VesselChooseProcessWarehouseState {
    
    private boolean manuallFlightCancellation = false;
    private boolean weatherAcceptable = false;
    
    public boolean arePositive() {
        return !manuallFlightCancellation && weatherAcceptable;
    }
    
    protected void cancelFlights() {
        manuallFlightCancellation = true;
    }
    
    protected void allowFlights() {
        manuallFlightCancellation = false;
    }
    
    public boolean isWeatherAcceptable() {
        return weatherAcceptable;
    }
    
    public void setWeatherAcceptable(boolean weatherAcceptable) {
        this.weatherAcceptable = weatherAcceptable;
    }
    
}
