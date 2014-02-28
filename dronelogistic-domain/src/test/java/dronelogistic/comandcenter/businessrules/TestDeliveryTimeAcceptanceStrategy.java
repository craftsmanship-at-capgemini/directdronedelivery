package dronelogistic.comandcenter.businessrules;

import javax.enterprise.inject.Alternative;

import lombok.ToString;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import dronelogistic.orderinformations.AcceptableDeliveryTime;

@ToString
@Alternative
public class TestDeliveryTimeAcceptanceStrategy extends DeliveryTimeAcceptanceStrategy {
    
    private DateTime now = DateTime.now();
    
    public static Configurator configure(DeliveryTimeAcceptanceStrategy instance) {
        return ((TestDeliveryTimeAcceptanceStrategy) instance).new Configurator();
    }
    
    public class Configurator {
        public Configurator withFixedCurrentTime(DateTime now) {
            TestDeliveryTimeAcceptanceStrategy.this.now = now;
            return this;
        }
        
        public Configurator withFixedCurrentTime(String HHmm) {
            TestDeliveryTimeAcceptanceStrategy.this.now = DateTime.parse(HHmm, DateTimeFormat.forPattern("HH:mm"));
            return this;
        }
    }
    
    public boolean isPositive(AcceptableDeliveryTime acceptableDeliveryTime) {
        return acceptableDeliveryTime.isInAcceptableTime(now);
    }
    
    public DateTime getCurrentTime() {
        return now;
    }
    
}
