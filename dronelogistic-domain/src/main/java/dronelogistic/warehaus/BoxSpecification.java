package dronelogistic.warehaus;

import lombok.Getter;
import dronelogistic.orderinformations.Size;
import static dronelogistic.warehaus.BoxType.*;

public class BoxSpecification {

    
    public static final int MAX_HEIGHT = 1000;
    public static final int MAX_LENGTH = 1000;
    public static final int MAX_WIDTH = 1000;
    
    public static final int SMALL_MAX_HEIGHT = 300;
    public static final int SMALL_MAX_LENGTH = 300;
    public static final int SMALL_MAX_WIDTH = 300;  
    
    @Getter protected Size size;
    @Getter protected int weightInGrams;
    @Getter protected BoxType boxType;
    
    public BoxSpecification(Size size, int weightInGrams) {
        this.size = size;
        this.weightInGrams = weightInGrams;
        this.boxType = defineBoxType();
    }
    
    public BoxType defineBoxType(){
        
        if (size.getHeight() < SMALL_MAX_HEIGHT && 
                size.getLength() < SMALL_MAX_LENGTH && 
                size.getWidth() < SMALL_MAX_WIDTH){
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
