package dronelogistic.orderinformations;

public interface OrdersInformationService {
    
    OrderAndCargoInformation getOrderAndCargoInformation(Integer cargoID);
    
    ConsignmentInformation getConsignmentInformation(Integer consignmentID);
    
}
