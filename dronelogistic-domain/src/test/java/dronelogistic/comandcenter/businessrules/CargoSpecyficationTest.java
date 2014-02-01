package dronelogistic.comandcenter.businessrules;

import static dronelogistic.orderinformations.OrderAndCargoInformationBuilder.aCargo;
import static org.fest.assertions.api.Assertions.assertThat;

import java.util.List;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import testing.Testing;
import dronelogistic.orderinformations.OrderAndCargoInformation;

public class CargoSpecyficationTest {
    
    private static final int COUNT_OF_DRONE_TYPES = 2;
    
    @Inject CargoSpecyfication cargoSpecyfication;
    
    @Before
    public void setUp() throws Exception {
        Testing.inject(this);
    }
    
    // 1. CargoSpecyfication Warunki techniczne Paczki (Cargo) umożliwiają
    // wysłanie jej Dronem (Vessel)
    // - Paczka (Cargo) ma wagę do 5kg
    // - Maksymalne wymiary długość/szerokość/wysokość umożliwiają dostarczenie
    // paczki Dronem (Vessel)
    // - Towar (Commodity) jest odpowiednio odporny na wstrząsy (np. może być
    // problem z przewożeniem szkła)
    // - Towar (Commodity) nie jest niebezpieczny ani łatwopalny (tzw. Dangerous
    // Goods np. 2l kanisterek benzyny może być problematyczny)
    
    @Test
    public void shouldNotAllowCargoWeighingOver5Kilos() {
        OrderAndCargoInformation orderAndCargoInformation = aCargo().likeSmallGift()
                .but().withWeightInKilos(6).build();
        List<String> possibleDronTypes = cargoSpecyfication.possibleDronTypes(orderAndCargoInformation);
        
        assertThat(possibleDronTypes).isEmpty();
    }
    
    @Test
    public void shouldAllowCargoWeighingExactly5Kilos() {
        OrderAndCargoInformation orderAndCargoInformation = aCargo().likeSmallGift()
                .but().withWeightInKilos(6).build();
        List<String> possibleDronTypes = cargoSpecyfication.possibleDronTypes(orderAndCargoInformation);
        
        assertThat(possibleDronTypes).hasSize(COUNT_OF_DRONE_TYPES);
    }
    
    @Test
    public void shouldAllowCargoWeighingUnder5Kilos() {
        int[] someWeightsInGrams = { 0, 1, 2, 10, 42, 50, 120, 550, 666, 999, 1000, 1001, 2500, 3000, 3333, 4242, 4999 };
        for (int weightInGrams : someWeightsInGrams) {
            OrderAndCargoInformation orderAndCargoInformation = aCargo().likeSmallGift()
                    .but().withWeightInGrams(weightInGrams).build();
            List<String> possibleDronTypes = cargoSpecyfication.possibleDronTypes(orderAndCargoInformation);
            
            assertThat(possibleDronTypes).hasSize(COUNT_OF_DRONE_TYPES);
        }
    }
    
}
