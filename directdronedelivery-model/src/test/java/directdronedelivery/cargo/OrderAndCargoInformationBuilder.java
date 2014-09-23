package directdronedelivery.cargo;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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
    
    public OrderAndCargoInformationBuilder withSize(Size size) {
        underConstruction.size = size;
        return this;
    }
    
    public OrderAndCargoInformationBuilder withSizeInMilimeters(int length, int width, int height) {
        underConstruction.size = Size.newSizeInMilimeters(length, width, height);
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
