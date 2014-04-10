package directdronedelivery.cargo;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class CargoAggregate {
    // TODO MM: split to Cargo with reference to Order
    @Getter protected Integer cargoID;
    @Getter protected int weightInGrams;
    @Getter protected Size size;
    @Getter protected boolean fixedOrientation;
    @Getter protected boolean fragileCommodity;
    @Getter protected boolean dangerousGoods;
    
    @Getter protected OrderAggregate order;
    
    CargoAggregate() {
    }
    
}
