package shapes;

import java.awt.Color;

public class DummyRectangle extends DummyShape {
   
   private double halfHeight = 0.0;
   private double halfWidth = 0.0;
   private double x = 0.0;
   private double y = 0.0;

   public DummyRectangle(Color c, boolean filled, boolean sizeBased) {
      super("Rectange", c, filled, sizeBased);
   }

   public DummyRectangle(Color c, boolean filled) {
      super("Rectangle", c,filled, false);
   }

   public int compareTo(Object o) {
      if (o instanceof DummyShape) {
         DummyShape d = (DummyShape) o;
         return this.getName().compareTo(d.getName());
      } else {
         return -1;
      }
   }

   public void setCoordinates(double x, double y, double halfWidth, double halfHeight) {
      this.x = x;
      this.y = y;
      this.halfWidth = halfWidth;
      this.halfHeight = halfHeight;
   }

   public boolean equals(Object o) {
      if (o instanceof DummyRectangle) {
         if (!this.getSize()){
            return super.equals(o);
         } else {
            DummyRectangle d = (DummyRectangle) o;
            return super.equals(o) && Math.abs(this.x - d.x) <= DummyShape.margin &&
               Math.abs(this.y - d.y) <= DummyShape.margin 
               && Math.abs(this.halfWidth - d.halfWidth) <= DummyShape.margin
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