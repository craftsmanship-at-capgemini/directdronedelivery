package directdronedelivery.warehouse;

import directdronedelivery.warehouse.businessrules.BoxChooseSpecification;

public interface BoxStockRepository {
    BoxType decrementStockOfAppropriateBoxes(BoxChooseSpecification boxSpecification);
    
    void revertStockOfBoxes(BoxType boxType);
    
}
