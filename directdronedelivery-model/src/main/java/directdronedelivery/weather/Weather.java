package directdronedelivery.weather;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
@AllArgsConstructor
public class Weather {
    
    @Getter protected double windInPMS;
    @Getter protected boolean lightningsPossible;
    @Getter protected boolean precipitationPossible;
    @Getter protected int temperatureInCelsius;
    @Getter protected int humidityInPercent;
    
    protected Weather() {
    }
}
