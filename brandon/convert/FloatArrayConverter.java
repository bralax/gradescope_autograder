package brandon.convert;

public class FloatArrayConverter extends ClassConverter {

   public FloatArrayConverter() {
      super.baseClass = float[].class;
      super.baseClassString = "float[]";
   }
   
   @Override
   public Object convert(String arg) {
      String[] words = arg.substring(arg.indexOf("{ ")+2, arg.indexOf(" }")).split(", ");
      float[] ints = new float[words.length];
      for(int i = 0; i < words.length; i++) {
         ints[i] = Float.parseFloat(words[i]);
      }
      return ints;
   }

   @Override
   public String toString(Object input) {
      StringBuilder sb = new StringBuilder();
      Float[] vals = (Float[])input;
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
