package directdronedelivery.cargo;

public interface OrdersInformationService {
    
    CargoAggregate getOrderAndCargoInformation(Integer cargoID);
    
    ConsignmentInformation getConsignmentInformation(Integer consignmentID);
    
}
