package dronelogistic.comandcenter;

import java.util.Collections;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import dronelogistic.comandcenter.businessrules.DeliveryTimeAcceptanceStrategy;
import dronelogistic.dronflightcontrol.DroneType;
import dronelogistic.orderinformations.AcceptableDeliveryTime;

@EqualsAndHashCode
@ToString
public class TakeOffDecision {
    
    private Integer cargoID;
    private Integer warehausID;
    private List<DroneType> possibleDronTypes;
    private boolean placeOfDeliveryAccepted;
    private boolean profitabilityAndPriorityAcceptance;
    private AcceptableDeliveryTime acceptableDeliveryTime;
    private transient CargoIndependentSubDecisions cargoIndependentSubDecisions;
    
    TakeOffDecision(Integer cargoID, Integer warehausID, AcceptableDeliveryTime acceptableDeliveryTime,
            CargoIndependentSubDecisions cargoIndependentSubDecisions) {
        this.cargoID = cargoID;
        this.warehausID = warehausID;
        this.cargoIndependentSubDecisions = cargoIndependentSubDecisions;
        
        this.possibleDronTypes = Collections.emptyList();
        this.placeOfDeliveryAccepted = false;
        this.profitabilityAndPriorityAcceptance = false;
        this.acceptableDeliveryTime = acceptableDeliveryTime;
    }
    
    public boolean isCargoAndOrderAcceptable() {
        return !possibleDronTypes.isEmpty() && placeOfDeliveryAccepted;
    }
    
    public boolean isPositive(DeliveryTimeAcceptanceStrategy deliveryTimeAcceptanceStrategy) {
        return isCargoAndOrderAcceptable()
                && profitabilityAndPriorityAcceptance
                && cargoIndependentSubDecisions.arePositive()
                && deliveryTimeAcceptanceStrategy.isPositive(acceptableDeliveryTime);
    }
    
    public Integer getCargoID() {
        return cargoID;
    }
    
    public Integer getWarehausID() {
        return warehausID;
    }
    
    public List<DroneType> getPossibleDronTypes() {
        return possibleDronTypes;
    }
    
    public void setPossibleDronTypes(List<DroneType> possibleDronTypes) {
        this.possibleDronTypes = possibleDronTypes;
    }
    
    public void setPlaceOfDeliveryAccepted(boolean placeOfDeliveryAccepted) {
        this.placeOfDeliveryAccepted = placeOfDeliveryAccepted;
        
    }
    
    public void setProfitabilityAndPriorityAcceptance(boolean profitabilityAndPriorityAcceptance) {
        this.profitabilityAndPriorityAcceptance = profitabilityAndPriorityAcceptance;
    }
}
