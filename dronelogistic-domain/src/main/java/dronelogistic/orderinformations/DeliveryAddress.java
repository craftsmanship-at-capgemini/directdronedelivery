package dronelogistic.orderinformations;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class DeliveryAddress {
    
    @Getter String houseNumber;
    @Getter String streetName;
    @Getter String postalCode;
    
    private DeliveryAddress() {
    }
    
    public static DeliveryAddress newAddress(String houseNumber, String streetName, String postalCode) {
        DeliveryAddress address = new DeliveryAddress();
        address.houseNumber = houseNumber;
        address.streetName = streetName;
        address.postalCode = postalCode;
        
        return address;
    }
    
}
