package logisticsystem.facade;

import static logisticsystem.facade.Configuration.Param.BASEURI;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import logisticsystem.facade.Configuration.Config;
import directdronedelivery.cargo.CargoAggregate;
import directdronedelivery.cargo.ConsignementFactory;
import directdronedelivery.cargo.CargoRepository;
import directdronedelivery.cargo.ConsignmentAggregate;
import directdronedelivery.cargo.JSONCargoMapper;

@Singleton
@LocalBean
public class RESTCachedCargoRepository implements CargoRepository {
    
    // TODO switch to JCache or handle concurrency issues.
    Map<Integer, CargoAggregate> cargos = new HashMap<>();
    
    @Inject @Config(BASEURI) String baseUri;
    
    @Inject ConsignementFactory factory;
    
    @Override
    public CargoAggregate findCargo(Integer cargoID) {
        if (cargos.containsKey(cargoID)) {
            return cargos.get(cargoID);
        } else {
            String json = ClientBuilder.newClient().target(baseUri)
                    .path("cargo").path(cargoID.toString())
                    .request(MediaType.APPLICATION_JSON)
                    .get(String.class);
            
            CargoAggregate cargo = JSONCargoMapper.createCargo(json);
            cargos.put(cargoID, cargo);
            return cargo;
        }
    }
    
    @Override
    public ConsignmentAggregate findConsignment(Integer consignmentID) {
        List<Integer> cargosInConsignment = ClientBuilder.newClient().target(baseUri)
                .path("consignment").path(consignmentID.toString()).path("cargos")
                .request(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<Integer>>() {
                });
        
        List<CargoAggregate> cargoAggregatesInConsignement =
                cargosInConsignment.parallelStream()
                        .map(cargoID -> findCargo(cargoID)).collect(Collectors.toList());
        return factory.createConsignment(consignmentID, cargoAggregatesInConsignement);
    }
    
    public void invalidateCacheForCargo(Integer cargoID) {
        cargos.remove(cargoID);
    }
    
}
