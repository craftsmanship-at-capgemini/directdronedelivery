package directdronedelivery.warehouse;

import directdronedelivery.warehouse.businessrules.BoxChooseRule;

public interface BoxStockRepository {
    // TODO GST: decrement stock - is that in business language
    public BoxType decrementStockOfAppropriateBoxes(BoxChooseRule boxSpecification);
    
    public void revertStockOfBoxes(BoxType boxType);
    
}
