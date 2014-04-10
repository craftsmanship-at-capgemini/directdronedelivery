package directdronedelivery.cargo;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode(of = "orderID")
@ToString
public class OrderAggregate {
    
    @Getter protected Integer orderID;
    @Getter protected AcceptableDeliveryTime acceptableDeliveryTime;
    @Getter protected DeliveryAddress deliveryAddress;
    
}
