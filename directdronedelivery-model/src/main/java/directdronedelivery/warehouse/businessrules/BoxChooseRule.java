package directdronedelivery.warehouse.businessrules;

import lombok.Getter;
import directdronedelivery.cargo.CargoAggregate;
import directdronedelivery.cargo.Size;
import directdronedelivery.warehouse.BoxType;
import static directdronedelivery.warehouse.BoxType.*;

// TODO GST: match to CargoSpecyfication: orientation
public class BoxChooseRule {
    
    private static final int MAX_HEIGHT = 1000;
    private static final int MAX_LENGTH = 1000;
    private static final int MAX_WIDTH = 1000;
    
    private static final int SMALL_MAX_HEIGHT = 300;
    private static final int SMALL_MAX_LENGTH = 300;
    private static final int SMALL_MAX_WIDTH = 300;
    
    private Size size;
    private int weightInGrams;
    @Getter protected BoxType boxType;
    
    public BoxChooseRule(CargoAggregate orderAndCargoInformation) {
        this.size = orderAndCargoInformation.getSize();
        this.weightInGrams = orderAndCargoInformation.getWeightInGrams();
        this.boxType = defineBoxType();
    }
    
    public BoxType defineBoxType() {
        
        if (size.getHeight() < SMALL_MAX_HEIGHT &&
                size.getLength() < SMALL_MAX_LENGTH &&
                size.getWidth() < SMALL_MAX_WIDTH) {
            return SMALL;
        } else if (size.getHeight() > MAX_HEIGHT ||
                size.getLength() > MAX_LENGTH ||
                size.getWidth() > MAX_WIDTH) {
            return UNKOWN;
            
        } else {
            return BIG;
        }
    }
}
