package directdronedelivery.cargo;

import lombok.Getter;

public class OrderAggregate {
    
    @Getter protected Integer orderID;
    @Getter protected AcceptableDeliveryTime acceptableDeliveryTime;
    @Getter protected DeliveryAddress deliveryAddress;
    
}
