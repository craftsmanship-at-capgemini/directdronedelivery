package directdronedelivery.cargo;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import directdronedelivery.cargo.AcceptableDeliveryTime;
import directdronedelivery.cargo.CargoAggregate;
import directdronedelivery.cargo.DeliveryAddress;
import directdronedelivery.cargo.OrderAggregate;
import directdronedelivery.cargo.Size;

public class OrderAndCargoInformationBuilder {
    
    private static AtomicInteger nextCargoID = new AtomicInteger(1313);
    
    private CargoAggregate underConstruction;
    
    private OrderAndCargoInformationBuilder() {
    }
    
    public static OrderAndCargoInformationBuilder aCargo() {
        OrderAndCargoInformationBuilder builder = new OrderAndCargoInformationBuilder();
        builder.underConstruction = new CargoAggregate();
        builder.underConstruction.order = new OrderAggregate();
        return builder;
    }
    
    public OrderAndCargoInformationBuilder likeSmallGift() {
        withCargoID(nextCargoID.incrementAndGet());
        withOrderID(nextCargoID.get());
        withWeightInGrams(850);
        withSizeInMilimeters(250, 100, 10);
        withFixedOrientation(false);
        withFragileCommodity(false);
        withDangerousGoods(false);
        withUnlimitedDeliveryTime();
        withDeliveryAddress("Wrocław", "50-540", "Jableczna", "13", "123A");
        return this;
    }
    
    public OrderAndCargoInformationBuilder but() {
        return this;
    }
    
    public OrderAndCargoInformationBuilder withCargoID(Integer cargoID) {
        underConstruction.cargoID = cargoID;
        return this;
    }
    
    private OrderAndCargoInformationBuilder withOrderID(Integer orderID) {
        underConstruction.order.orderID = orderID;
        return this;
    }
    
    public OrderAndCargoInformationBuilder withWeightInGrams(int weightInGrams) {
        underConstruction.weightInGrams = weightInGrams;
        return this;
    }
    
    public OrderAndCargoInformationBuilder withWeightInKilos(int weightInKilos) {
        underConstruction.weightInGrams = 1000 * weightInKilos;
        return this;
    }
    
    /**
     * Sets weight parsed form text representation examples: <strong>5
     * kg</strong>, <strong>2,5 kg</strong>
     * 
     * @param textRepresentation
     *            text representation of size
     * @return this builder instance
     */
    public OrderAndCargoInformationBuilder withWeightFromTextRepresentation(String textRepresentation) {
        Pattern weightPattern = Pattern.compile(" *(\\d+)[,.]?(\\d*) *(g|kg) *");
        Matcher matcher = weightPattern.matcher(textRepresentation);
        if (matcher.matches()) {
            int integer = Integer.parseInt(matcher.group(1));
            int fraction = Integer.parseInt("0" + matcher.group(2));
            String units = matcher.group(3);
            BigDecimal weight = new BigDecimal(integer + "." + fraction);
            if (units.equals("g")) {
                underConstruction.weightInGrams = weight.intValue();
            } else if (units.equals("kg")) {
                underConstruction.weightInGrams = weight.multiply(new BigDecimal(1000)).intValue();
            }
        }
        return this;
    }
    
    public OrderAndCargoInformationBuilder withSize(Size size) {
        underConstruction.size = size;
        return this;
    }
    
    public OrderAndCargoInformationBuilder withSizeInMilimeters(int length, int width, int height) {
        underConstruction.size = Size.newSizeInMilimeters(length, width, height);
        return this;
    }
    
    /**
     * Sets size parsed form text representation examples:
     * <strong>250 x 150 x 100 mm</strong> or <strong>25 x 15 x 10 cm</strong>
     * 
     * @param textRepresentation
     *            text representation of size
     * @return this builder instance
     */
    public OrderAndCargoInformationBuilder withSizeFromTextRepresentation(String textRepresentation) {
        Pattern sizePattern = Pattern.compile(" *(\\d+) *x *(\\d+) *x *(\\d+) *(mm|cm) *");
        Matcher matcher = sizePattern.matcher(textRepresentation);
        if (matcher.matches()) {
            int length = Integer.parseInt(matcher.group(1));
            int width = Integer.parseInt(matcher.group(2));
            int height = Integer.parseInt(matcher.group(3));
            String units = matcher.group(4);
            
            if (units.equals("mm")) {
                underConstruction.size = Size.newSizeInMilimeters(length, width, height);
            } else if (units.equals("cm")) {
                underConstruction.size = Size.newSizeInMilimeters(10 * length, 10 * width, 10 * height);
            } else {
                throw new IllegalArgumentException("Wrong unit in text represenattion of size: " + textRepresentation
                        + " correct unit are: mm, cm");
            }
        } else {
            throw new IllegalArgumentException("Wrong text represenattion of size: " + textRepresentation
                    + " correct is: 250 x 150 x 100 mm");
        }
        return this;
    }

    public OrderAndCargoInformationBuilder withFixedOrientation(boolean fixedOrientation) {
        underConstruction.fixedOrientation = fixedOrientation;
        return this;
    }
    
    public OrderAndCargoInformationBuilder withFragileCommodity(boolean fragileCommodity) {
        underConstruction.fragileCommodity = fragileCommodity;
        return this;
    }
    
    public OrderAndCargoInformationBuilder withDangerousGoods(boolean dangerousGoods) {
        underConstruction.dangerousGoods = dangerousGoods;
        return this;
    }
    
    public OrderAndCargoInformationBuilder withAcceptableDeliveryTime(AcceptableDeliveryTime acceptableDeliveryTime) {
        underConstruction.order.acceptableDeliveryTime = acceptableDeliveryTime;
        return this;
    }
    
    public OrderAndCargoInformationBuilder withAcceptableDeliveryTime(
            AcceptableDeliveryTimeBuilder acceptableDeliveryTimeBuilder) {
        underConstruction.order.acceptableDeliveryTime = acceptableDeliveryTimeBuilder.build();
        return this;
    }
    
    public OrderAndCargoInformationBuilder withAcceptableDeliveryTime(List<String> intervals) {
        AcceptableDeliveryTimeBuilder acceptableDeliveryTimeBuilder = AcceptableDeliveryTimeBuilder.aTime();
        intervals.stream().forEach(interval -> acceptableDeliveryTimeBuilder.addInterval(interval));
        underConstruction.order.acceptableDeliveryTime = acceptableDeliveryTimeBuilder.build();
        return this;
    }
    
    public OrderAndCargoInformationBuilder withUnlimitedDeliveryTime() {
        underConstruction.order.acceptableDeliveryTime = AcceptableDeliveryTimeBuilder.aTime()
                .addInterval("00:00-24:00").build();
        return this;
    }
    
    public OrderAndCargoInformationBuilder withDeliveryAddress(String city, String postalCode, String streetName, String houseNumber, String flatNumber) {
        underConstruction.order.deliveryAddress = DeliveryAddress.newAddress(city, postalCode, streetName, houseNumber, flatNumber);
        return this;
    }
    
    public CargoAggregate build() {
        CargoAggregate builded = underConstruction;
        underConstruction = new CargoAggregate();
        underConstruction.order = new OrderAggregate();
        return builded;
    }
    
    public static int nextCargoID() {
        return nextCargoID.incrementAndGet();
    }
    
}
