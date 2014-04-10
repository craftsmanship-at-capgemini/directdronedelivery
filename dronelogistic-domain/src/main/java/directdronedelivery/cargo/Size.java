package directdronedelivery.cargo;

import java.util.Arrays;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class Size {
    
    @Getter int length;
    @Getter int width;
    @Getter int height;
    
    private Size() {
    }
    
    public static Size newSizeInMilimeters(int length, int width, int height) {
        Size cargoSize = new Size();
        cargoSize.length = length;
        cargoSize.width = width;
        cargoSize.height = height;
        
        return cargoSize;
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
