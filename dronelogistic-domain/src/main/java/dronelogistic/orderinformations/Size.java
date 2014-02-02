package dronelogistic.orderinformations;

import java.util.Arrays;

public class Size {
    
    int length;
    int width;
    int height;
    
    protected Size() {
    }
    
    public static Size newSizeInMilimeters(int length, int width, int height) {
        Size cargoSize = new Size();
        cargoSize.length = length;
        cargoSize.width = width;
        cargoSize.height = height;
        
        return cargoSize;
    }
    
    public int getLength() {
        return length;
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public boolean fitsIn(Size otherSize) {
        int[] dimensions = { getLength(), getWidth(), getHeight() };
        Arrays.sort(dimensions);
        
        int[] otherDimensions = { otherSize.getLength(), otherSize.getWidth(), otherSize.getHeight() };
        Arrays.sort(otherDimensions);
        
        boolean fits = dimensions[0] <= otherDimensions[0] &&
                dimensions[1] <= otherDimensions[1] &&
                dimensions[2] <= otherDimensions[2];
        return fits;
    }
    
    public boolean fitsInWithFixedOrientation(Size otherSize) {
        if (getHeight() > otherSize.getHeight()) {
            return false;
        }
        int[] dimensions = { getLength(), getWidth() };
        Arrays.sort(dimensions);
        
        int[] otherDimensions = { otherSize.getLength(), otherSize.getWidth() };
        Arrays.sort(otherDimensions);
        
        boolean fits = dimensions[0] <= otherDimensions[0] &&
                dimensions[1] <= otherDimensions[1];
        return fits;
    }
    
}
