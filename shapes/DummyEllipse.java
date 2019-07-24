package shapes;

import java.awt.Color;

public class DummyEllipse extends DummyShape {

   private double x = 0.0;
   private double y = 0.0;
   private double major = 0.0;
   private double minor = 0.0;

   public DummyEllipse(Color c, boolean filled, boolean sizeBased) {
      super("Ellipse", c, filled, sizeBased);
   }

   public DummyEllipse(Color c, boolean filled) {
      super("Ellipse", c,filled, false);
   }

   public int compareTo(Object o) {
      if (o instanceof DummyShape) {
         DummyShape d = (DummyShape) o;
         return this.getName().compareTo(d.getName());
      } else {
         return -1;
      }
   }

   public void setCoordinates(double x, double y, double major, double minor) {
      this.x = x;
      this.y = y;
      this.major = major;
      this.minor = minor;
   }

   public boolean equals(Object o) {
      if (o instanceof DummyEllipse) {
         if (!this.getSize()){
            return super.equals(o);
         } else {
            DummyEllipse d = (DummyEllipse) o;
            return super.equals(o) && Math.abs(this.x - d.x) <= DummyShape.margin
               && Math.abs(this.y - d.y) <= DummyShape.margin
               && Math.abs(this.major - d.major) <= DummyShape.margin
               && Math.abs(this.minor - d.minor) <= DummyShape.margin;
         }
      } else if (o instanceof DummyShape) {
         DummyShape d = (DummyShape) o;
         return !this.getSize() && super.equals(d); 
      } else {
         return false;
      }
   }
}