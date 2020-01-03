package brandon.convert;

public class StringConverter extends ClassConverter {

   public StringConverter() {
      super.baseClass = String.class;
      super.baseClassString = "String";
   }
   
   @Override
   public Object convert(String arg) {
      return arg;
   }

   @Override
   public String toString(Object input) {
      return input.toString();
   }
}
