package directdronedelivery.cargo;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

import directdronedelivery.cargo.CargoAggregate;
import directdronedelivery.cargo.ConsignmentAggregate;

public class ConsignmentBuilder {
    
    private static AtomicInteger nextConsignmentID = new AtomicInteger(666);
    
    private ConsignmentAggregate underConstruction;
    
    private ConsignmentBuilder() {
    }
    
    public static ConsignmentBuilder aConsignment() {
        ConsignmentBuilder builder = new ConsignmentBuilder();
        builder.underConstruction = new ConsignmentAggregate();
        return builder;
    }
    
    public ConsignmentBuilder likeEmptyConsignment() {
        withConsignmentID(nextConsignmentID.incrementAndGet());
        withNoCargoIn();
        return this;
    }
    
    public ConsignmentBuilder but() {
        return this;
    }
    
    public ConsignmentBuilder withConsignmentID(int consignmentID) {
        underConstruction.consignmentID = consignmentID;
        return this;
    }
    
    public ConsignmentBuilder withCargo(CargoAggregate cargo) {
        underConstruction.cargosInConsignment.add(cargo);
        return this;
    }
    
    public ConsignmentBuilder withNoCargoIn() {
        underConstruction.cargosInConsignment = new LinkedList<>();
        return this;
    }
    
    public ConsignmentAggregate build() {
        ConsignmentAggregate builded = underConstruction;
        underConstruction = new ConsignmentAggregate();
        return builded;
    }
    
    public static int nextConsignmentID() {
        return nextConsignmentID.incrementAndGet();
    }
    
}
