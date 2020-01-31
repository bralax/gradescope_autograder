package shapes;

import java.awt.Color;

public class DummyTriangle extends DummyShape {

   private double x1 = 0.0;
   private double y1 = 0.0;
   private double x2 = 0.0;
   private double y2 = 0.0;
   private double x3 = 0.0;
   private double y3 = 0.0;
   //private double halfHeight = 0.0;
   //private double halfLength = 0.0;
   
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

   public void setCoordinates(double x1, double y1, double x2, double y2, double x3, double y3) {
      this.x1 = x1; 
      this.y1 = y1;
      this.x2 = x2; 
      this.y2 = y2;
      this.x3 = x3; 
      this.y3 = y3;
      //this.halfLength = halfLength;
      //this.halfHeight = halfHeight;
   }

   public boolean equals(Object o) {
      if (o instanceof DummyTriangle) {
         if (!this.getSize()){
            return super.equals(o);
         } else {
            DummyTriangle d = (DummyTriangle) o;
            return super.equals(o) && Math.abs(this.x1 - d.x1) <= DummyShape.margin
               && Math.abs(this.y1 - d.y1) <= DummyShape.margin
               && Math.abs(this.x2 - d.x2) <= DummyShape.margin
               && Math.abs(this.y2 - d.y2) <= DummyShape.margin
               && Math.abs(this.x3 - d.x3) <= DummyShape.margin
               && Math.abs(this.y3 - d.y3) <= DummyShape.margin
         }
      } else if (o instanceof DummyShape) {
         DummyShape d = (DummyShape) o;
         return !this.getSize() && super.equals(d); 
      } else {
         return false;
      }
   }
}
