package shapes;

import java.awt.Color;

public class DummyPoint extends DummyShape {

   private double x = 0.0;
   private double y = 0.0;
   private double r = 0.0;

   public DummyPoint(Color c, boolean sizeBased) {
      super("Point", c, true, sizeBased);
   }

   public DummyPoint(Color c) {
      super("Point", c,true, false);
   }

   public int compareTo(Object o) {
      if (o instanceof DummyShape) {
         DummyShape d = (DummyShape) o;
         return this.getName().compareTo(d.getName());
      } else {
         return -1;
      }
   }

   public void setCoordinates(double x, double y, double r) {
      this.x = x;
      this.y = y;
      this.r = r;
   }

   public boolean equals(Object o) {
      if (o instanceof DummyPoint) {
         if (!this.getSize()){
            return super.equals(o);
         } else {
            DummyPoint d = (DummyPoint) o;
            return super.equals(o) && Math.abs(this.x - d.x) <= DummyShape.margin
               && Math.abs(this.y - d.y) <= DummyShape.margin
               && Math.abs(this.r - d.r) <= DummyShape.margin;
         }
      } else if (o instanceof DummyShape) {
         DummyShape d = (DummyShape) o;
         return !this.getSize() && super.equals(d); 
      } else {
         return false;
      }
   }
}