package dronelogistic.comandcenter.businessrules;

import static org.fest.assertions.api.Assertions.assertThat;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import testing.Testing;
import dronelogistic.weather.ActualWeather;
import dronelogistic.weather.ActualWeatherBuilder;

public class WeatherSpecyficationTest {
    
    // 5. WeatherSpecyfication Warunki pogodowe umożliwiają wysłanie Paczki
    // (Cargo) Dronem (Vessel)
    // - siła wiatru jest mniejsza niż 20km/h
    // - brak wyładowań atmosferycznych (burzy)
    // - brak deszczu
    // - brak gradu
    // - brak śniegu
    // - temperatura powietrza jest wyższa niż 10 stopni i niższa niż 30 stopni
    // - wilgotność powietrza nie przekracza 55%
    
    @Inject WeatherSpecyfication weatherSpecyfication;
    
    @Before
    public void setUp() throws Exception {
        Testing.inject(this);
    }
    
    @Test
    public void shouldAcceptWeakWind() {
        ActualWeather weather = ActualWeatherBuilder.aWeather().likeNiceWeather()
                .but().withWindInMetersPerSecond(3.0).build();
        
        boolean weatherAcceptable = weatherSpecyfication.isAcceptable(weather);
        
        assertThat(weatherAcceptable).isTrue();
    }
    
    @Test
    public void shouldAcceptMaximalWindStrength() {
        ActualWeather weather = ActualWeatherBuilder.aWeather().likeNiceWeather()
                .but().withWindInMetersPerSecond(5.56).build();
        
        boolean weatherAcceptable = weatherSpecyfication.isAcceptable(weather);
        
        assertThat(weatherAcceptable).isTrue();
    }
    
    @Test
    public void shouldAcceptStrongWind() {
        ActualWeather weather = ActualWeatherBuilder.aWeather().likeNiceWeather()
                .but().withWindInMetersPerSecond(6.0).build();
        
        boolean weatherAcceptable = weatherSpecyfication.isAcceptable(weather);
        
        assertThat(weatherAcceptable).isFalse();
    }
    
    @Test
    public void shouldAcceptWeatherWhennLightningsArePossible() {
        ActualWeather weather = ActualWeatherBuilder.aWeather().likeNiceWeather()
                .but().withLightningsPossible(true).build();
        
        boolean weatherAcceptable = weatherSpecyfication.isAcceptable(weather);
        
        assertThat(weatherAcceptable).isFalse();
    }
    
    @Test
    public void shouldNotAcceptPrecipitation() {
        ActualWeather weather = ActualWeatherBuilder.aWeather().likeNiceWeather()
                .but().withPrecipitationPossible(true).build();
        
        boolean weatherAcceptable = weatherSpecyfication.isAcceptable(weather);
        
        assertThat(weatherAcceptable).isFalse();
    }
    
    @Test
    public void shouldAcceptWeatherWihtNoLightningsAndPrecipitation() {
        ActualWeather weather = ActualWeatherBuilder.aWeather().likeNiceWeather()
                .but().withLightningsPossible(false).withPrecipitationPossible(false).build();
        
        boolean weatherAcceptable = weatherSpecyfication.isAcceptable(weather);
        
        assertThat(weatherAcceptable).isTrue();
    }
    
}
