package directdronedelivery.warehouse.businessrules;

import static directdronedelivery.warehouse.BoxType.BIG;
import static directdronedelivery.warehouse.BoxType.BIG_FRAGILE;
import static directdronedelivery.warehouse.BoxType.SMALL;
import static directdronedelivery.warehouse.BoxType.SMALL_FRAGILE;
import static directdronedelivery.warehouse.BoxType.UNKOWN;
import lombok.Getter;
import directdronedelivery.cargo.CargoAggregate;
import directdronedelivery.cargo.Size;
import directdronedelivery.warehouse.BoxType;

public class BoxChooseSpecification {

    //TODO MM: compare with CargoSpecyfication
    //size in millimeters
    private static final int MAX_HEIGHT = 500;
    private static final int MAX_LENGTH = 500;
    private static final int MAX_WIDTH = 500;
    
    //size in millimeters
    private static final int SMALL_MAX_HEIGHT = 300;
    private static final int SMALL_MAX_LENGTH = 300;
    private static final int SMALL_MAX_WIDTH = 300;
    
    private Size size;
    private boolean fragile;
    @Getter protected BoxType boxType;
    
    public BoxChooseSpecification(CargoAggregate orderAndCargoInformation) {
        this.size = orderAndCargoInformation.getSize();
        this.fragile = orderAndCargoInformation.isFragileCommodity();
        this.boxType = defineBoxType();
    }
    
    public BoxType defineBoxType() {
        if (isSmall() && this.fragile){
            return SMALL_FRAGILE;
        } else if (isBig() && this.fragile){
            return BIG_FRAGILE;
        } else if (isSmall()){
            return SMALL;
        } else if (isBig()){
            return BIG;
        } else {
            return UNKOWN;
        }
    }
    
    private boolean isSmall(){
        if (size.getHeight() < SMALL_MAX_HEIGHT &&
                size.getLength() < SMALL_MAX_LENGTH &&
                size.getWidth() < SMALL_MAX_WIDTH) {
            return true;
        }
        return false;
    }
    
    private boolean isBig(){
        if ((size.getHeight() >= SMALL_MAX_HEIGHT && size.getHeight() <= MAX_HEIGHT) &&
                (size.getLength() >= SMALL_MAX_LENGTH && size.getLength() <= MAX_LENGTH)  &&
                (size.getWidth() >= SMALL_MAX_WIDTH && size.getWidth() <= MAX_WIDTH)) {
            return true;
        }
        return false;
    }
}
