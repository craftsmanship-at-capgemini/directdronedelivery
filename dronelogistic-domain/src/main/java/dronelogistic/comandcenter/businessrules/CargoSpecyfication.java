package dronelogistic.comandcenter.businessrules;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import dronelogistic.orderinformations.OrderAndCargoInformation;
import dronelogistic.orderinformations.Size;

public class CargoSpecyfication {
    
    private static final int MAX_WEIGHT = 5000;
    private static final Size MAX_SIZE = Size.newSizeInMilimeters(250, 150, 100);
    private static final List<String> POSSIBLE_DRONE_TYPES = Arrays.asList("T4 v1", "T8 v1");
    
    public List<String> possibleDronTypes(OrderAndCargoInformation orderAndCargoInformation) {
        
        if (orderAndCargoInformation.getWeightInGrams() > MAX_WEIGHT) {
            return Collections.emptyList();
        }
        
        Size size = orderAndCargoInformation.getSize();
        if (!orderAndCargoInformation.isFixedOrientation() && !size.fitsIn(MAX_SIZE)) {
            return Collections.emptyList();
        }
        if (orderAndCargoInformation.isFixedOrientation() && !size.fitsInWithFixedOrientation(MAX_SIZE)) {
            return Collections.emptyList();
        }
        
        return POSSIBLE_DRONE_TYPES;
    }
    
}
