package dronelogistic.orderinformations;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

public class ConsignmentInformationBuilder {
    
    private static AtomicInteger nextConsignmentID = new AtomicInteger(666);
    
    private ConsignmentInformation underConstruction;
    
    private ConsignmentInformationBuilder() {
    }
    
    public static ConsignmentInformationBuilder aConsignment() {
        ConsignmentInformationBuilder builder = new ConsignmentInformationBuilder();
        builder.underConstruction = new ConsignmentInformation();
        return builder;
    }
    
    public ConsignmentInformationBuilder likeEmptyConsignment() {
        withConsignmentID(nextConsignmentID.incrementAndGet());
        withNoCargoIn();
        return this;
    }
    
    public ConsignmentInformationBuilder but() {
        return this;
    }
    
    public ConsignmentInformationBuilder withConsignmentID(int consignmentID) {
        underConstruction.consignmentID = consignmentID;
        return this;
    }
    
    public ConsignmentInformationBuilder withCargo(OrderAndCargoInformation cargo) {
        underConstruction.cargosInConsignment.add(cargo);
        return this;
    }
    
    public ConsignmentInformationBuilder withNoCargoIn() {
        underConstruction.cargosInConsignment = new LinkedList<>();
        return this;
    }
    
    public ConsignmentInformation build() {
        ConsignmentInformation builded = underConstruction;
        underConstruction = new ConsignmentInformation();
        return builded;
    }
    
    public static int nextConsignmentID() {
        return nextConsignmentID.incrementAndGet();
    }
    
}
