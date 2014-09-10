package directdronedelivery.warehouse;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import directdronedelivery.warehouse.BoxStockRepository;
import directdronedelivery.warehouse.BoxType;
import directdronedelivery.warehouse.businessrules.BoxChooseSpecification;

public class BoxStockRepositoryInMem implements BoxStockRepository {
    
    private Map<BoxType, AtomicInteger> boxStocksInMemoryStore;
    
    public static Configurator configure(BoxStockRepository instance) {
        return ((BoxStockRepositoryInMem) instance).new Configurator();
    }
    
    public class Configurator {
        
        public Configurator withBoxStock(BoxType boxType, int stock) {
            boxStocksInMemoryStore.get(boxType).set(stock);
            return this;
        }
    }
    
    public BoxStockRepositoryInMem() {
        HashMap<BoxType, AtomicInteger> map = new HashMap<>(BoxType.values().length);
        for (BoxType boxType : BoxType.values()) {
            map.put(boxType, new AtomicInteger(97));
        }
        boxStocksInMemoryStore = Collections.unmodifiableMap(map);
    }
    
    @Override
    public BoxType decrementStockOfAppropriateBoxes(BoxChooseSpecification boxSpecification) {
        BoxType boxType = boxSpecification.preferredBoxTyp();
        boxStocksInMemoryStore.get(boxType).decrementAndGet();
        return boxType;
    }
    
    @Override
    public void revertStockOfBoxes(BoxType boxType) {
        boxStocksInMemoryStore.get(boxType).incrementAndGet();
    }
    
}
