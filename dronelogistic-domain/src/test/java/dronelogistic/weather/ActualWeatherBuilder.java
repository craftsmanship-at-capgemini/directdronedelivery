package dronelogistic.weather;

public class ActualWeatherBuilder {
    
    private ActualWeather underConstruction = null;
    
    public static ActualWeatherBuilder aWeather() {
        ActualWeatherBuilder builder = new ActualWeatherBuilder();
        builder.underConstruction = new ActualWeather();
        return builder;
    }
    
    public ActualWeatherBuilder likeNiceWeather() {
        withWindInMetersPerSecond(0.8);
        
        return this;
    }
    
    public ActualWeatherBuilder but() {
        return this;
    }
    
    public ActualWeatherBuilder withWindInMetersPerSecond(double windInMPS) {
        underConstruction.wind = windInMPS;
        return this;
    }
    
    public ActualWeather build() {
        ActualWeather builded = underConstruction;
        underConstruction = new ActualWeather();
        return builded;
    }
    
    public ActualWeatherBuilder withLightningsPossible(boolean lightningsPossible) {
        underConstruction.lightningsPossible = lightningsPossible;
        return this;
    }
    
    public ActualWeatherBuilder withPrecipitationPossible(boolean precipitationPossible) {
        underConstruction.precipitationPossible = precipitationPossible;
        return this;
    }
    
}
