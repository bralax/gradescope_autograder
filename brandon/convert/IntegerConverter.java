package brandon.convert;

public class IntegerConverter extends ClassConverter{

   public IntegerConverter() {
      super.baseClass = Integer.class;
      super.baseClassString = "Integer";
   }
   
   @Override
   public Object convert(String arg) {
      return Integer.parseInt(arg);
   }

   @Override
   public String toString(Object input) {
      Integer i = (Integer) input;
      return i.toString();
   }
}
