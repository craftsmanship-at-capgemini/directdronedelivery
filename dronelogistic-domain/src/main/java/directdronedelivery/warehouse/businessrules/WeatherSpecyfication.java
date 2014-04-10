package directdronedelivery.warehouse.businessrules;

import directdronedelivery.weather.Weather;

public class WeatherSpecyfication {
    
    private static final double MAXIMAL_WIND_IN_MPS = 5.56;
    private static final int MAXIMAL_TEMPERATURE_C = 30;
    private static final int MINIMAL_TEMPERATURE_C = 10;
    private static final int MAXIMAL_HUMIDITY = 55;
    
    public boolean isSatisfiedBy(Weather weather) {
        
        if (weather.getWindInPMS() > MAXIMAL_WIND_IN_MPS) {
            return false;
        }
        
        if (weather.isLightningsPossible()) {
            return false;
        }
        
        if (weather.isPrecipitationPossible()) {
            return false;
        }
        
        if (weather.getTemperatureInCelsius() < MINIMAL_TEMPERATURE_C
                || MAXIMAL_TEMPERATURE_C < weather.getTemperatureInCelsius()) {
            return false;
        }
        
        if (weather.getHumidityInPercent() > MAXIMAL_HUMIDITY) {
            return false;
        }
        
        return true;
    }
}
