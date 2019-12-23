import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import jh61b.grader.TestResult; 

import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.AuditListener;

/**This is a listener for checkstyle. 
   It is designed to follow the rules of checkstyle for gateway java.
   We grade by only taking off once per type of error. Therefore rather
   than the default output, we group errors by type Listing every instance
   of a specific issue on a single line.*/
public class GatewayCheckstyleListener implements AuditListener {

   /**All the test result created by checkstyling files..*/
   private static List<TestResult> allResults;
   /**The point value for the test.*/
   private static double maxScore = 1.0;
   /**The point value lost per error.*/
   private static double value = 0.1;
   /**The visibility of the test.*/
   private static String visibility = "hidden";
   /**All the errors from the checkstyling..*/
   private List<AuditEvent> checks;
   /**The exception thrown while testing.*/
   private Throwable except; 
   /**
      The constructor of the listener.
   */
   public GatewayCheckstyleListener() {
      GatewayCheckstyleListener.allResults = new ArrayList<TestResult>();
   }

   /**
      Sets the values for the score and visibility of checkstyle tests.
      @param max the maximum score you can get for checkstyle
      @param lossVal the number of points lost for every mistake
      @param visibil the visibility of each test
    */
   public static void setDefaultValues(double max, double lossVal, String visibil) {
      GatewayCheckstyleListener.maxScore = max;
      GatewayCheckstyleListener.value = lossVal;
      GatewayCheckstyleListener.visibility = visibil;
   }

   /**
      A method to return all the tests run from an audit.
      @return the list of tests
    */
   public static List<TestResult> getResults() {
      return GatewayCheckstyleListener.allResults;
   }

   @Override
   public void auditStarted(AuditEvent aEvt) {
      GatewayCheckstyleListener.allResults = new ArrayList<TestResult>();
   }

   @Override
   public void auditFinished(AuditEvent aEvt) {
      
   }

   @Override
   public void fileStarted(AuditEvent aEvt) {
      this.checks = new ArrayList<AuditEvent>();
      this.except = null;
   }

   @Override
   public void fileFinished(AuditEvent aEvt) {
      String fileName = aEvt.getFileName();
      int index = fileName.lastIndexOf('/');
      fileName = fileName.substring(index+1, fileName.length());
      String name = fileName + " Checkstyle Compiliant";
      TestResult t = new TestResult(name ,"Pre-Test", maxScore , visibility);   
      if (this.except != null) {
         t.setScore(0.0);
         t.addOutput("ERROR: Checkstyle crashed while processing file. \n Exception Thrown: \n"+ this.except.getMessage() + "\n" + this.except.getStackTrace());
         GatewayCheckstyleListener.allResults.add(t);
      } else {
         this.checks.sort(new compareEvent());
         AuditEvent last;
         int listLength = this.checks.size();
         if (listLength > 1) {
            double score = maxScore;
            t.addOutput("ERROR: File has the following checkstyle error(s): \n");
            last = this.checks.get(0);
            List<Integer> rows = new ArrayList<>();
            rows.add(last.getLine());
            for (int i = 1; i < listLength; i++) {
               AuditEvent next = this.checks.get(i);
               if (next == null || next.getSourceName() == null){
                  System.err.println("Bad AuditEvent");
               } else {
                  String lastName = getCheckShortName(last);
                  String nextName = getCheckShortName(next);
                  if (!lastName.equals(nextName)) {
                     score -= value;
                     t.addOutput(createLine(last, rows));
                     t.addOutput("\n");
                     rows.clear();
                  }
                  rows.add(next.getLine());
                  last = next;

               }
            }
            t.addOutput(createLine(last, rows));
            score -= value;
            score = (score >= 0.0) ? score : 0.0;
            t.setScore(score);
            GatewayCheckstyleListener.allResults.add(t);
         } else if (listLength == 1) {
            t.addOutput("ERROR: File has the following checkstyle error(s): \n");
            List<Integer> rows = new ArrayList<>();
            rows.add(this.checks.get(0).getLine());
            t.addOutput(createLine(this.checks.get(0), rows));
            GatewayCheckstyleListener.allResults.add(t);
            //Only one checkstyle error
         } else {
            //Completely Passed
            t.addOutput("SUCCESS: File is 100% Checkstyle Compliant");
            t.setScore(maxScore);
            GatewayCheckstyleListener.allResults.add(t);
         }
      }
   }

   @Override
   public void addError(AuditEvent aEvt) {
      this.checks.add(aEvt);
   }

   @Override
   public void addException(AuditEvent aEvt, Throwable aThrowable) {
      this.checks.add(aEvt);
      this.except = aThrowable;
   }

   /**
      Helper method to convert exception to output string.
      @param aEvt The rule that had been violated
      @param rows all the lines on which the rule had been violated
      @returns the error in string format
    */
   private String createLine(AuditEvent aEvt, List<Integer> rows) {
      StringBuilder sb = new StringBuilder();
      sb.append("[");
      sb.append(getCheckShortName(aEvt));
      sb.append("]  Lines:   ");
      for(Integer i: rows) {
         sb.append(i);
         sb.append(",   ");
      }
      return sb.toString();
   }
    /**
     * Gets the short name of a checkstyle error.
     * Taken from the checkstyle codebase.
     * @see com.puppycrawl.tools.checkstyle.AuditEventDefaultFormatter
     * @param event audit event.
     * @return check name without 'Check' suffix.
     */
    private static String getCheckShortName(AuditEvent event) {
        final String checkFullName = event.getSourceName();
        final String checkShortName;
        final int lastDotIndex = checkFullName.lastIndexOf('.');
        if (lastDotIndex == -1) {
            if (checkFullName.endsWith("Check")) {
                checkShortName = checkFullName.substring(0, checkFullName.lastIndexOf("Check"));
            }
            else {
                checkShortName = checkFullName;
            }
        }
        else {
            if (checkFullName.endsWith("Check")) {
                checkShortName = checkFullName.substring(lastDotIndex + 1,
                    checkFullName.lastIndexOf("Check"));
            }
            else {
                checkShortName = checkFullName.substring(lastDotIndex + 1);
            }
        }
        return checkShortName;
    }

   /**Comparison class to be able to sort AuditEvents by cause and line number.*/
   private class compareEvent implements Comparator<AuditEvent> {

      @Override
      public int compare(AuditEvent a, AuditEvent b) {
         int diff = a.getSourceName().compareTo(b.getSourceName());
         if (diff == 0) {
            return a.getLine() - b.getLine();
         } else {
            return diff;
         }
      }
   }

}
