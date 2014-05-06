package directdronedelivery.warehouse.process;

import static directdronedelivery.cargo.OrderAndCargoInformationBuilder.aCargo;
import static org.fest.assertions.api.Assertions.assertThat;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import testing.Testing;
import directdronedelivery.cargo.CargoAggregate;
import directdronedelivery.cargo.Size;
import directdronedelivery.warehouse.BoxType;
import directdronedelivery.warehouse.businessrules.BoxChooseSpecification;
import directdronedelivery.warehouse.businessrules.CargoSpecyfication;

public class BoxSpecificationVsCargoSpecyficationTest {
    
    // TODO MM: test name or implementation
    @Inject CargoSpecyfication cargoSpecyfication;
    
    @Before
    public void setUp() throws Exception {
        Testing.inject(this);
    }
    
    @Test
    public void shouldFitEachValidCargoSizeInAnyBox() {
        CargoAggregate cargoSmallFragile = aCargo().withSize(Size.newSizeInMilimeters(200, 200, 200)).withFragileCommodity(true).build();
        BoxChooseSpecification boxChooseSpecSmallFragile = new BoxChooseSpecification(cargoSmallFragile);
        assertThat(boxChooseSpecSmallFragile.getBoxType()).isEqualTo(BoxType.SMALL_FRAGILE);
        
        CargoAggregate cargoBigFragile =  aCargo().withSize(Size.newSizeInMilimeters(350, 400, 500)).withFragileCommodity(true).build();
        BoxChooseSpecification boxChooseSpecBigFragile = new BoxChooseSpecification(cargoBigFragile);
        assertThat(boxChooseSpecBigFragile.getBoxType()).isEqualTo(BoxType.BIG_FRAGILE);
        
        CargoAggregate cargoBig =  aCargo().withSize(Size.newSizeInMilimeters(500, 500, 500)).withFragileCommodity(false).build();
        BoxChooseSpecification boxChooseSpecBig = new BoxChooseSpecification(cargoBig);
        assertThat(boxChooseSpecBig.getBoxType()).isEqualTo(BoxType.BIG);
        
        CargoAggregate cargoSmall =  aCargo().withSize(Size.newSizeInMilimeters(250, 250, 250)).withFragileCommodity(false).build();
        BoxChooseSpecification boxChooseSpecSmall = new BoxChooseSpecification(cargoSmall);
        assertThat(boxChooseSpecSmall.getBoxType()).isEqualTo(BoxType.SMALL);
        
        CargoAggregate cargoUnknown =  aCargo().withSize(Size.newSizeInMilimeters(600, 600, 600)).withFragileCommodity(false).build();
        BoxChooseSpecification boxChooseSpecUnkown = new BoxChooseSpecification(cargoUnknown);
        assertThat(boxChooseSpecUnkown.getBoxType()).isEqualTo(BoxType.UNKOWN);
        
        CargoAggregate cargoUnknown1 =  aCargo().withSize(Size.newSizeInMilimeters(250, 450, 600)).withFragileCommodity(false).build();
        BoxChooseSpecification boxChooseSpecUnkown1 = new BoxChooseSpecification(cargoUnknown1);
        assertThat(boxChooseSpecUnkown1.getBoxType()).isEqualTo(BoxType.UNKOWN);
        
     
    }
    
}
