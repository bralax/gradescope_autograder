package brandon.convert;

public class CharacterConverter extends ClassConverter{

   public CharacterConverter() {
      super.baseClass = Character.class;
      super.baseClassString = "Character";
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
