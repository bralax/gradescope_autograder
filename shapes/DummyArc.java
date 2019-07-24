package shapes;

import java.awt.Color;

public class DummyArc extends DummyShape {

   private double x = 0.0;
   private double y = 0.0;
   private double radius = 0.0;
   private double angle1 = 0.0;
   private double angle2 = 0.0;

   public DummyArc(Color c,  boolean sizeBased) {
      super("Arc", c, true, sizeBased);
   }

   public DummyArc(Color c) {
      super("Arc", c,true, false);
   }

   public int compareTo(Object o) {
      if (o instanceof DummyShape) {
         DummyShape d = (DummyShape) o;
         return this.getName().compareTo(d.getName());
      } else {
         return -1;
      }
   }

   public void setCoordinates(double x, double y, double r, double a1, double a2) {
      this.x = x;
      this.y = y;
      this.radius = r;
      this.angle1 = a1;
      this.angle2 = a2;
   }

   public boolean equals(Object o) {
      if (o instanceof DummyArc) {
         if (!this.getSize()){
            return super.equals(o);
         } else {
            DummyArc d = (DummyArc) o;
            return super.equals(o) && Math.abs(this.x - d.x) <= DummyShape.margin
               && Math.abs(this.y - d.y) <= DummyShape.margin
               && Math.abs(this.radius - d.radius) <= DummyShape.margin
               && Math.abs(this.angle1 - d.angle1) <= DummyShape.margin
               && Math.abs(this.angle2 - d.angle2) <= DummyShape.margin;
         }
      } else if (o instanceof DummyShape) {
         DummyShape d = (DummyShape) o;
         return !this.getSize() && super.equals(d); 
      } else {
         return false;
      }
   }
}