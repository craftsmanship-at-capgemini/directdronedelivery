package dronelogistic.orderinformations;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AcceptableDeliveryTimeBuilder {
    
    private AcceptableDeliveryTime underConstruction;
    
    private AcceptableDeliveryTimeBuilder() {
    }
    
    public static AcceptableDeliveryTimeBuilder aTime() {
        AcceptableDeliveryTimeBuilder builder = new AcceptableDeliveryTimeBuilder();
        builder.underConstruction = new AcceptableDeliveryTime();
        return builder;
    }
    
    public AcceptableDeliveryTimeBuilder addInterval(String interval) {
        Pattern pattern = Pattern.compile("([0-9]{1,2}):([0-9]{2}) *- *([0-9]{1,2}):([0-9]{2})");
        Matcher matcher = pattern.matcher(interval.trim());
        
        if (!matcher.matches() || matcher.groupCount() != 4) {
            throw new IllegalArgumentException("Only intervals in format: 'HH:mm-HH:mm' are alloved ex: 17:30-23:00");
        }
        
        int start_h = Integer.parseInt(matcher.group(1));
        int start_m = Integer.parseInt(matcher.group(2));
        int end_h = Integer.parseInt(matcher.group(3));
        int end_m = Integer.parseInt(matcher.group(4));
        
        if (!(0 <= start_h && start_h <= 23)) {
            throw new IllegalArgumentException("Start houer need be in [0 23]");
        }
        if (!(0 <= start_m && start_m < 60)) {
            throw new IllegalArgumentException("Start minute need be in [0 59]");
        }
        if (!(0 <= end_h && end_h <= 24)) {
            throw new IllegalArgumentException("End houer need be in [0 24]");
        }
        if (!(0 <= end_m && end_m < 60)) {
            throw new IllegalArgumentException("End minute need be in [0 59]");
        }
        if (!(start_h * 60 + start_m < end_h * 60 + end_m)) {
            throw new IllegalArgumentException("Start time need be before end time");
        }
        if (!(end_h * 60 + end_m <= 24 * 60)) {
            throw new IllegalArgumentException("End time need be before 24:00");
        }
        
        if (start_m == 0) {
            underConstruction.hours.set(start_h, true);
        }
        if (start_m <= 30) {
            underConstruction.halves.set(start_h, true);
        }
        for (int h = start_h + 1; h < end_h; h++) {
            underConstruction.hours.set(h, true);
            underConstruction.halves.set(h, true);
        }
        if (end_m >= 30 || end_h == 24) {
            underConstruction.hours.set(end_h, true);
        }
        if (end_h == 24) {
            underConstruction.halves.set(end_h, true);
        }
        return this;
    }
    
    public AcceptableDeliveryTime build() {
        AcceptableDeliveryTime builded = underConstruction;
        underConstruction = new AcceptableDeliveryTime();
        return builded;
    }
}
