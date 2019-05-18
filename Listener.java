import java.util.List;
import java.util.ArrayList;
import jh61b.grader.TestResult; 
import jh61b.junit.JUnitUtilities;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.Failure;
import org.junit.runner.Description;

public class Listener extends RunListener {

   private List<TestResult> allResults;
   private TestResult current;
   private int unitNum;
   private double maxScore;
   private String name;

   public Listener(double max, int unit, String program) {
      this.allResults = new ArrayList<TestResult>();
      this.maxScore = max;
      this.unitNum = unit;
      this.name = program;
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
                                    "" + this.unitNum, this.maxScore, "hidden");
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

   public List<TestResult> allResults() {
      return this.allResults;
   }

   public int unitNum() {
      return this.unitNum;
   }
}
