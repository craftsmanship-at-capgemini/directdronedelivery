package dronelogistic.orderinformations;

import static org.fest.assertions.api.Assertions.assertThat;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.Test;

public class AcceptableDeliveryTimeTest {
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");
    
    @Before
    public void setUp() throws Exception {
    }
    
    @Test
    public void shouldAllowTimeInInterval() {
        AcceptableDeliveryTime acceptableTime = AcceptableDeliveryTimeBuilder.aTime()
                .addInterval("16:30-22:30").build();
        
        String[] times = {
                "16:30", "16:31", "16:50", "17:00",
                "17:30", "18:00", "18:59", "19:01",
                "21:59", "22:00", "22:13", "22:29",
        };
        for (String time : times) {
            DateTime dateTime = FORMATTER.parseDateTime("2014-02-02 " + time);
            
            assertThat(acceptableTime.isInAcceptableTime(dateTime))
                    .describedAs("" + time + " should be in " + acceptableTime).isTrue();
        }
    }
    
    @Test
    public void shouldDisallowTimeOutsideInterval() {
        AcceptableDeliveryTime acceptableTime = AcceptableDeliveryTimeBuilder.aTime()
                .addInterval("16:30-22:30").build();
        
        String[] times = {
                "00:00", "16:00", "16:10", "16:29",
                "22:31", "22:50", "23:00", "23:59",
        };
        for (String time : times) {
            DateTime dateTime = FORMATTER.parseDateTime("2014-02-02 " + time);
            
            assertThat(acceptableTime.isInAcceptableTime(dateTime))
                    .describedAs("" + time + " should be NOT in " + acceptableTime).isFalse();
        }
    }
    
    @Test
    public void shouldAllowTimeInIntervalTillMidnight() {
        AcceptableDeliveryTime acceptableTime = AcceptableDeliveryTimeBuilder.aTime()
                .addInterval("16:30-24:00").build();
        
        String[] times = {
                "22:59", "23:00", "23:13", "23:30", "23:59",
        };
        for (String time : times) {
            DateTime dateTime = FORMATTER.parseDateTime("2014-02-02 " + time);
            
            assertThat(acceptableTime.isInAcceptableTime(dateTime))
                    .describedAs("" + time + " should be in " + acceptableTime).isTrue();
        }
    }
    
    @Test
    public void shouldAllowTimeDuringNight() {
        AcceptableDeliveryTime acceptableTime = AcceptableDeliveryTimeBuilder.aTime()
                .addInterval("22:30-24:00").addInterval("00:00-06:00").build();
        
        String[] times = {
                "22:30", "22:59", "23:00", "23:13", "23:30", "23:59",
                "00:00", "00:01", "00:30", "01:00", "05:00", "05:59",
        };
        for (String time : times) {
            DateTime dateTime = FORMATTER.parseDateTime("2014-02-02 " + time);
            
            assertThat(acceptableTime.isInAcceptableTime(dateTime))
                    .describedAs("" + time + " should be in " + acceptableTime).isTrue();
        }
    }
}
