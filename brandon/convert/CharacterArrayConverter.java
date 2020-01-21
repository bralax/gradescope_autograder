package brandon.convert;

public class CharacterArrayConverter extends ClassConverter {

   public CharacterArrayConverter() {
      super.baseClass = Character[].class;
      super.baseClassString = "Character[]";
   }
   
   @Override
   public Object convert(String arg) {
      String[] words = arg.substring(arg.indexOf("{ ")+2, arg.indexOf(" }")).split(", ");
      Character[] letters = new Character[words.length];
      for(int w = 0; w < words.length; w++) {
         letters[w] = words[w].charAt(0);
      }
      return letters;
   }

   @Override
   public String toString(Object input) {
      StringBuilder sb = new StringBuilder();
      Character[] vals = (Character[])input;
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
