package dronelogistic.orderinformations;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class OrderAndCargoInformation {
    
    @Getter protected Integer cargoID;
    @Getter protected int weightInGrams;
    @Getter protected Size size;
    @Getter protected boolean fixedOrientation;
    @Getter protected boolean fragileCommodity;
    @Getter protected boolean dangerousGoods;
    @Getter protected AcceptableDeliveryTime acceptableDeliveryTime;
    
    OrderAndCargoInformation() {
    }
}
