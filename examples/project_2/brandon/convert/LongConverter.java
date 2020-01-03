package brandon.convert;

public class LongConverter extends ClassConverter{

   public LongConverter() {
      super.baseClass = long.class;
      super.baseClassString = "long";
   }
   
   @Override
   public Object convert(String arg) {
      return Long.parseLong(arg);
   }

   @Override
   public String toString(Object input) {
      Long i = (Long) input;
      return i.toString();
   }
}
