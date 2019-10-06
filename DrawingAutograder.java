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
   output of using the StdDraw Library. 
   @see Autograder
   @author Brandon Lax
 */
public class DrawingAutograder extends Autograder {

   /**
      The constructor of a drawing Autograder.
    */
   public DrawingAutograder() {
      super();
   }

   /** Test to check whether the students code draws all expected shapes.
       WARNING: A students code may be 100% correct and still not pass this test.
       This test only checks the lists of shapes and their colors. If a student 
       uses a non-standard color (they calculate the RGB values themself) or
       use non-standard shapes (creating shapes using the polygon instead of
       the built in version) they may get the same output but fail the tests.
       The param size is to know whether to test where size matters (true if 
       you want to compare size) and order is to know whether the order of 
       the drawing matterss (this might be important to check layering)
      @param p the name of the students program to run
      @param size boolean false for size agnostic true for size dependent comparison
      @param order boolean false for order agnostic true for order dependent
      @param inFile the stdinput to be passed to the main method of the code
    */
   public void drawingDiffTest(String p,
                               boolean size,
                               boolean order,
                               String inFile) {
      String name = "Drawing Comparison Test";
      Object argList = (Object) new String[0];
      Class[] args = {String[].class};
      PrintStream original = System.out;
      InputStream origin = System.in;
      System.setOut(new PrintStream(
        new OutputStream() {
           public void write(int b) {}
        }));
      try {
         StdDraw.clearMoveList();
         if (inFile != null && inFile != "") {
            System.setIn(new FileInputStream(inFile));
         }
         Method m  = Autograder.getMethod(p, "main", args);
         if (m != null) {
            m.invoke(null, argList);
            ArrayList<DummyShape> opts = StdDraw.getMoveList();
            StdDraw.clearMoveList();
            System.in.close();
            if (inFile != null && inFile != "") {
               System.setIn(new FileInputStream(inFile));
            }
            m  = Autograder.getMethod(p+"Sample", "main", args);
            m.invoke(null, argList);
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
                  this.addTestResult(name, true, "The Student submission creates the same shapes as the sample \n" +
                                     "Make sure visually that the shapes are alll in the correct locations");
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
         } else {
            this.addTestResult(name, false, "ERROR - Student Submission Missing a Main Method");
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


   /** Test to check whether the students code draws all expected shapes.
       WARNING: A students code may be 100% correct and still not pass this test.
       This test only checks the lists of shapes and their colors. If a student 
       uses a non-standard color (they calculate the RGB values themself) or
       use non-standard shapes (creating shapes using the polygon instead of
       the built in version) they may get the same output but fail the tests.
       The param size is to know whether to test where size matters (true if 
       you want to compare size) and order is to know whether the order of 
       the drawing matterss (this might be important to check layering)
      @param p the name of the students program to run
      @param exp a lists containing all the expected shapes that the program should make
      @param size boolean false for size agnostic true for size dependent comparison
      @param order boolean false for order agnostic true for order dependent
      @param inFile the stdinput to be passed to the main method of the code
    */
   public void drawingDiffTest(String p,
                               ArrayList<DummyShape> exp,
                               boolean size,
                               boolean order,
                               String inFile) {
      String name = "Drawing Comparison Test";
      Object argList = (Object) new String[0];
      Class[] args = {String[].class};
      PrintStream original = System.out;
      InputStream origin = System.in;
      System.setOut(new PrintStream(
        new OutputStream() {
           public void write(int b) {}
        }));
      try {
         StdDraw.clearMoveList();
         if (inFile != null && inFile != "") {
            System.setIn(new FileInputStream(inFile));
         }
         Method m  = Autograder.getMethod(p, "main", args);
         if (m != null) {
            m.invoke(null, argList);
            ArrayList<DummyShape> opts = StdDraw.getMoveList();
            StdDraw.clearMoveList();
            System.in.close();
            for (DummyShape d : exp) {
               d.setSize(size);
            }
            if (opts.size() > exp.size()) {
               this.addTestResult(name, false, "The Student made more shapes than the sample \n"+
                                  listToString(opts, exp));
            } else if (opts.size() < exp.size()) {
               this.addTestResult(name, false, "The Student submission is missing shapes \n"+
                                  listToString(opts, exp));
            } else {
               int out = this.compare(opts, exp, order);
               if (out == -1) {
                  this.addTestResult(name, true, "The Student submission creates the same shapes as the sample \n" +
                                     "Make sure visually that the shapes are alll in the correct locations");
               } else {
                  if (order) {
                     this.addTestResult(name, false, "The Student and Sample did not match at position #"+out+
                                        " \n " + listToString(opts, exp));
                  } else {
                     this.addTestResult(name, false, "The Student Submission did not have a matching shape for"
                                        + " the sample shape at position #"+out+" \n "+
                                        listToString(opts, exp));
                  }
               }
            }
         } else {
            this.addTestResult(name, false, "ERROR - Student Submission Missing a Main Method");
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

   /** Test to check whether the students code draws an expected shape.
      @param p the name of the students program to run
      @param exp the shape the students code must contain
      @param inFile the std input to be passed to the main method of the code
    */
   public void drawingContainsTest(String p,
                               DummyShape exp,
                               String inFile) {
      String name = "Drawing Contains Shape Test";
      Object argList = (Object) new String[0];
      Class[] args = {String[].class};
      PrintStream original = System.out;
      InputStream origin = System.in;
      System.setOut(new PrintStream(
        new OutputStream() {
           public void write(int b) {}
        }));
      try {
         StdDraw.clearMoveList();
         if (inFile != null && inFile != "") {
            System.setIn(new FileInputStream(inFile));
         }
         Method m  = Autograder.getMethod(p, "main", args);
         if (m != null) {
            m.invoke(null, argList);
            ArrayList<DummyShape> opts = StdDraw.getMoveList();
            StdDraw.clearMoveList();
            System.in.close();
            boolean out = this.contains(opts, exp);
            if (out) {
               this.addTestResult(name, true, "The Student submission creates the expected shape of \n" +
                                  exp.toString());
            } else {
                  this.addTestResult(name, false, "The Student submission is missing the expected shape of \n" 
                                     + exp.toString());
            }
         } else {
            this.addTestResult(name, false, "ERROR - Student Submission Missing a Main Method");
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


   /**
      A method to compare two array lists of shapes.
      @param opts the shapes made by the student
      @param samp the expected shapes to be made
      @param order whether the order of the lists should matter
      @return -1 if equal, an index greater than or equal to zero representing the first index in the sample of a difference
    */
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

/**
      A method to find if an array lists has a shapes.
      @param opts the shapes made by the student
      @param samp the shape to find
      @return -1 if equal, an index greater than or equal to zero representing the first index in the sample of a difference
    */
   private boolean contains(ArrayList<DummyShape> opts, DummyShape samp) {
      int j = opts.indexOf(samp);
      return j > -1;
   }


   /**
      Helper method to take the lists and make them strings for output.
      @param student the students shape list
      @param sample the expected shape list
      @param a string containing output of all the shapes made by both programs
    */
   private String listToString(ArrayList<DummyShape> student, ArrayList<DummyShape> sample) {
      StringBuilder sb = new StringBuilder();
      sb.append("Shapes \n");
      sb.append("Sample \t Students \n");
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