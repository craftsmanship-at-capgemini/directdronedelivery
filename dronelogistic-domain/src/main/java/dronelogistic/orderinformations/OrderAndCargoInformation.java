package dronelogistic.orderinformations;

public class OrderAndCargoInformation {
    
    protected Integer cargoId;
    protected int weight;
    protected Size size;
    protected boolean fixedOrientation;
    protected boolean fragileCommodity;
    protected boolean dangerousGoods;
    
    public Integer getCargoId() {
        return cargoId;
    }
    
    public int getWeightInGrams() {
        return weight;
    }
    
    public Size getSize() {
        return size;
    }
    
    public boolean isFixedOrientation() {
        return fixedOrientation;
    }
    
    public boolean isFragileCommodity() {
        return fragileCommodity;
    }
    
    public boolean isDangerousGoods() {
        return dangerousGoods;
    }
    
}
