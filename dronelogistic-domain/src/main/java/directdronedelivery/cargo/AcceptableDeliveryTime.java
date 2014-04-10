package directdronedelivery.cargo;

import java.util.BitSet;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import org.joda.time.DateTime;

@EqualsAndHashCode
@ToString
public class AcceptableDeliveryTime {
    
    protected BitSet hours;
    protected BitSet halves;
    
    protected AcceptableDeliveryTime() {
        hours = new BitSet(24);
        halves = new BitSet(24);
    }
    
    public boolean isInAcceptableTime(DateTime time) {
        int minute = time.getMinuteOfHour();
        int hour = time.getHourOfDay();
        if (minute < 30) {
            return hours.get(hour);
        } else {
            return halves.get(hour);
        }
    }
    
}
