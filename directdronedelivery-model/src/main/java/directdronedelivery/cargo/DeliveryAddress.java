package directdronedelivery.cargo;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class DeliveryAddress {
    
    @Getter protected String city;
    @Getter protected String postalCode;
    @Getter protected String streetName;
    @Getter protected String houseNumber;
    
    protected DeliveryAddress() {
    }
    
    public static DeliveryAddress newAddress(String city, String postalCode, String streetName, String houseNumber) {
        DeliveryAddress address = new DeliveryAddress();
        address.city = city;
        address.postalCode = postalCode;
        address.streetName = streetName;
        address.houseNumber = houseNumber;
        
        return address;
    }
    
}
