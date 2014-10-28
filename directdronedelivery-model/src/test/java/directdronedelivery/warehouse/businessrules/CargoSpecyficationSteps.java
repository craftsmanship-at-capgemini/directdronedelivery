package directdronedelivery.warehouse.businessrules;

import static directdronedelivery.cargo.OrderAndCargoInformationBuilder.aCargo;
import static org.fest.assertions.api.Assertions.assertThat;

import java.util.List;

import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Named;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

import directdronedelivery.cargo.CargoAggregate;
import directdronedelivery.drone.DroneType;
import directdronedelivery.warehouse.businessrules.CargoSpecyfication;

public class CargoSpecyficationSteps {
    
    private CargoAggregate givenCargo;
    private List<DroneType> actualDronTypes;
    
    @Given("a cargo with <weight>")
    public void createCargoWithWeight(@Named("weight") String weight) {
        givenCargo = aCargo().likeSmallGift()
                .but().withWeightFromTextRepresentation(weight).build();
    }
    
    @Given("a cargo with <size>")
    public void createCargoWithSize(@Named("size") String size) {
        givenCargo = aCargo().likeSmallGift()
                .but().withSizeFromTextRepresentation(size).build();
    }
    
    @Given("a cargo with dangerous goods")
    public void createCargoWithDangerousGoods() {
        givenCargo = aCargo().likeSmallGift()
                .but().withDangerousGoods(true).build();
    }
    
    @When("the cargo arrives to warehouse")
    public void useCargoSpecyfication() {
        actualDronTypes = new CargoSpecyfication().isSatisfiedForDronTypes(givenCargo);
    }
    
    @Then("any drone of <dronetypes> could be chosen")
    public void assertDroneTypes(@Named("dronetypes") List<DroneType> expectedDroneTypes) {
        assertThat(actualDronTypes).containsAll(expectedDroneTypes);
    }
    
    @Then("it can't be deliver by drone")
    public void assertDroneDeliveryImpossible() {
        assertThat(actualDronTypes).isEmpty();
    }
    
}
