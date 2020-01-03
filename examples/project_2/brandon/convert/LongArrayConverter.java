package brandon.convert;

public class LongArrayConverter extends ClassConverter {

   public LongArrayConverter() {
      super.baseClass = long[].class;
      super.baseClassString = "long[]";
   }
   
   @Override
   public Object convert(String arg) {
      String[] words = arg.substring(arg.indexOf("{ ")+2, arg.indexOf(" }")).split(", ");
      long[] ints = new long[words.length];
      for(int i = 0; i < words.length; i++) {
         ints[i] = Long.parseLong(words[i]);
      }
      return ints;
   }

   @Override
   public String toString(Object input) {
      StringBuilder sb = new StringBuilder();
      Long[] vals = (Long[])input;
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
