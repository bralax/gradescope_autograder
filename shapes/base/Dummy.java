package shapes;

import java.awt.Color;

public class Dummy extends DummyShape {

   public Dummy(Color c, boolean filled, boolean sizeBased) {
      super("", c, filled, sizeBased);
   }

   public Dummy(Color c, boolean filled) {
      super("", c,filled, false);
   }

   public int compareTo(Object o) {
      if (o instanceof DummyShape) {
         DummyShape d = (DummyShape) o;
         return this.getName().compareTo(d.getName());
      } else {
         return -1;
      }
   }

   public void setCoordinates() {
      
   }

   public boolean equals(Object o) {
      if (o instanceof Dummy) {
         if (!this.getSize()){
            return super.equals(o);
         } else {
            Dummy d = (Dummy) o;
            return super.equals(o) && Math.abs(this. - d.) <= DummyShape.margin;
         }
      } else if (o instanceof DummyShape) {
         DummyShape d = (DummyShape) o;
         return !this.getSize() && super.equals(d); 
      } else {
         return false;
      }
   }
}