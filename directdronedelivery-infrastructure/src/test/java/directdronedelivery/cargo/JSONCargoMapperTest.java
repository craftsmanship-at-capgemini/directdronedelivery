package directdronedelivery.cargo;

import static directdronedelivery.cargo.OrderAndCargoInformationBuilder.aCargo;
import static java.util.Arrays.asList;
import static org.fest.assertions.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;

public class JSONCargoMapperTest {
    
    @Test
    public void shouldCreateValidCargoFromJSON() {
        // given
        List<String> acceptableDeliveryTimeIntervals = asList("07:30-09:00", "17:00-23:30");
        CargoAggregate cargo = aCargo().likeSmallGift()
                .withAcceptableDeliveryTime(acceptableDeliveryTimeIntervals)
                .build();
        String json = CargoToJSONMapper.toLogisticSystemJSON(cargo, acceptableDeliveryTimeIntervals);
        
        // when
        CargoAggregate parsed = JSONCargoMapper.createCargo(json);
        
        // then
        assertThat(parsed).isEqualsToByComparingFields(cargo);
        assertThat(parsed.order).isEqualsToByComparingFields(cargo.order);
    }
    
}
