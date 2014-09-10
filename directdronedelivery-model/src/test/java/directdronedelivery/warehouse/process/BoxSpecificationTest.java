package directdronedelivery.warehouse.process;

import static directdronedelivery.cargo.OrderAndCargoInformationBuilder.aCargo;
import static directdronedelivery.drone.DroneBuilder.aDrone;
import static org.fest.assertions.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

import testing.Testing;
import directdronedelivery.cargo.CargoAggregate;
import directdronedelivery.drone.DroneAggregate;
import directdronedelivery.drone.DroneType;
import directdronedelivery.warehouse.BoxType;
import directdronedelivery.warehouse.businessrules.BoxChooseSpecification;

public class BoxSpecificationTest {
    
    @Before
    public void setUp() throws Exception {
        Testing.inject(this);
    }
    
    @Test
    public void shouldPreferSmallFragileBox() {
        CargoAggregate fragileCargo = aCargo().likeSmallGift().but().withFragileCommodity(true).build();
        DroneAggregate smallDrone = aDrone().likeDocked4RotorsDrone().but().withDroneType(DroneType.SMALL_FOUR_ROTORS).build();
        
        BoxChooseSpecification boxSpecification = new BoxChooseSpecification(fragileCargo, smallDrone);
        BoxType preferredBoxTyp = boxSpecification.preferredBoxTyp();
        
        assertThat(preferredBoxTyp).isEqualTo(BoxType.SMALL_FRAGILE);
    }
    
    @Test
    public void shouldPreferBigFragileBox() {
        CargoAggregate fragileCargo = aCargo().likeSmallGift().but().withFragileCommodity(true).build();
        DroneAggregate bigDrone = aDrone().likeDocked4RotorsDrone().but().withDroneType(DroneType.BIG_SIX_ROTORS).build();
        
        BoxChooseSpecification boxSpecification = new BoxChooseSpecification(fragileCargo, bigDrone);
        BoxType preferredBoxTyp = boxSpecification.preferredBoxTyp();
        
        assertThat(preferredBoxTyp).isEqualTo(BoxType.BIG_FRAGILE);
    }
    
    @Test
    public void shouldPreferSmallBox() {
        CargoAggregate nonfragileCargo = aCargo().likeSmallGift().but().withFragileCommodity(false).build();
        DroneAggregate smallDrone = aDrone().likeDocked4RotorsDrone().but().withDroneType(DroneType.SMALL_FOUR_ROTORS).build();
        
        BoxChooseSpecification boxChooseSpecSmall = new BoxChooseSpecification(nonfragileCargo, smallDrone);
        
        assertThat(boxChooseSpecSmall.preferredBoxTyp()).isEqualTo(BoxType.SMALL);
    }
    
    @Test
    public void shouldPreferBigBox() {
        CargoAggregate nonfragileCargo = aCargo().likeSmallGift().but().withFragileCommodity(false).build();
        DroneAggregate bigDrone = aDrone().likeDocked4RotorsDrone().but().withDroneType(DroneType.BIG_SIX_ROTORS).build();
        
        BoxChooseSpecification boxSpecification = new BoxChooseSpecification(nonfragileCargo, bigDrone);
        BoxType preferredBoxTyp = boxSpecification.preferredBoxTyp();
        
        assertThat(preferredBoxTyp).isEqualTo(BoxType.BIG);
    }
    
}
