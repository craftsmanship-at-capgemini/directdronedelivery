package dronelogistic.orderinformations;

public class OrderAndCargoInformationBuilder {
    
    private OrderAndCargoInformation underConstruction;
    
    private OrderAndCargoInformationBuilder() {
    }
    
    public static OrderAndCargoInformationBuilder aCargo() {
        OrderAndCargoInformationBuilder builder = new OrderAndCargoInformationBuilder();
        builder.underConstruction = new OrderAndCargoInformation();
        return builder;
    }
    
    public OrderAndCargoInformationBuilder likeSmallGift() {
        withWeightInGrams(850);
        withSizeInMilimeters(250, 100, 10);
        withFixedOrientation(false);
        withFragileCommodity(false);
        withDangerousGoods(false);
        withUnimitedDeliveryTime();
        return this;
    }
    
    public OrderAndCargoInformationBuilder but() {
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
        underConstruction.acceptableDeliveryTime = acceptableDeliveryTime;
        return this;
    }
    
    public OrderAndCargoInformationBuilder withAcceptableDeliveryTime(
            AcceptableDeliveryTimeBuilder acceptableDeliveryTimeBuilder) {
        underConstruction.acceptableDeliveryTime = acceptableDeliveryTimeBuilder.build();
        return this;
    }
    
    public OrderAndCargoInformationBuilder withUnimitedDeliveryTime() {
        underConstruction.acceptableDeliveryTime = AcceptableDeliveryTimeBuilder.aTime()
                .addInterval("00:00-24:00").build();
        return this;
    }
    
    public OrderAndCargoInformation build() {
        OrderAndCargoInformation builded = underConstruction;
        underConstruction = new OrderAndCargoInformation();
        return builded;
    }
    
}
