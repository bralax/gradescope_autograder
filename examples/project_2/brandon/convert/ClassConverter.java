package brandon.convert;

public abstract class ClassConverter {
   /**The class that this converter can create.
    Should be set in all subclasses. To acces the 
   Class<?> of a class, get it's .class attribute. 
   For example for String do String.class.*/
   protected Class<?> baseClass;

   protected String baseClassString;
   
   /**This method converts from an array of strings to the desired object.
    Each string will represent one parameter.
   @param the array of all the paremeters to make this object.
   @return the converted object.
   */
   public abstract Object convert(String args);

   @Override
   public boolean equals(Object other) {
      if (other instanceof ClassConverter) {
         return this.baseClass == ((ClassConverter) other).baseClass;
      } else if (other instanceof String) {
         return baseClassString.equals(other);
      } else {
         return this.baseClass == other.getClass();
      }
   }

   public Class<?> getClassType() {
      return this.baseClass;
   }

   public String getString() {
      return this.baseClassString;
   }
   
   /**This method converts the object to a represented string.
    This is used to get readable output of an object for grading.
   @param input the object to convert to string
   @return The string represenation of this object
   */
   public abstract String toString(Object input);
}
