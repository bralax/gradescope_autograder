package shapes;

import java.awt.Color;

public class DummyLine extends DummyShape {

   private double x0 = 0.0;
   private double x1 = 0.0;
   private double y0 = 0.0;
   private double y1 = 0.0;

   public DummyLine(Color c, boolean sizeBased) {
      super("Line", c, true, sizeBased);
   }

   public DummyLine(Color c) {
      super("Line", c,true, false);
   }

   public int compareTo(Object o) {
      if (o instanceof DummyShape) {
         DummyShape d = (DummyShape) o;
         return this.getName().compareTo(d.getName());
      } else {
         return -1;
      }
   }

   public void setCoordinates(double x0, double x1, double y0, double y1) {
      this.x0 = x0;
      this.x1 = x1;
      this.y0 = y0;
      this.y1 = y1;
   }

   public boolean equals(Object o) {
      if (o instanceof DummyLine) {
         if (!this.getSize()){
            return super.equals(o);
         } else {
            DummyLine d = (DummyLine) o;
            return super.equals(o) && Math.abs(this.x0 - d.x0) <= DummyShape.margin
               && Math.abs(this.x1 - d.x1) <= DummyShape.margin
               && Math.abs(this.y0 - d.y0) <= DummyShape.margin
               && Math.abs(this.y1 - d.y1) <= DummyShape.margin;
         }
      } else if (o instanceof DummyShape) {
         DummyShape d = (DummyShape) o;
         return !this.getSize() && super.equals(d); 
      } else {
         return false;
      }
   }
}