package brandon.convert;

public class DoubleConverter extends ClassConverter{

   public DoubleConverter() {
      super.baseClass = double.class;
      super.baseClassString = "double";
   }
   
   @Override
   public Object convert(String arg) {
      return Double.parseDouble(arg);
   }

   @Override
   public String toString(Object input) {
      Double i = (Double) input;
      return i.toString();
   }
}
