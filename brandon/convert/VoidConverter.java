package brandon.convert;

public class VoidConverter extends ClassConverter{

   public VoidConverter() {
      super.baseClass = void.class;
      super.baseClassString = "void";
   }
   
   @Override
   public Object convert(String arg) {
      return null;
   }

   @Override
   public String toString(Object input) {
      return "";
   }
}
