package dronelogistic.orderinformations;

public class Size {
    
    int x;
    int y;
    int z;
    
    protected Size() {
    }
    
    public static Size newSizeInMilimeters(int x, int y, int z) {
        Size cargoSize = new Size();
        cargoSize.x = x;
        cargoSize.y = y;
        cargoSize.z = z;
        
        return cargoSize;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public int getZ() {
        return z;
    }
    
}
