package directdronedelivery.cargo;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class DeliveryAddress {
    
    @Getter protected String houseNumber;
    @Getter protected String streetName;
    @Getter protected String postalCode;
    
    protected DeliveryAddress() {
    }
    
    public static DeliveryAddress newAddress(String houseNumber, String streetName, String postalCode) {
        DeliveryAddress address = new DeliveryAddress();
        address.houseNumber = houseNumber;
        address.streetName = streetName;
        address.postalCode = postalCode;
        
        return address;
    }
    
}
