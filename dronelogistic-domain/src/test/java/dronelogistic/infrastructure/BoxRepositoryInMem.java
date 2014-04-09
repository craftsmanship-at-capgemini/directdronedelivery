package dronelogistic.infrastructure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.Getter;
import dronelogistic.orderinformations.Size;
import dronelogistic.warehaus.Box;
import dronelogistic.warehaus.BoxRepository;
import dronelogistic.warehaus.BoxSpecification;
import dronelogistic.warehaus.BoxType;

public class BoxRepositoryInMem implements BoxRepository {
    
    private Map<Integer, Box> boxDB;
    private AtomicInteger boxID = new AtomicInteger(1000);
    
    public BoxRepositoryInMem() {
        boxDB = new HashMap<Integer, Box>();
    }
    
    @Override
    public Box findBox(Integer boxID) {
        return boxDB.get(boxID);
    }
    
    @Override
    public Box findAppropriateBox(BoxSpecification boxSpecification) {
        List<Box> allBoxes = new ArrayList<Box>(boxDB.values());
        Box appropiateBox = null;
        
        for (Iterator<Box> iterator = allBoxes.iterator(); iterator.hasNext();) {
            Box box = (Box) iterator.next();
            
            if (boxSpecification.getBoxType().equals(box.getBoxType())){
                appropiateBox = box;
                break;
            }
        }
        return appropiateBox;
    }
    
    @Override
    public List<Box> findAll() {
        return new ArrayList<Box>(boxDB.values());
    }
    
    @Override
    public void init() {
        
        //the first small box
        Integer box1ID = nextBoxID();
        Size size = Size.newSizeInMilimeters(100, 100, 100);
        int weightInGrams = 2000;
        BoxType boxtype = BoxType.SMALL;
        
        Box box1 = new Box(box1ID, size, weightInGrams, boxtype);
        boxDB.put(box1ID, box1);
        
        // TODO Auto-generated method stub
        /**
        @Getter protected int boxID;
        @Getter protected Size size;
        @Getter protected int weightInGrams;
        @Getter protected BoxType boxType;
        */
        
    }
    
    /**public void init() throws Exception {
        final TrackingId xyz = new TrackingId("XYZ");
        final Cargo cargoXYZ = createCargoWithDeliveryHistory(
          xyz, STOCKHOLM, MELBOURNE, handlingEventRepository.lookupHandlingHistoryOfCargo(xyz));
        cargoDb.put(xyz.idString(), cargoXYZ);

        final TrackingId zyx = new TrackingId("ZYX");
        final Cargo cargoZYX = createCargoWithDeliveryHistory(
          zyx, MELBOURNE, STOCKHOLM, handlingEventRepository.lookupHandlingHistoryOfCargo(zyx));
        cargoDb.put(zyx.idString(), cargoZYX);

        final TrackingId abc = new TrackingId("ABC");
        final Cargo cargoABC = createCargoWithDeliveryHistory(
          abc, STOCKHOLM, HELSINKI, handlingEventRepository.lookupHandlingHistoryOfCargo(abc));
        cargoDb.put(abc.idString(), cargoABC);

        final TrackingId cba = new TrackingId("CBA");
        final Cargo cargoCBA = createCargoWithDeliveryHistory(
          cba, HELSINKI, STOCKHOLM, handlingEventRepository.lookupHandlingHistoryOfCargo(cba));
        cargoDb.put(cba.idString(), cargoCBA);
      }*/
    
    private int nextBoxID(){
        return boxID.incrementAndGet();
    }
    
}
