package brandon.convert;

public class BoolArrayConverter extends ClassConverter {

   public BoolArrayConverter() {
      super.baseClass = boolean[].class;
      super.baseClassString = "boolean[]";
   }
   
   @Override
   public Object convert(String arg) {
      String[] words = arg.substring(arg.indexOf("{ ")+2, arg.indexOf(" }")).split(", ");
      boolean[] ints = new boolean[words.length];
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
