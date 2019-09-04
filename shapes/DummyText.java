package shapes;

import java.awt.Color;

public class DummyText extends DummyShape {

   private String text;
   private String justify;

   public DummyText(Color c) {
      super("Text", c, false);
   }

   public void setText(String text) {
      this.text = text;
   }

   public String getText() {
      return this.text;
   }

   public void setJustify(String justify) {
      this.justify = justify;
   }

   public String getJustify() {
      return this.justify;
   }

   public int compareTo(Object o) {
      if (o instanceof DummyShape) {
         DummyShape d = (DummyShape) o;
         return this.getName().compareTo(d.getName());
      } else {
         return -1;
      }
   }

   public boolean equals(Object o) {
      if (o instanceof DummyText) {
         DummyText d = (DummyText) o;
         return d.text.equals(this.text);
      }
      return false;
   }

   public String toString() {
      return super.toString() + " Text: " + this.text + " Justify: " + this.justify;
   }
}