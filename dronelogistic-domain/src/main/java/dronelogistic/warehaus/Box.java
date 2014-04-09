package dronelogistic.warehaus;

import lombok.Getter;
import dronelogistic.orderinformations.Size;

public class Box {
    
    @Getter protected Integer boxID;
    @Getter protected Size size;
    @Getter protected int weightInGrams;
    @Getter protected BoxType boxType;
    
    public Box(int boxID, Size size, int weightInGrams, BoxType boxtype) {
        this.boxID = boxID;
        this.size = size;
        this.weightInGrams = weightInGrams;
        this.boxType = boxtype;
    }
    
    
    public boolean sameIdentityAs(final Box other) {
      return other != null && other.boxID.equals(other.boxID);
    }


    @Override
    public boolean equals(final Object object) {
      if (this == object) return true;
      if (object == null || getClass() != object.getClass()) return false;

      final Box other = (Box) object;
      return sameIdentityAs(other);
    }
}
