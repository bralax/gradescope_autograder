package brandon.convert;

public class IntArrayConverter extends ClassConverter {

   public IntArrayConverter() {
      super.baseClass = int[].class;
      super.baseClassString = "int[]";
   }
   
   @Override
   public Object convert(String arg) {
      String[] words = arg.substring(arg.indexOf("{ ")+2, arg.indexOf(" }")).split(", ");
      int[] ints = new int[words.length];
      for(int i = 0; i < words.length; i++) {
         ints[i] = Integer.parseInt(words[i]);
      }
      return ints;
   }

   @Override
   public String toString(Object input) {
      StringBuilder sb = new StringBuilder();
      Integer[] vals = (Integer[])input;
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
