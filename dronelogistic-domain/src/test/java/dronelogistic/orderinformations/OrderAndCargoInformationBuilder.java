package dronelogistic.orderinformations;

public class OrderAndCargoInformationBuilder {
    
    private OrderAndCargoInformation underConstruction = null;
    
    public static OrderAndCargoInformationBuilder aCargo() {
        OrderAndCargoInformationBuilder builder = new OrderAndCargoInformationBuilder();
        builder.underConstruction = new OrderAndCargoInformation();
        return builder;
    }
    
    public OrderAndCargoInformationBuilder likeSmallGift() {
        withWeightInGrams(850);
        withSizeInMilimeters(250, 100, 10);
        withFixedOrientation(false);
        return this;
    }
    
    public OrderAndCargoInformationBuilder but() {
        return this;
    }
    
    public OrderAndCargoInformationBuilder withWeightInGrams(int weightInGrams) {
        underConstruction.weight = weightInGrams;
        return this;
    }
    
    public OrderAndCargoInformationBuilder withWeightInKilos(int weightInKilos) {
        underConstruction.weight = 1000 * weightInKilos;
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
    
    public OrderAndCargoInformation build() {
        OrderAndCargoInformation builded = underConstruction;
        underConstruction = new OrderAndCargoInformation();
        return builded;
    }
    
}
