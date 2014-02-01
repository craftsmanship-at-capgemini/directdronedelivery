package dronelogistic.orderinformations;

public class OrderAndCargoInformation {
    
    protected Integer cargoId;
    protected int weight;
    protected Size size;
    
    public Integer getCargoId() {
        return cargoId;
    }
    
    public int getWeightInGrams() {
        return weight;
    }
    
    public Size getSize() {
        return size;
    }
    
}
