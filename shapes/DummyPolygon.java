package shapes;

import java.awt.Color;

public class DummyPolygon extends DummyShape {

   private double[] x = {0};
   private double[] y = {0};

   public DummyPolygon(Color c, boolean filled, boolean sizeBased) {
      super("Polygon", c, filled, sizeBased);
   }

   public DummyPolygon(Color c, boolean filled) {
      super("Polygon", c,filled, false);
   }

   public int compareTo(Object o) {
      if (o instanceof DummyShape) {
         DummyShape d = (DummyShape) o;
         return this.getName().compareTo(d.getName());
      } else {
         return -1;
      }
   }

   public void setCoordinates(double[] x, double[] y) {
      this.x = x;
      this.y = y;
   }

   public boolean equals(Object o) {
      if (o instanceof DummyPolygon) {
         if (!this.getSize()){
            return super.equals(o);
         } else {
            DummyPolygon d = (DummyPolygon) o;
            if (this.x.length != d.x.length || this.y.length != d.y.length) {
               return false;
            }
            boolean same = true;
            for (int i = 0; i < this.x.length; i++) {
               same = same && Math.abs(this.x[i] - d.x[i]) <= DummyShape.margin
                  && Math.abs(this.y[i] - d.y[i]) <= DummyShape.margin;
            }
            return super.equals(o) && same;
         }
      } else if (o instanceof DummyShape) {
         DummyShape d = (DummyShape) o;
         return !this.getSize() && super.equals(d); 
      } else {
         return false;
      }
   }
}