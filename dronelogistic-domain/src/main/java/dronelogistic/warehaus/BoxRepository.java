package dronelogistic.warehaus;

import java.util.List;

public interface BoxRepository {
    
    public Box findBox(Integer boxID);
    
    public Box findAppropriateBox(BoxSpecification boxSpecification);
    
    public List<Box> findAll();
    
    public void init();
   
}
