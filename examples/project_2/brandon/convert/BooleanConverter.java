package brandon.convert;

public class BooleanConverter extends ClassConverter{

   public BooleanConverter() {
      super.baseClass = boolean.class;
      super.baseClassString = "boolean";
   }
   
   @Override
   public Object convert(String arg) {
      return Boolean.parseBoolean(arg);
   }

   @Override
   public String toString(Object input) {
      Boolean i = (Boolean) input;
      return i.toString();
   }
}
