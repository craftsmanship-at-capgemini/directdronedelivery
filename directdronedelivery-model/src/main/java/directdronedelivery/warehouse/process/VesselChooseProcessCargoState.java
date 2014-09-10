package directdronedelivery.warehouse.process;

import java.util.Collections;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import directdronedelivery.cargo.AcceptableDeliveryTime;
import directdronedelivery.drone.DroneType;
import directdronedelivery.warehouse.businessrules.DeliveryTimeAcceptanceStrategy;

@EqualsAndHashCode
@ToString
public class VesselChooseProcessCargoState {
    
    @Getter private Integer cargoID;
    @Getter private Integer warehouseID;
    @Getter private List<DroneType> possibleDronTypes;
    @Setter private boolean placeOfDeliveryAccepted;
    @Setter private boolean profitabilityAndPriorityAcceptance;
    @Setter private boolean alreadyDeliveredWithTruck;
    private boolean droneDeliveryDenied;
    private AcceptableDeliveryTime acceptableDeliveryTime;
    private transient VesselChooseProcessWarehouseState warehouseState;
    
    protected VesselChooseProcessCargoState(Integer cargoID, Integer warehouseID,
            AcceptableDeliveryTime acceptableDeliveryTime,
            VesselChooseProcessWarehouseState warehouseState) {
        this.cargoID = cargoID;
        this.warehouseID = warehouseID;
        this.warehouseState = warehouseState;
        
        this.possibleDronTypes = Collections.emptyList();
        this.placeOfDeliveryAccepted = false;
        this.profitabilityAndPriorityAcceptance = false;
        this.alreadyDeliveredWithTruck = false;
        this.droneDeliveryDenied = false;
        this.acceptableDeliveryTime = acceptableDeliveryTime;
    }
    
    public boolean isDroneDeliveryPossible() {
        return !possibleDronTypes.isEmpty() && placeOfDeliveryAccepted;
    }
    
    public boolean isPositive(DeliveryTimeAcceptanceStrategy deliveryTimeAcceptanceStrategy) {
        return !alreadyDeliveredWithTruck && !droneDeliveryDenied
                && isDroneDeliveryPossible()
                && profitabilityAndPriorityAcceptance
                && warehouseState.arePositive()
                && deliveryTimeAcceptanceStrategy.isPositive(acceptableDeliveryTime);
    }
    
    public void setPossibleDronTypes(List<DroneType> possibleDronTypes) {
        this.possibleDronTypes = Collections.unmodifiableList(possibleDronTypes);
    }
    
    public void denyDroneDelivery() {
        this.droneDeliveryDenied = true;
    }
}
