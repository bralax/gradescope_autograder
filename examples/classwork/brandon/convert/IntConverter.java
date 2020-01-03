package brandon.convert;

public class IntConverter extends ClassConverter{

   public IntConverter() {
      super.baseClass = int.class;
      super.baseClassString = "int";
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
