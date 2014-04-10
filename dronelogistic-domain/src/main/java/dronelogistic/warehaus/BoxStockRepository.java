package dronelogistic.warehaus;

public interface BoxStockRepository {
    
    public BoxType decrementStockOfAppropriateBoxes(BoxSpecification boxSpecification);
    
    public void revertStockOfBoxes(BoxType boxType);
    
}
