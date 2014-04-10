package directdronedelivery.warehouse;

import directdronedelivery.warehouse.businessrules.BoxChooseRule;

public interface BoxStockRepository {
    // TODO GST: decrement stock - is that in business language
    BoxType decrementStockOfAppropriateBoxes(BoxChooseRule boxSpecification);
    
    void revertStockOfBoxes(BoxType boxType);
    
}
