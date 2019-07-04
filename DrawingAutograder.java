import java.io.File;
import java.awt.Color;
import jh61b.grader.TestResult;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

/**
   Example child class of the Autograder.
   Has an additional test for comparing the 
   output of pictures. 
   @see Autograder
   @author Brandon Lax
 */
public class DrawingAutograder extends Autograder {

   /**
      The constructor of a picture Autograder.
    */
   public DrawingAutograder() {
      super();
   }

   /** Test.
      @param p the name of the students program to run
      @param size boolean false for size agnostic true for size dependent comparison
      @param order boolean false for order agnostic true for order dependent
    */
   public void drawingDiffTest(String p,
                               boolean size,
                               boolean order) {
      String[] args = {"String[]"};
      try {
         Method m  = Autograder.getMethod(p, "main", args);
         m.invoke(null);
      } catch(InvocationTargetException e) {
      } catch(IllegalAccessException e) {
      }
   }
}