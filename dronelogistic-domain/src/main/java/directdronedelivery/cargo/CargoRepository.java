package directdronedelivery.cargo;

public interface CargoRepository {
    
    CargoAggregate findCargo(Integer cargoID);
    
    ConsignmentAggregate findConsignment(Integer consignmentID);
    
}
