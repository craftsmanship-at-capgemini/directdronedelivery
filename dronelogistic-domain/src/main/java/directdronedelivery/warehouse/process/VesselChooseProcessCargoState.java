package directdronedelivery.warehouse.process;

import java.util.Collections;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import directdronedelivery.cargo.AcceptableDeliveryTime;
import directdronedelivery.drone.DroneType;
import directdronedelivery.warehouse.businessrules.DeliveryTimeAcceptanceStrategy;

@EqualsAndHashCode
@ToString
public class VesselChooseProcessCargoState {
    
    private Integer cargoID;
    private Integer warehausID;
    private List<DroneType> possibleDronTypes;
    private boolean placeOfDeliveryAccepted;
    private boolean profitabilityAndPriorityAcceptance;
    private AcceptableDeliveryTime acceptableDeliveryTime;
    private transient VesselChooseProcessCargoIndependentState cargoIndependentSubDecisions;
    
    VesselChooseProcessCargoState(Integer cargoID, Integer warehausID, AcceptableDeliveryTime acceptableDeliveryTime,
            VesselChooseProcessCargoIndependentState cargoIndependentSubDecisions) {
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
