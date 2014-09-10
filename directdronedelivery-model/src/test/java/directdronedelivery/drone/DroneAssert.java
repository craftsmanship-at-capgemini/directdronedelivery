package directdronedelivery.drone;

import org.fest.assertions.api.AbstractAssert;

public class DroneAssert extends AbstractAssert<DroneAssert, DroneAggregate> {
    
    private DroneAssert(DroneAggregate drone) {
        super(drone, DroneAssert.class);
    }
    
    public static DroneAssert assertThat(DroneAggregate drone) {
        return new DroneAssert(drone);
    }
    
    public DroneAssert isCool() {
        org.fest.assertions.api.Assertions.assertThat(actual)
            .overridingErrorMessage("If you have drone <is not null>, it is always cool")
            .isNotNull();
        return this;
    }
    
}
