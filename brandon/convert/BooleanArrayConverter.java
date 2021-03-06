package brandon.convert;

public class BooleanArrayConverter extends ClassConverter {

   public BooleanArrayConverter() {
      super.baseClass = Boolean[].class;
      super.baseClassString = "Boolean[]";
   }
   
   @Override
   public Object convert(String arg) {
      String[] words = arg.substring(arg.indexOf("{ ")+2, arg.indexOf(" }")).split(", ");
      Boolean[] ints = new Boolean[words.length];
      for(int i = 0; i < words.length; i++) {
         ints[i] = Boolean.parseBoolean(words[i]);
      }
      return ints;
   }

   @Override
   public String toString(Object input) {
      StringBuilder sb = new StringBuilder();
      Boolean[] vals = (Boolean[])input;
      sb.append("{ ");
      for(int i = 0; i < vals.length; i++) {
         sb.append(""+vals[i]);
         if (i < vals.length-1) {
            sb.append(", ");
         }
      }
      sb.append("} \n");
      return sb.toString();
   }
}
