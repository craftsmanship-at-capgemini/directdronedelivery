package dronelogistic.comandcenter.businessrules;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import dronelogistic.orderinformations.OrderAndCargoInformation;

public class CargoSpecyfication {
    
    private List<String> possibleDronTypes = Arrays.asList("T4 v1", "T8 v1");
    
    public List<String> possibleDronTypes(OrderAndCargoInformation orderAndCargoInformation) {
        if (orderAndCargoInformation.getWeightInGrams() > 5000) {
            return Collections.emptyList();
        } else {
            return possibleDronTypes;
        }
    }
}
