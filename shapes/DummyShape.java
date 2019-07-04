package shapes;

import java.awt.Color;

public abstract class DummyShape implements Comparable {

   public static final double margin = 0.5;
   
   private String name;
   private Color color;
   private boolean sizeBased;
   private boolean filled;

   public DummyShape(String newName, Color c, boolean filled, boolean sizeBased) {
      this.name = newName;
      this.sizeBased = sizeBased;
      this.color = c;
      this.filled = filled;
   }

   public DummyShape(String newName, Color c, boolean filled) {
      this.name = newName;
      this.color = c;
      this.sizeBased = false;
      this.filled = filled;
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

   public boolean getFilled() {
      return this.filled;
   }

   public void setFilled(boolean newFill) {
      this.filled = newFill;
   }

   public boolean getSize() {
      return this.sizeBased;
   }

   public void setSize(boolean newSize) {
      this.sizeBased = newSize;
   }

   public abstract int compareTo(Object o);

   public boolean equals(Object o) {
      if (o instanceof DummyShape) {
         DummyShape d = (DummyShape) o;
         return this.getName().equals(d.getName()) && this.getColor().equals(d.getColor()) &&
            this.filled == d.filled;
      }
      return false;
   }
}