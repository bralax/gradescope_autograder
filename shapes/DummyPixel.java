package shapes;

import java.awt.Color;

public class DummyPixel extends DummyShape {

   private double x = 0.0;
   private double y = 0.0;

   public DummyPixel(Color c, boolean sizeBased) {
      super("Pixel", c, true, sizeBased);
   }

   public DummyPixel(Color c) {
      super("Pixel", c,true, false);
   }

   public int compareTo(Object o) {
      if (o instanceof DummyShape) {
         DummyShape d = (DummyShape) o;
         return this.getName().compareTo(d.getName());
      } else {
         return -1;
      }
   }

   public void setCoordinates(double x, double y) {
      this.x = x;
      this.y = y;
   }

   public boolean equals(Object o) {
      if (o instanceof DummyPixel) {
         if (!this.getSize()){
            return super.equals(o);
         } else {
            DummyPixel d = (DummyPixel) o;
            return super.equals(o) && Math.abs(this.x - d.x) <= DummyShape.margin
               && Math.abs(this.y - d.y) <= DummyShape.margin;
         }
      } else if (o instanceof DummyShape) {
         DummyShape d = (DummyShape) o;
         return !this.getSize() && super.equals(d); 
      } else {
         return false;
      }
   }
}