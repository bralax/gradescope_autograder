package brandon.convert;

public class DoubleArrayConverter extends ClassConverter {

   public DoubleArrayConverter() {
      super.baseClass = double[].class;
      super.baseClassString = "double[]";
   }
   
   @Override
   public Object convert(String arg) {
      String[] words = arg.substring(arg.indexOf("{ ")+2, arg.indexOf(" }")).split(", ");
      double[] ints = new double[words.length];
      for(int i = 0; i < words.length; i++) {
         ints[i] = Double.parseDouble(words[i]);
      }
      return ints;
   }

   @Override
   public String toString(Object input) {
      StringBuilder sb = new StringBuilder();
      Double[] vals = (Double[])input;
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
