package directdronedelivery.warehouse.businessrules;

import static directdronedelivery.cargo.OrderAndCargoInformationBuilder.aCargo;
import static org.fest.assertions.api.Assertions.assertThat;

import java.util.List;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import testing.Testing;
import directdronedelivery.cargo.CargoAggregate;
import directdronedelivery.drone.DroneType;
import directdronedelivery.warehouse.businessrules.CargoSpecyfication;

public class CargoSpecyficationTest {
    
    private static final int COUNT_OF_DRONE_TYPES = DroneType.values().length;
    
    @Inject CargoSpecyfication cargoSpecyfication;
    
    @Before
    public void setUp() throws Exception {
        Testing.inject(this);
    }
    
    @Test
    public void shouldNotAcceptCargoWeighingOver5Kilos() {
        CargoAggregate cargo = aCargo().likeSmallGift()
                .but().withWeightInKilos(6).build();
        
        List<DroneType> possibleDronTypes = cargoSpecyfication.isSatisfiedForDronTypes(cargo);
        
        assertThat(possibleDronTypes).isEmpty();
    }
    
    @Test
    public void shouldAcceptCargoWeighingExactly5Kilos() {
        CargoAggregate cargo = aCargo().likeSmallGift()
                .but().withWeightInKilos(5).build();
        
        List<DroneType> possibleDronTypes = cargoSpecyfication.isSatisfiedForDronTypes(cargo);
        
        assertThat(possibleDronTypes).hasSize(COUNT_OF_DRONE_TYPES);
    }
    
    @Test
    public void shouldAcceptCargoWeighingUnder5Kilos() {
        int[] someWeightsInGrams = { 0, 1, 2, 10, 42, 50, 120, 550, 666, 999, 1000, 1001, 2500, 3000, 3333, 4242, 4999 };
        for (int weightInGrams : someWeightsInGrams) {
            CargoAggregate cargo = aCargo().likeSmallGift()
                    .but().withWeightInGrams(weightInGrams).build();
            
            List<DroneType> possibleDronTypes = cargoSpecyfication.isSatisfiedForDronTypes(cargo);
            
            assertThat(possibleDronTypes).hasSize(COUNT_OF_DRONE_TYPES);
        }
    }
    
    @Test
    public void shouldNotAcceptToBigCargoSize() {
        CargoAggregate cargo = aCargo().likeSmallGift()
                .but().withSizeInMilimeters(10, 1000, 10).withFixedOrientation(false).build();
        
        List<DroneType> possibleDronTypes = cargoSpecyfication.isSatisfiedForDronTypes(cargo);
        
        assertThat(possibleDronTypes).isEmpty();
    }
    
    @Test
    public void shouldAcceptMaximalCargoSize() {
        CargoAggregate cargo = aCargo().likeSmallGift()
                .but().withSizeInMilimeters(250, 150, 100).withFixedOrientation(false).build();
        
        List<DroneType> possibleDronTypes = cargoSpecyfication.isSatisfiedForDronTypes(cargo);
        
        assertThat(possibleDronTypes).hasSize(COUNT_OF_DRONE_TYPES);
    }
    
    @Test
    public void shouldNotAcceptCargoSizeEvenWhenItMatchesAfterRotationWHWhenFixedOrientationIsChecked() {
        CargoAggregate cargo = aCargo().likeSmallGift()
                .but().withSizeInMilimeters(250, 100, 150).withFixedOrientation(true).build();
        
        List<DroneType> possibleDronTypes = cargoSpecyfication.isSatisfiedForDronTypes(cargo);
        
        assertThat(possibleDronTypes).isEmpty();
    }
    
    @Test
    public void shouldNotAcceptCargoSizeEvenWhenItMatchesAfterRotationLHWhenFixedOrientationIsChecked() {
        CargoAggregate cargo = aCargo().likeSmallGift()
                .but().withSizeInMilimeters(100, 150, 250).withFixedOrientation(true).build();
        
        List<DroneType> possibleDronTypes = cargoSpecyfication.isSatisfiedForDronTypes(cargo);
        
        assertThat(possibleDronTypes).isEmpty();
    }
    
    @Test
    public void shouldAcceptCargoSizeAfterRotationWH() {
        CargoAggregate cargo = aCargo().likeSmallGift()
                .but().withSizeInMilimeters(250, 100, 150).withFixedOrientation(false).build();
        
        List<DroneType> possibleDronTypes = cargoSpecyfication.isSatisfiedForDronTypes(cargo);
        
        assertThat(possibleDronTypes).hasSize(COUNT_OF_DRONE_TYPES);
    }
    
    @Test
    public void shouldAcceptCargoSizeAfterRotationLH() {
        CargoAggregate cargo = aCargo().likeSmallGift()
                .but().withSizeInMilimeters(100, 150, 250).withFixedOrientation(false).build();
        
        List<DroneType> possibleDronTypes = cargoSpecyfication.isSatisfiedForDronTypes(cargo);
        
        assertThat(possibleDronTypes).hasSize(COUNT_OF_DRONE_TYPES);
    }
    
    @Test
    public void shouldAcceptNonFragileCommodityInCargo() {
        CargoAggregate cargo = aCargo().likeSmallGift()
                .but().withFragileCommodity(false).build();
        
        List<DroneType> possibleDronTypes = cargoSpecyfication.isSatisfiedForDronTypes(cargo);
        
        assertThat(possibleDronTypes).hasSize(COUNT_OF_DRONE_TYPES);
    }
    
    @Test
    public void shouldNotAcceptFragileCommodityInCargo() {
        CargoAggregate cargo = aCargo().likeSmallGift()
                .but().withFragileCommodity(true).build();
        
        List<DroneType> possibleDronTypes = cargoSpecyfication.isSatisfiedForDronTypes(cargo);
        
        assertThat(possibleDronTypes).isEmpty();
    }
    
    @Test
    public void shouldAcceptNonDangerousGoods() {
        CargoAggregate cargo = aCargo().likeSmallGift()
                .but().withDangerousGoods(false).build();
        
        List<DroneType> possibleDronTypes = cargoSpecyfication.isSatisfiedForDronTypes(cargo);
        
        assertThat(possibleDronTypes).hasSize(COUNT_OF_DRONE_TYPES);
    }
    
    @Test
    public void shouldNotAcceptDangerousGoods() {
        CargoAggregate cargo = aCargo().likeSmallGift()
                .but().withDangerousGoods(true).build();
        
        List<DroneType> possibleDronTypes = cargoSpecyfication.isSatisfiedForDronTypes(cargo);
        
        assertThat(possibleDronTypes).isEmpty();
    }
}
