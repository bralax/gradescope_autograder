package brandon.convert;

public class StringArrayConverter extends ClassConverter {

   public StringArrayConverter() {
      super.baseClass = String[].class;
      super.baseClassString = "String[]";
   }
   
   @Override
   public Object convert(String arg) {
      String[] words = arg.substring(arg.indexOf("{ ")+2, arg.indexOf(" }")).split(", ");
      return words;
   }

   @Override
   public String toString(Object input) {
      StringBuilder sb = new StringBuilder();
      String[] vals = (String[])input;
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
