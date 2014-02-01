package dronelogistic.orderinformations;

public interface OrdersInformationService {
    
    OrderAndCargoInformation getOrderAndCargoInformation(Integer cargoId);
    
    ConsignmentInformation getConsignmentInformation(Integer consignmentID);
    
}
