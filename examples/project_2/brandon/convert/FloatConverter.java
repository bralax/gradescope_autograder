package brandon.convert;

public class FloatConverter extends ClassConverter{

   public FloatConverter() {
      super.baseClass = float.class;
      super.baseClassString = "Float";
   }
   
   @Override
   public Object convert(String arg) {
      return Float.parseFloat(arg);
   }

   @Override
   public String toString(Object input) {
      Float i = (Float) input;
      return i.toString();
   }
}
