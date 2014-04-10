package directdronedelivery.warehouse.businessrules;

import org.joda.time.DateTime;

import directdronedelivery.cargo.AcceptableDeliveryTime;

public class DeliveryTimeAcceptanceStrategy {
    
    public boolean isPositive(AcceptableDeliveryTime acceptableDeliveryTime) {
        return acceptableDeliveryTime.isInAcceptableTime(DateTime.now());
    }
    
    public DateTime getCurrentTime() {
        return DateTime.now();
    }
    
}
