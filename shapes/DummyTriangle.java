package shapes;

import java.awt.Color;

public class DummyTriangle extends DummyShape {

   private double x = 0.0;
   private double y = 0.0;
   private double halfHeight = 0.0;
   private double halfLength = 0.0;
   
   public DummyTriangle(Color c, boolean filled, boolean sizeBased) {
      super("Triangle", c, filled, sizeBased);
   }

   public DummyTriangle(Color c, boolean filled) {
      super("Triangle", c,filled, false);
   }

   public int compareTo(Object o) {
      if (o instanceof DummyShape) {
         DummyShape d = (DummyShape) o;
         return this.getName().compareTo(d.getName());
      } else {
         return -1;
      }
   }

   public void setCoordinates(double x, double y, double halfLength, double halfHeight) {
      this.x = x; 
      this.y = y;
      this.halfLength = halfLength;
      this.halfHeight = halfHeight;
   }

   public boolean equals(Object o) {
      if (o instanceof DummyTriangle) {
         if (!this.getSize()){
            return super.equals(o);
         } else {
            DummyTriangle d = (DummyTriangle) o;
            return super.equals(o) && Math.abs(this.x - d.x) <= DummyShape.margin
               && Math.abs(this.y - d.y) <= DummyShape.margin
               && Math.abs(this.halfLength - d.halfLength) <= DummyShape.margin
               && Math.abs(this.halfHeight - d.halfHeight) <= DummyShape.margin;
         }
      } else if (o instanceof DummyShape) {
         DummyShape d = (DummyShape) o;
         return !this.getSize() && super.equals(d); 
      } else {
         return false;
      }
   }
}