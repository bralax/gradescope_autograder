import java.io.File;
import java.awt.Color;
import jh61b.grader.TestResult;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import shapes.DummyShape;
import java.util.ArrayList;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.IOException;

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
                               boolean order,
                               String inFile) {
      String name = "Drawing Comparison Test";
      String[] args = {"String[]"};
      PrintStream original = System.out;
      InputStream origin = System.in;
      System.setOut(new PrintStream(
        new OutputStream() {
           public void write(int b) {}
        }));
      try {
         StdDraw.clearMoveList();
         System.setIn(new FileInputStream(inFile));
         Method m  = Autograder.getMethod(p, "main", args);
         m.invoke(null);
         ArrayList<DummyShape> opts = StdDraw.getMoveList();
         StdDraw.clearMoveList();
         System.in.close();
         System.setIn(new FileInputStream(inFile));
         m  = Autograder.getMethod(p+"Sample", "main", args);
         m.invoke(null);
         ArrayList<DummyShape> sampleOpts = StdDraw.getMoveList();
         StdDraw.clearMoveList();
         for (DummyShape d : sampleOpts) {
            d.setSize(size);
         }
         if (opts.size() > sampleOpts.size()) {
            this.addTestResult(name, false, "The Student made more shapes than the sample \n"+
                               listToString(opts, sampleOpts));
         } else if (opts.size() < sampleOpts.size()) {
            this.addTestResult(name, false, "The Student submission is missing shapes \n"+
                               listToString(opts, sampleOpts));
         } else {
            int out = this.compare(opts, sampleOpts, order);
            if (out == -1) {
               this.addTestResult(name, true, "The Student ssubmission makes the same shapes as the sample" +
                                  "Make sure visually that they are in the correct locations");
            } else {
               if (order) {
                  this.addTestResult(name, false, "The Student and Sample did not match at position #"+out+
                                     " \n " + listToString(opts, sampleOpts));
               } else {
                  this.addTestResult(name, false, "The Student Submission did not have a matching shape for"
                                     + " the sample shape at position #"+out+" \n "+
                                     listToString(opts, sampleOpts));
               }
            }
         }
      } catch(InvocationTargetException e) {
         Throwable et = e.getCause();
         Exception es;
         if(et instanceof Exception) {
            es = (Exception) et;
         } else {
            es = e;
         }
         this.addTestResult(name, false, "ERROR: "+p+" Threw " + es + " With Stack Trace: " + stackTraceToString(es));
      } catch(IllegalAccessException e) {
         this.addTestResult(name, false, "ERROR: Students Code Not Accessible");
      } catch(IOException e) {
         this.addTestResult(name, false, "ERROR: Input File Failed to Open Or Close");
      }
      System.setIn(origin);
      System.setOut(original);
   }


   private int compare(ArrayList<DummyShape> opts, ArrayList<DummyShape> samp, boolean order) {
      if (!order) {
         opts = new ArrayList<DummyShape>(opts);
      }
      for (int i = 0; i < samp.size(); i++) {
         int j = opts.indexOf(samp.get(i));
         if (j == -1) {
            return i;
         }
         if (!order) {
            opts.remove(j);
         } else if (j != i) {
            return i;
         }
      }
      return -1;
   }

   private String listToString(ArrayList<DummyShape> student, ArrayList<DummyShape> sample) {
      StringBuilder sb = new StringBuilder();
      sb.append("Shapes");
      sb.append("Sample \t Students");
      for (int i = 0; i < Math.max(student.size(), sample.size()); i++) {
         sb.append(""+i+". ");
         if (i < sample.size()) {
            sb.append(sample.get(i).toString());
         }
         sb.append("\t");
         if (i < student.size()) {
            sb.append(student.get(i).toString());
         }
         sb.append("\n");
      } 
      return sb.toString();
   }
}