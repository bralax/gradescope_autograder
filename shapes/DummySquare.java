package shapes;

import java.awt.Color;

public class DummySquare extends DummyShape {
   
   private double halfLength = 0.0;
   private double x = 0.0;
   private double y = 0.0;

   public DummySquare(Color c, boolean filled, boolean sizeBased) {
      super("Square", c, filled, sizeBased);
   }

   public DummySquare(Color c, boolean filled) {
      super("Square", c,filled, false);
   }

   public int compareTo(Object o) {
      if (o instanceof DummyShape) {
         DummyShape d = (DummyShape) o;
         return this.getName().compareTo(d.getName());
      } else {
         return -1;
      }
   }

   public void setCoordinates(double x, double y, double halfLength) {
      this.x = x;
      this.y = y;
      this.halfLength = halfLength;
   }

   public boolean equals(Object o) {
      if (o instanceof DummySquare) {
         if (!this.getSize()){
            return super.equals(o);
         } else {
            DummySquare d = (DummySquare) o;
            return super.equals(o) && Math.abs(this.x - d.x) <= 0.5 &&
               Math.abs(this.y - d.y) <= 0.5 && Math.abs(this.halfLength - d.halfLength) <= 0.5;
         }
      } else if (o instanceof DummyShape) {
         DummyShape d = (DummyShape) o;
         return !this.getSize() && super.equals(d); 
      } else {
         return false;
      }
   }
}