import java.awt.Color;

public class DummySquare extends DummyShape {
   
   private int sideLength;

   public DummySquare(Color c, boolean sizeBased) {
      super("Circle", c, sizeBased);
   }

   public DummySquare(Color c) {
      super("Circle", c, false);
   }

   public int compareTo(Object o) {
      if (o instanceof DummySquare) {
         DummySquare d = (DummySquare) o;
         return this.getName().compareTo(d.getName());
      } else {
         return -1;
      }
   }

   public boolean equals(Object o) {
      if (o instanceof DummySquare) {
         return super.equals(o);
      } else if (o instanceof DummyShape) {
         DummyShape d = (DummyShape) o;
         return !this.getSize() && super.equals(d); 
      } else {
         return false;
      }
   }
}