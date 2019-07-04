import java.awt.Color;

public abstract class DummyShape implements Comparable {
   
   private String name;
   private Color color;
   private boolean sizeBased;

   public DummyShape(String newName, Color c, boolean sizeBased) {
      this.name = newName;
      this.sizeBased = sizeBased;
      this.color = c;
   }

   public DummyShape(String newName, Color c) {
      this.name = newName;
      this.color = c;
      this.sizeBased = false;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String newName) {
      this.name = newName;
   }

   public String getColor() {
      return this.name;
   }

   public void setColor(Color c) {
      this.color = c;
   }

   public boolean getSize() {
      return this.sizeBased;
   }

   public void setName(boolean newSize) {
      this.sizeBased = newSize;
   }

   public abstract int compareTo(Object o);

   public boolean equals(Object o) {
      if (o instanceof DummyShape) {
         DummyShape d = (DummyShape) o;
         return this.getName().equals(d.getName()) && this.getColor().equals(d.getColor());
      }
      return false;
   }
}