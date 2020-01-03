package brandon.convert;

public class CharConverter extends ClassConverter{

   public CharConverter() {
      super.baseClass = char.class;
      super.baseClassString = "char";
   }
   
   @Override
   public Object convert(String arg) {
      return arg.charAt(0);
   }

   @Override
   public String toString(Object input) {
      Character i = (Character) input;
      return i.toString();
   }
}
