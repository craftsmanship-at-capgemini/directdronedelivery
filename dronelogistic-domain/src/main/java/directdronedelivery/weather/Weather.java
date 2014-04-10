package directdronedelivery.weather;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class Weather {
    
    @Getter protected double windInPMS;
    @Getter protected boolean lightningsPossible;
    @Getter protected boolean precipitationPossible;
    @Getter protected int temperatureInCelsius;
    @Getter protected int humidityInPercent;
    
}
