package directdronedelivery.warehouse.businessrules;

import static org.fest.assertions.api.Assertions.assertThat;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import testing.Testing;
import directdronedelivery.warehouse.businessrules.WeatherSpecyfication;
import directdronedelivery.weather.Weather;
import directdronedelivery.weather.WeatherBuilder;

public class WeatherSpecyficationTest {
    
    @Inject WeatherSpecyfication weatherSpecyfication;
    
    @Before
    public void setUp() throws Exception {
        Testing.inject(this);
    }
    
    @Test
    public void shouldAcceptWeakWind() {
        Weather weather = WeatherBuilder.aWeather().likeNiceWeather()
                .but().withWindInMetersPerSecond(3.0).build();
        
        boolean weatherAcceptable = weatherSpecyfication.isAcceptable(weather);
        
        assertThat(weatherAcceptable).isTrue();
    }
    
    @Test
    public void shouldAcceptMaximalWindStrength() {
        Weather weather = WeatherBuilder.aWeather().likeNiceWeather()
                .but().withWindInMetersPerSecond(5.56).build();
        
        boolean weatherAcceptable = weatherSpecyfication.isAcceptable(weather);
        
        assertThat(weatherAcceptable).isTrue();
    }
    
    @Test
    public void shouldAcceptStrongWind() {
        Weather weather = WeatherBuilder.aWeather().likeNiceWeather()
                .but().withWindInMetersPerSecond(6.0).build();
        
        boolean weatherAcceptable = weatherSpecyfication.isAcceptable(weather);
        
        assertThat(weatherAcceptable).isFalse();
    }
    
    @Test
    public void shouldAcceptWeatherWhennLightningsArePossible() {
        Weather weather = WeatherBuilder.aWeather().likeNiceWeather()
                .but().withLightningsPossible(true).build();
        
        boolean weatherAcceptable = weatherSpecyfication.isAcceptable(weather);
        
        assertThat(weatherAcceptable).isFalse();
    }
    
    @Test
    public void shouldNotAcceptPrecipitation() {
        Weather weather = WeatherBuilder.aWeather().likeNiceWeather()
                .but().withPrecipitationPossible(true).build();
        
        boolean weatherAcceptable = weatherSpecyfication.isAcceptable(weather);
        
        assertThat(weatherAcceptable).isFalse();
    }
    
    @Test
    public void shouldAcceptWeatherWihtNoLightningsAndPrecipitation() {
        Weather weather = WeatherBuilder.aWeather().likeNiceWeather()
                .but().withLightningsPossible(false).withPrecipitationPossible(false).build();
        
        boolean weatherAcceptable = weatherSpecyfication.isAcceptable(weather);
        
        assertThat(weatherAcceptable).isTrue();
    }
    
    @Test
    public void shouldAcceptTemperatureInAcceptableRange() {
        Weather weather = WeatherBuilder.aWeather().likeNiceWeather()
                .but().withTemperatureInCelsius(15).build();
        
        boolean weatherAcceptable = weatherSpecyfication.isAcceptable(weather);
        
        assertThat(weatherAcceptable).isTrue();
    }
    
    @Test
    public void shouldAcceptTemperatureOnLowerBoundry() {
        Weather weather = WeatherBuilder.aWeather().likeNiceWeather()
                .but().withTemperatureInCelsius(10).build();
        
        boolean weatherAcceptable = weatherSpecyfication.isAcceptable(weather);
        
        assertThat(weatherAcceptable).isTrue();
    }
    
    @Test
    public void shouldAcceptTemperatureOnUpperBoundry() {
        Weather weather = WeatherBuilder.aWeather().likeNiceWeather()
                .but().withTemperatureInCelsius(30).build();
        
        boolean weatherAcceptable = weatherSpecyfication.isAcceptable(weather);
        
        assertThat(weatherAcceptable).isTrue();
    }
    
    @Test
    public void shouldNotAcceptTemperatureUnderLowerBoundry() {
        Weather weather = WeatherBuilder.aWeather().likeNiceWeather()
                .but().withTemperatureInCelsius(9).build();
        
        boolean weatherAcceptable = weatherSpecyfication.isAcceptable(weather);
        
        assertThat(weatherAcceptable).isFalse();
    }
    
    @Test
    public void shouldNotAcceptTemperatureOverUpperBoundry() {
        Weather weather = WeatherBuilder.aWeather().likeNiceWeather()
                .but().withTemperatureInCelsius(31).build();
        
        boolean weatherAcceptable = weatherSpecyfication.isAcceptable(weather);
        
        assertThat(weatherAcceptable).isFalse();
    }
    
    @Test
    public void shouldAcceptWeatherWithLowHumidity() {
        Weather weather = WeatherBuilder.aWeather().likeNiceWeather()
                .but().withHumidityInPercent(30).build();
        
        boolean weatherAcceptable = weatherSpecyfication.isAcceptable(weather);
        
        assertThat(weatherAcceptable).isTrue();
    }
    
    @Test
    public void shouldAcceptWeatherWithMaximalHumidity() {
        Weather weather = WeatherBuilder.aWeather().likeNiceWeather()
                .but().withHumidityInPercent(55).build();
        
        boolean weatherAcceptable = weatherSpecyfication.isAcceptable(weather);
        
        assertThat(weatherAcceptable).isTrue();
    }
    
    @Test
    public void shouldNotAcceptWeatherWithHighHumidity() {
        Weather weather = WeatherBuilder.aWeather().likeNiceWeather()
                .but().withHumidityInPercent(60).build();
        
        boolean weatherAcceptable = weatherSpecyfication.isAcceptable(weather);
        
        assertThat(weatherAcceptable).isFalse();
    }
    
}
