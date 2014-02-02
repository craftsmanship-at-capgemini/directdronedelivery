package dronelogistic.comandcenter.businessrules;

import dronelogistic.weather.ActualWeather;

public class WeatherSpecyfication {
    
    private static final double MAXIMAL_WIND_IN_MPS = 5.56;
    
    public boolean isAcceptable(ActualWeather actualWeather) {
        
        if (actualWeather.getWind() > MAXIMAL_WIND_IN_MPS) {
            return false;
        }
        
        return true;
    }
    
}
