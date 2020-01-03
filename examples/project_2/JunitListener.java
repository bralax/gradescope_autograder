import java.util.List;
import java.util.ArrayList;
import jh61b.grader.TestResult; 
import jh61b.junit.JUnitUtilities;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.Failure;
import org.junit.runner.Description;


/**
   A custom built Junit listener to work with the autograder.
   It allows for testing whether a method fails or passes.
   @author Brandon Lax
*/
public class JunitListener extends RunListener {

   /**All the test result created by the junits.*/
   private List<TestResult> allResults;
   /**The current test that is running.*/
   private TestResult current;
   /**A counter to keep track of teh current test number.*/
   private int unitNum;
   /**The point value for the test.*/
   private double maxScore;
   /**The name of the java file being tested.*/
   private String name;
   /**The visibility of the test.*/
   private String visibility;

   /**
      The constructor of the listener.
      @param max the score possible for the tests
      @param unit the current test number the program is on
      @param program the name of the java class
      @param visible the visibility of the tests
    */
   public JunitListener(double max, int unit, String program, String visible) {
      this.allResults = new ArrayList<TestResult>();
      this.maxScore = max;
      this.unitNum = unit;
      this.name = program;
      this.visibility = visible;
   }

   @Override
   public void testFinished(Description description) {
      if(this.current.getScore() == this.maxScore) {
         this.current.addOutput("Success: This Test Passed!");
      }
      this.allResults.add(this.current);
      this.unitNum++;
   }

   @Override
   public void testStarted(Description description) {
      
      this.current = new TestResult(this.name + ": " +
                                    description.getMethodName(), 
                                    "" + this.unitNum, this.maxScore, this.visibility);
      this.current.setScore(this.maxScore);
   }

   @Override
   public void testFailure(Failure failure) {
      this.current.setScore(0);
      this.current.addOutput(JUnitUtilities.failureToString(failure));
   }

   @Override
   public void testAssumptionFailure(Failure failure) {
      this.current.setScore(0);
      this.current.addOutput(JUnitUtilities.failureToString(failure));
   }

   /**Method to reutn the test created by the junits.
    This is used to add these tests to the autograder list.
    @return the list of tests listened to.
   */
   public List<TestResult> allResults() {
      return this.allResults;
   }

   /**
      Method to get the current unit test number.
      @return the current unit test number (starts at Autograder.diffNum not 0)
    */
   public int unitNum() {
      return this.unitNum;
   }
}
