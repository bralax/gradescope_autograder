import java.util.List;
import java.util.ArrayList;
import jh61b.grader.TestResult; 
import java.util.Scanner;  //to read in file of diff results
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import javax.tools.ToolProvider;
import javax.tools.JavaCompiler;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.io.FileReader;
import java.lang.ProcessBuilder.Redirect;
import org.junit.runner.notification.RunListener;
import org.junit.runner.JUnitCore;

/**
   Classs representing an autograder.\n
   It's main method is the running of the autograder
   and instances can be made to store all important information.
   @author Brandon Lax
*/
public class Autograder {

   /** The value of each test.*/
   protected double maxScore;
   
   /**The current test number we are on.*/
   protected int diffNum;

   /**The visibilty for the current gradescope test.*/
   protected String visibility;

   /**The list of all tests performed.*/
   private List<TestResult> allTestResults;
   
   /**The current junit test.*/
   private TestResult currentJunitTestResult;

   /**The location of the checkstyle Jar.*/
   public static final String CHECKSTYLE_JAR = "/autograder/source/checkstyle/checkstyle-8.10.1-all.jar";

   /**The location of the checkstyle xml.*/
   public static final String CHECKSTYLE_XML = "/autograder/source/checkstyle/check112.xml";

   /**
      The Autograder class constructor.\n
      Initializes the list of all tests.
      @param visible The visibility of the result to students see {@link #setVisibility(int) setvisibility}
      @param score The amount of points a test is worth
   */
   public Autograder(int visible, double score) {
      this.allTestResults = new ArrayList<TestResult>();
      this.diffNum = 1;
      this.setVisibility(visible);
      this.setScore(score);
   }

   /**
      The Autograder class constructor.\n
      Initializes the list of all tests.\n
      Also sets the visibility to hidden and 
      the score to 0.1
   */
   public Autograder() {
      this(1, 0.1);
   }
   
   /** Method to add a seperately made test to the results.\n
       This allows for people to make child classes of the autograder
       if they need tests that dont currently exist that they would 
       prefer to avoid adding to this class. For an example see
       {@link PictureAutograder}
       @param t the test to be added to the output
    */
   public void addTestResult(TestResult t) {
      this.allTestResults.add(t);
   }
   
   
   /** This is the wrap-up code of the autograder.\n
       <b>Must be the last line of the main method.</b> \nIt
       prints all of the results in a JSON format to 
       standard out.
       @throws Exception fails to create json for a test 
    */
   public void testRunFinished() throws Exception {  
      /* Dump allTestResults to StdOut in JSON format. */
      ArrayList<String> objects = new ArrayList<String>();
      for (TestResult tr : this.allTestResults) {
         objects.add(tr.toJSON());
      }
      String testsJSON = String.join(",", objects);
      
      System.out.println("{" + String.join(",", new String[] {
               String.format("\"tests\": [%s]", testsJSON)}) + "}");
   }

   /**
    * Test to check if source file exists.\n
    * Will output whether the file exists as well as 
    * add a junit test for it.
    * @param programName the program name (can include or not include the .java)
    * @return whether or not the source exists
    */
   public boolean testSourceExists(String programName) {
      boolean sourceExists = false;
   
      File source;
      if (programName.indexOf(".") == -1) {
         source = new File(programName + ".java");
      } else {
         source = new File(programName);
      }
      TestResult trSourceFile = new TestResult(programName +
                                               " Source File Exists", 
                                               "Pre-Test",
                                               this.maxScore, 
                                               this.visibility);
      
      if (!source.exists() || source.isDirectory()) { // source not present
         trSourceFile.setScore(0);
         trSourceFile.addOutput("ERROR: file " + programName +
                                 ".java is not present!\n");
         trSourceFile.addOutput("\tCheck the spelling of your file name.\n");
      } else { // source present
         trSourceFile.setScore(this.maxScore);
         trSourceFile.addOutput("SUCCESS: file " + programName +
                                 ".java is present!\n");
         sourceExists = true;
      }
      this.allTestResults.add(trSourceFile);
      return sourceExists;
   }

   /**
      Method to compile a java file for you.
      NOTE: <b> This is not a test </b>
      This just compiles a file. It is useful
      for when you need a test file but dont want
      to compile it until you know with certainty
      that the base file compiles.
      @param fileName The filename of the file to compile
      @return the int result of the java compiler 0 for success, nonzero otherwise 
    */
   public int compiler(String fileName) {
      JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
      return compiler.run(null, null, null, fileName);
   }


   /** Function to test if a class compiles.
       Outputs whether the file compiles as well as 
       adds an additional gradescope test for it.
       @param programName the name of the java file to test (without the .java) 
       @return whether the class compiled
    */
   public boolean testCompiles(String programName) {
      boolean passed = false;
      //File source = new File(programName + ".class");
      TestResult trCompilation = new TestResult(programName + " Compiles",
                                                 "Pre-Test", this.maxScore,
                                                this.visibility);
      String fileName = programName + ".java";
      JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      ByteArrayOutputStream err = new ByteArrayOutputStream();
      int compilationResult = compiler.run(null, out, err, fileName);
      if (compilationResult != 0) {
         trCompilation.setScore(0);
         trCompilation.addOutput("ERROR: " + programName + 
                                  ".java did not compile!\n");
         String output = new String(out.toByteArray());
         String error = new String(err.toByteArray());
         trCompilation.addOutput("Output: " + output + "\n"
                                 + "Error: " + error);
      
      } 
      else {
         trCompilation.setScore(this.maxScore);
         trCompilation.addOutput("SUCCESS: " + programName + 
                                  ".java compiled successfully!\n");
         passed = true;
      }
      this.allTestResults.add(trCompilation);
      return passed;
   }
   
   
   /**
    * Checks if checkstyle passed.
    * Creates a gradescope test with the
    * results from checkstyle and gives the 
    * output to the grader. Relies on having 
    * the checkstyle files (the jar and xml)
    * in the location of CHECKSTYLE_JAR and 
    * CHECKSTYLE_XML. Please change those to 
    * match the location for your class.
    * This also assumes that the java files 
    * is in the source folder of the 
    * autograder when uploaded to gradescope.
    * @param programName the java class name (without the .java)
    */
   public void testCheckstyle(String programName) {
      TestResult trCheck = new TestResult(programName + "Checkstyle Compliant",
                                          "Pre-Test",
                                           this.maxScore, this.visibility);
      
      
      String result;
      try {
         String proc = "java -jar " + CHECKSTYLE_JAR +
            " -c " + CHECKSTYLE_XML + " /autograder/source/" +
            programName + ".java";
         Process check = Runtime.getRuntime().exec(proc);
         check.waitFor();  
         Scanner s = new Scanner(check.getInputStream()).useDelimiter("\\A");
         result = s.hasNext() ? s.next() : "";
         //no problems reported in checkstylefile; it passed checkstyle
         if (result.equals("Starting audit...\nAudit done.\n")) {
            trCheck.setScore(this.maxScore);
            trCheck.addOutput("SUCCESS: " + programName +
                               " passed checkstyle with no warnings\n");
         }
         else {  //something in checkstylefile; it failed checkstyle
            trCheck.setScore(0);
            trCheck.addOutput("ERROR: " + programName +
                               " did not pass checkstyle." + 
                              " Results are below:\n" + result);
         }
         
      }  catch (IOException e) {
         return;
      } catch (InterruptedException e) {
         return;
      }
      this.allTestResults.add(trCheck);
   }

   

   /**
      Runs a all the diff tests for a specific file.
      This runs count diff tests each one using the naming 
      convetion on the next line for the name of the input file.
      All input files are named: {Program_Name}_Diff_#.in
      The diff test have the option of not using a sample program
      and instead using already made output files. This approach
      is good if there is a sample run created.
      The expected output should be named:
      {program_Name}_expected_#.out
      @param name the name of the program to do diff tests on
      @param count the number of diffs to perform
      @param sampleFile true if using a sample program false if just comparing to a file. 
   */
   public void diffTests(String name, int count, boolean sampleFile) {
      this.diffTests(name, count, sampleFile, count);
   }

   /**
      Runs a all the diff tests for a specific file.
      This runs count diff tests each one using the naming 
      convetion on the next line for the name of the input file.
      All input files are named: {Program_Name}_Diff_#.in
      The diff test have the option of not using a sample program
      and instead using already made output files. This approach
      is good if there is a sample run created.
      The expected output should be named:
      {program_Name}_expected_#.out
      This version allows for a certain amount to be forced to be
      hidden. 
      @param name the name of the program to do diff tests on
      @param count the number of diffs to perform
      @param sampleFile true if using a sample program false if just comparing to a file.
      @param numVisible The number of tests that should not be automatically hidden (count - numVisible) = numHidden
   */
   public void diffTests(String name, int count, boolean sampleFile, int numVisible) {
      PrintStream originalOut = System.out;
      InputStream originalIn = System.in;
      if (sampleFile) {
         this.compiler(name+"Sample.java");
      }
      for (int i = 0; i < count; i++) {
         String visible = this.visibility;
         if (i >= numVisible) {
            visible = "hidden";
         }
         TestResult trDiff = new TestResult(name + " Diff Test #" + i,
                                            "" + this.diffNum,
                                            this.maxScore, visible);
         this.diffNum++;
         String input = name + "_Diff_" + i + ".in";
         String exOut = name + "_expected_" + i + ".out";
         String acOut = name + "_" + i + ".out";
         String result;
         try {
            File exfile = new File(exOut);
            File infile = new File(input);
            File acfile = new File(acOut);
            File sample = new File(name + "Sample.java");
            if (sampleFile && sample.exists() && !sample.isDirectory()) {
               String[] procSample = {"java", name + "Sample"};
               ProcessBuilder pbSample = new ProcessBuilder(procSample);
               pbSample.redirectOutput(Redirect.to(exfile));
               pbSample.redirectInput(Redirect.from(infile));
               Process sampleProcess = pbSample.start();
               sampleProcess.waitFor();
            }
            PrintStream out = new PrintStream(new FileOutputStream(acOut));
            System.setOut(out);
            System.setIn(new FileInputStream(input));
            Class<?> act = Class.forName(name);
            if (act == null) {
               throw new ClassNotFoundException();
            }
            Method main = act.getMethod("main", String[].class);
            if (main == null) {
               throw new NoSuchMethodException();
            }
            String[] strings = new String[0];
            main.invoke(null, ((Object)strings));
            out.flush();
            out.close();
            System.setOut(originalOut);
            String[] procDiff = {"diff", exOut, acOut, "-y", 
                                 "--width=175", "-t" };
            ProcessBuilder pbDiff = new ProcessBuilder(procDiff);
            Process diffProcess = pbDiff.start();
            diffProcess.waitFor();
            Scanner s = new Scanner(diffProcess.getInputStream())
               .useDelimiter("\\A");
            result = s.hasNext() ? s.next() : "";
            
            if (diffProcess.exitValue() == 0) {
               trDiff.setScore(this.maxScore);
               trDiff.addOutput("SUCCESS: " + name +
                                  " passed this diff test");
            }
            else { 
               trDiff.setScore(0);
               trDiff.addOutput("ERROR: " + name +
                                  " differed from expected output." +
                                " Results are below:\n" + result);
            } 
            
         }  catch (IOException e) {
            trDiff.setScore(0);
            trDiff.addOutput("ERROR: " + name + 
                             " could not be found to run Diff Test");
         } catch (InterruptedException e) {
            trDiff.setScore(0);
            trDiff.addOutput("ERROR: " +  name +
                             " got interrupted");
         } catch (ClassNotFoundException e) {
            trDiff.setScore(0);
            trDiff.addOutput("ERROR: " + name + 
                             " could not be found to run Diff Test");
         } catch (NoSuchMethodException e) {
            trDiff.setScore(0);
            trDiff.addOutput("ERROR: Students " +
                             name + " Main method not found");
         } catch (IllegalAccessException e) {
            trDiff.setScore(0);
            trDiff.addOutput("ERROR: Students code not accessible");
         } catch (InvocationTargetException e) {
            Throwable et = e.getCause();
            Exception es;
            if(et instanceof Exception) {
               es = (Exception) et;
            } else {
               es = e;
            }
            String sStackTrace = stackTraceToString(es);
            trDiff.setScore(0);
            trDiff.addOutput("ERROR: Students code threw " + 
                             es + "\n Stack Trace: " +
                             sStackTrace);
         }
         this.allTestResults.add(trDiff);
         System.setOut(originalOut);
         System.setIn(originalIn);
      }
   }

   /**
      Method to do a test comparing the ouptut of the students method against an expected value.
      Basically this method runs the students code and calls 
      .equals() method of the return to compare it to the expected value.
      It also will work if both methods are supposed to return null.
      If the method fails or gives the wrong output, the test result
      will include the information about the method call and potentially 
      the stack trace if the method crashed.
      @param programName the name of the java class to test
      @param m The method to test use {@link #getMethod(String, String, String[]) getMethod} to get the method
      @param ret The expect output from the method
      @param caller An object the method should be called on (null if a static method)
      @param args Any arguments that need to be passed into the method, can be an array of objects
    */
   public void compTest(String programName, Method m, Object ret, Object caller, Object... args) {
      TestResult trComp = new TestResult(programName + " Unit Test # " + this.diffNum,
                                         "" + this.diffNum,
                                         this.maxScore, this.visibility);
      this.diffNum++;
      if (m != null) {
         try {
            Object t = m.invoke(caller, args);
            if ((t != null && t.equals(ret)) || (t == ret)) {
               trComp.setScore(this.maxScore);
               trComp.addOutput("SUCCESS: Method - "
                                + m.getName() + 
                                " \n" + "Returned the correct output of: " + 
                                ret + "\n" + "On Inputs: \n");
            
            } else {
               trComp.setScore(0);
               trComp.addOutput("FALIURE: Method - "
                                + m.getName() + 
                                "\n" + "Returned the incorrect output of: " + 
                                t + " \n" + "Instead of: " + ret + 
                                "\n" + "On Inputs: \n");
            }
            for (Object arg: args) {
               trComp.addOutput("" + arg + "\n");
            }
         } catch (IllegalAccessException e) {
            trComp.setScore(0);
            trComp.addOutput("ERROR: Method - " + 
                             m.getName() + "Is not Accessible");
         } catch (InvocationTargetException e) {
            Throwable et = e.getCause();
            Exception es;
            if(et instanceof Exception) {
               es = (Exception) et;
            } else {
               es = e;
            }
            String sStackTrace = stackTraceToString(e);
            trComp.setScore(0);
            trComp.addOutput("ERROR: Method = " +
                             m.getName() +
                             " threw " + 
                             es + "On Inputs: \n");
            for (Object arg: args) {
               trComp.addOutput("" + arg + "\n");
            }
            trComp.addOutput("\n Stack Trace: " +
                             sStackTrace);
         }
      } else {
         trComp.setScore(0);
         trComp.addOutput("ERROR: Method - " + 
                             m.getName() + "Does not exist");
      }
      this.allTestResults.add(trComp);
   }

   public static String stackTraceToString(Exception es) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            es.printStackTrace(pw);
            return sw.toString(); // stack trace as a string
   }


   /**
      Method to do a test comparing the ouptut of the students method against an expected value.
      Will wrap the int in its object type (Integer) and then call the object version
      @see #compTest(String, Method, Object, Object, Object...)
      @param programName the name of the java class to test
      @param m The method to test use {@link #getMethod(String, String, String[]) getMethod} to get the method
      @param ret The expect output from the method
      @param caller An object the method should be called on (null if a static method)
      @param args Any arguments that need to be passed into the method, can be an array of objects
    */
   public void compTest(String programName,  Method m, int ret, Object caller, Object... args) {
      Integer i = ret;
      this.compTest(programName, m, i, caller, args);
   }

   /**
      Method to do a test comparing the ouptut of the students method against an expected value.
      Will wrap the boolean in its object type (Boolean) and then call the object version
      @see #compTest(String, Method, Object, Object, Object...)
      @param programName the name of the java class to test
      @param m The method to test use {@link #getMethod(String, String, String[]) getMethod} to get the method
      @param ret The expect output from the method
      @param caller An object the method should be called on (null if a static method)
      @param args Any arguments that need to be passed into the method, can be an array of objects
    */
   public void compTest(String programName, Method m, boolean ret, Object caller, Object... args) {
      Boolean i = ret;
      this.compTest(programName, m, i, caller, args);
   }

   /**
      Method to do a test comparing the ouptut of the students method against an expected value.
      Will wrap the char in its object type (Character) and then call the object version
      @see #compTest(String, Method, Object, Object, Object...)
      @param programName the name of the java class to test
      @param m The method to test use {@link #getMethod(String, String, String[]) getMethod} to get the method
      @param ret The expect output from the method
      @param caller An object the method should be called on (null if a static method)
      @param args Any arguments that need to be passed into the method, can be an array of objects
    */
   public void compTest(String programName, Method m, char ret, Object caller, Object... args) {
      Character i = ret;
      this.compTest(programName, m, i, caller, args);
   }

   /**
      Method to do a test comparing the ouptut of the students method against an expected value.
      Will wrap the double in its object type (Double) and then call the object version
      @see #compTest(String, Method, Object, Object, Object...)
      @param programName the name of the java class to test
      @param m The method to test use {@link #getMethod(String, String, String[]) getMethod} to get the method
      @param ret The expect output from the method
      @param caller An object the method should be called on (null if a static method)
      @param args Any arguments that need to be passed into the method, can be an array of objects
    */
   public void compTest(String programName, Method m, double ret, Object caller, Object... args) {
      Double i = ret;
      this.compTest(programName, m, i, caller, args);
   }

   /**
      Method to do a test comparing the ouptut of the students method against an expected value.
      Will wrap the long in its object type (Long) and then call the object version
      @see #compTest(String, Method, Object, Object, Object...)
      @param programName the name of the java class to test
      @param m The method to test use {@link #getMethod(String, String, String[]) getMethod} to get the method
      @param ret The expect output from the method
      @param caller An object the method should be called on (null if a static method)
      @param args Any arguments that need to be passed into the method, can be an array of objects
    */
   public void compTest(String programName, Method m, long ret, Object caller, Object... args) {
      Long i = ret;
      this.compTest(programName, m, i, caller, args);
   }

   /**
      Method to do a test comparing the ouptut of the students method against an expected value.
      Will wrap the float in its object type (Float) and then call the object version
      @see #compTest(String, Method, Object, Object, Object...)
      @param programName the name of the java class to test
      @param m The method to test use {@link #getMethod(String, String, String[]) getMethod} to get the method
      @param ret The expect output from the method
      @param caller An object the method should be called on (null if a static method)
      @param args Any arguments that need to be passed into the method, can be an array of objects
    */
   public void compTest(String programName, Method m, float ret, Object caller, Object... args) {
      Float i = ret;
      this.compTest(programName, m, i, caller, args);
   }

   /** Method to get a method.
       @see #compTest(String, Method, Object, Object, Object...) compTest
       This method takes in the name of the method and class
       and gets the method to use with the compTest.
       @param programName the name of the java class
       @param methodName the name of the method
       @param paramTypes the argument classes that the method takes in
       @return an object representing the method, null if the method does not exist 
    */
   public static Method getMethod(String programName,
                                  String methodName, 
                                  Class<?>... paramTypes) {
      try {
         Class<?> c = Class.forName(programName);
         if (c == null) {
            throw new ClassNotFoundException();
         }
         Method m = c.getMethod(methodName, paramTypes);
         return m;
      } catch(Exception e) {
         return null;
      }
   
   }

   /** Method to get a method.
       @see #compTest(String, Method, Object, Object, Object...) compTest
       This method takes in the name of the method and class
       and gets the method to use with the compTest.
       @param programName the name of the java class
       @param methodName the name of the method
       @param argTypes the argument classes (in string form) that the method takes in
       @return an object representing the method, null if the method does not exist 
    */
   public static Method getMethod(String programName,
                                  String methodName, 
                                  String[] argTypes) {
      Class<?> args[] = getClasses(argTypes);
      if (args != null) {
         return getMethod(programName, methodName, args);
      }
      return null;
   
   }

   /** Method to test whether a method exists.
       This method takes in the name of the method and class
       and checks if a method exists. It then adds a TestResult
       of whether the method exists.
       @param programName the name of the java class
       @param methodName the name of the method
       @param argTypes the argument classes (in string form) that the method takes in
    */
   public void  hasMethodTest(String programName,
                              String methodName, 
                              String[] argTypes) {
      TestResult trHas = new TestResult("Method Exists Test " + methodName,
                                         "" + this.diffNum,
                                         this.maxScore, this.visibility);
      Class<?> args[] = getClasses(argTypes);
      if (args != null) {
         try {
            Class<?> c = Class.forName(programName);
            if (c == null) {
               trHas.setScore(0);
               trHas.addOutput("ERROR: Class - " + programName 
                               + " does not exist");
            } else {
               Method m = c.getMethod(methodName, args);
               if (m == null) {
                  throw new NoSuchMethodException();
               }
               trHas.setScore(this.maxScore);
               trHas.addOutput("SUCCESS: Class - " + programName
                               + "\nHas a method named: "+ methodName
                               + "\nWith input parameters:\n");
               for (String arg : argTypes) {
                  trHas.addOutput(arg +"\n");
               }
            }
         } catch(Exception e) {
            trHas.setScore(0);
            trHas.addOutput("ERROR: Class - " + programName
                            + "\nDoes not have a method named: "+ methodName
                            + "\nWith input parameters:\n");
            for (String arg : argTypes) {
               trHas.addOutput(arg +"\n");
            }
         }
      } else {
         trHas.setScore(0);
         trHas.addOutput("ERROR: Unable to convert input parameters");
      }
      this.allTestResults.add(trHas);
   }

   /**
      Method to convert the string paramaters to Class{@literal <?>}[].
      Some rules for the string format. Basically take the name 
      that would be in front of an object of that type and that 
      is a string. Order matters as it has to be the same order
      that you want it to be in for the method.
      @param args the list of parameter types
      @return a list of parameter types as a list of classes
    */
   public static Class<?>[] getClasses(String[] args) {
      int argsCount = args.length;
      Class<?>[] ins = new Class<?>[argsCount];
      try {
         for (int j = 0; j < argsCount; j++) {
            String inputop = args[j];
            Class<?> c;
            switch (inputop) {
               case "int":
                  c = int.class;
                  break;
               case "boolean":
                  c = boolean.class;
                  break;
               case "char":
                  c = char.class;
                  break;
               case "float":
                  c = float.class;
                  break;
               default:
                  c  = Class.forName(inputop);
            }
            
            ins[j] = c;
         }
      } catch (ClassNotFoundException e) {
         return null;
      }
      return ins;
   }

/**
      Runs all the comparison tests for a specific file.
      A comparison test is similar to a {@link #compTest(String, Method, Object, Object, Object...) compTest}
      but it is comparing the output of the students program 
      to the output of the same method for a sample program.
      This method will compile the sample program for you.
      The sample programs named: {Program_Name}Sample.java
      All input files are named: {Program_Name}_Comp_#.in
      @param programName the program to do comparison tests on
      @param testCount the number of tests to perform
      @param caller the object the method should be called on (if an instance method)
   */
   public void comparisonTests(String programName, int testCount, Object caller) {
      PrintStream original = System.out;
      System.setOut(new PrintStream(
         new OutputStream() {
            public void write(int b) {
            
            }
         }));
      this.compiler(programName+"Sample.java");
      for (int i = 0; i < testCount; i++) {
         String input = programName + "_Comp_" + i + ".in";
         String result;
         Scanner s;
         try {
            s = new Scanner(new FileReader(input));
         } catch (FileNotFoundException e) {
            return;
         }
         String method = s.next();
         int argsCount = s.nextInt();
         String[] argStrings = new String[argsCount];
         for (int j = 0; j < argStrings.length; j++) {
            argStrings[j] = s.next();
         }
         Class<?>[] ins = this.getClasses(argStrings);
         s.nextLine();
         Object[] args = new Object[argsCount];
         for (int j = 0; j < args.length; j++) {
            Object c;
            String val = s.nextLine();
            if (!ins[j].equals(String.class)) {
               if (ins[j].equals(int.class)) {
                  c = Integer.parseInt(val);
               } else if (ins[j].equals(boolean.class)) {
                  c = Boolean.parseBoolean(val);
               } else if (ins[j].equals(char.class)) {
                  c = val.charAt(0);
               } else if (ins[j].equals(float.class)) {
                  c = Float.parseFloat(val); 
               } else {
                  c  = ins[j].cast(val);
               }
            } else {
               c = val;
            }
            args[j] = c;
         }
         
         Method m = Autograder.getMethod(programName, method, ins);
         Method ms = Autograder.getMethod(programName + "Sample", method, ins);
         Object out = null;
         try {
            out = ms.invoke(caller, args);
         } catch (Exception e) {
            //Do nothing
         }
         this.compTest(programName, m, out, caller, args);
      }
      System.setOut(original);
   }



   /**
      This is a method to run a set of junit tests on a class.
      This method will run the junits and give points for
      every junit passed and give the faliure if the 
      method failed.
      The test file is named: {Program_Name}Test.java
      @param programName the name of the class to test
    */
   public void junitTests(String programName) {
      PrintStream original = System.out;
      System.setOut(new PrintStream(
         new OutputStream() {
            public void write(int b) {
            
            }
         }));
      String fileName = programName + "Test.java";
      int compilationResult = this.compiler(fileName);
      if (compilationResult == 0) {
         try {
            Class<?> clss = Class.forName(programName +"Tests");
            JUnitCore junit = new JUnitCore();
            Listener listen = new Listener(this.maxScore, this.diffNum, programName, this.visibility);
            junit.addListener(listen);
            junit.run(clss);
            this.allTestResults.addAll(listen.allResults());
            this.diffNum = listen.unitNum();
         } catch (Exception e){
            //System.out.println(e);
         }
      } else {
         System.err.println(compilationResult);
      }
      System.setOut(original);
   }

 

   /**
      Runs a test to make sure that the student submitted enough methods.
      This has two particular uses. First in Gateway we sometimes ask
      for them to use a minimum number of helper methods in their code
      in order to get them to break their code up into chunks. In addition
      this has the ability to put in an access modifier to be able to check
      that the method is public or private ensuring that a student created
      all the public methods asked and nothing more.
      In java, access modifiers are represented as ints. The Modifier
      class has all of the possibiliites stored as constants.
      @see java.lang.reflect.Modifier java.lang.reflect.Modifier
      <b>This method only looks at declaredMethods not constructors and inherited methods.</b>
      @param programName the name of the java class
      @param quantity the number of methods the class needs.
      @param modifiers the Modifier that we are only looking for
      @param modify whether to use the access modifier or to look for all methods
      @return whether the class has enough methods
    */
   public boolean testMethodCount(String programName, Integer quantity, int modifiers, boolean modify) {
      boolean passed = false;
      TestResult trMethodCount = new TestResult(programName + " Method Count", 
                                                "" + this.diffNum , this.maxScore, this.visibility);
      this.diffNum++;
      try {
         Class<?> act = Class.forName(programName);
         if (act == null) {
            trMethodCount.setScore(0);
            trMethodCount.addOutput("Faliure: Class " + 
                                    programName + " could not be found!");
         } else {
            int count = 0;
            Method[] methods = act.getDeclaredMethods();
            for (int i = 0; i < methods.length; i++) {
               if (!modify || methods[i].getModifiers() == modifiers) {
                  count++;
               }
            }
            try {
               Method m = act.getMethod("main", String[].class);
               if (m != null) {
                  count--;
               }
            } catch (NoSuchMethodException e) {
               //do nothing
            } catch (SecurityException e) {
               //do nothing
            }
            
            if (count < quantity) {
               trMethodCount.setScore(0);
               trMethodCount.addOutput("Faliure: Class " 
                                       + programName +
                                       " is missing expected methods!");
            } else if (count > quantity) {
               trMethodCount.setScore(0);
               trMethodCount.addOutput("Faliure: Class " 
                                       + programName +
                                       " has unexpected methods!");
            } else {
               trMethodCount.setScore(this.maxScore);
               trMethodCount.addOutput("SUCCESS: Class " + programName +
                                       " has the correct number of methods!");
               passed = true;
            }
         }
      } catch (ClassNotFoundException e) {
         trMethodCount.setScore(0);
         trMethodCount.addOutput("Faliure: Class " 
                                 + programName + 
                                 " could not be found!");
      }
      this.allTestResults.add(trMethodCount);
      return passed;
   }


   /**
      Runs a test to make sure that the student submitted enough constructors.
      This would allow a professor to ensure that a student created multiple 
      constructors if the project description requested it.
      @param programName the name of the java class
      @param quantity the number of methods the class needs.
      @return whether the class has enough constructors
    */
   public boolean testConstructorCount(String programName, Integer quantity) {
      boolean passed = false;
      TestResult trMethodCount = new TestResult(programName + " Method Count", 
                                                "" + this.diffNum , this.maxScore, this.visibility);
      this.diffNum++;
      try {
         Class<?> act = Class.forName(programName);
         if (act == null) {
            trMethodCount.setScore(0);
            trMethodCount.addOutput("Faliure: Class " + 
                                    programName + " could not be found!");
         } else {
            int count = 0;
            Constructor<?>[] methods = act.getConstructors();
            count = methods.length;
            
            if (count < quantity) {
               trMethodCount.setScore(0);
               trMethodCount.addOutput("Faliure: Class " 
                                       + programName +
                                       " is missing expected constructors!");
            } else if (count > quantity) {
               trMethodCount.setScore(0);
               trMethodCount.addOutput("Faliure: Class " 
                                       + programName +
                                       " has unexpected constructors!");
            } else {
               trMethodCount.setScore(this.maxScore);
               trMethodCount.addOutput("SUCCESS: Class " + programName +
                                       " has the correct number of constructors!");
               passed = true;
            }
         }
      } catch (ClassNotFoundException e) {
         trMethodCount.setScore(0);
         trMethodCount.addOutput("Faliure: Class " 
                                 + programName + 
                                 " could not be found!");
      }
      this.allTestResults.add(trMethodCount);
      return passed;
   }
   /**
      Runs a test to make sure that the student code has no public instace variables.
      This method counts the number of fields in the class that are public 
      and gives credit if the user has no public fields and takes off if they do.
      @param programName the name of the java class
      @return whether the class has no public fields
    */
   public boolean testPublicInstanceVariables(String programName) {
      boolean passed = false;
      TestResult trMethodCount = new TestResult(programName + " Check for Public Instance Variables", 
                                                "" + this.diffNum , this.maxScore, this.visibility);
      this.diffNum++;
      try {
         Class<?> act = Class.forName(programName);
         if (act == null) {
            trMethodCount.setScore(0);
            trMethodCount.addOutput("Faliure: Class " + 
                                    programName + " could not be found!");
         } else {
            int count = 0;
            Field[] fields = act.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
               if (Modifier.isPublic(fields[i].getModifiers())) {
                  count++;
               }
            }
            
            if (count > 0) {
               trMethodCount.setScore(0);
               trMethodCount.addOutput("Faliure: Class " 
                                       + programName +
                                       " has public Fields!");
            } else {
               trMethodCount.setScore(this.maxScore);
               trMethodCount.addOutput("SUCCESS: Class " + programName +
                                       " has no public fields!");
               passed = true;
            }
         }
      } catch (ClassNotFoundException e) {
         trMethodCount.setScore(0);
         trMethodCount.addOutput("Faliure: Class " 
                                 + programName + 
                                 " could not be found!");
      }
      this.allTestResults.add(trMethodCount);
      return passed;
   }

   /**
      Runs a test to make sure that the student code does not use ArrayLists.
      This method goes line by line and checks if any line includes an ArrayList.
      @param name the name of the java class
      @return whether the class declares an ArrayList
    */
   public boolean classDoesNotUseArrayList(String name) {
      boolean passed = true;
      TestResult trArrayList = new TestResult(name + " Check for ArrayList Use", 
                                                "" + this.diffNum , this.maxScore, this.visibility);
      this.diffNum++;
      String input = name + ".java";
      String result;
      Scanner s;
      try {
         s = new Scanner(new FileReader(input));
      } catch (FileNotFoundException e) {
         trArrayList.setScore(0);
         trArrayList.addOutput("Test failed to open file "+input);
         this.allTestResults.add(trArrayList);
         return false;
      }
      int line = 0;
      while(s.hasNextLine()) {
         line++;
         String out = s.nextLine();
         if (out.contains("ArrayList")) {
            int curPoints = 0;
            for (TestResult t : this.allTestResults) {
               curPoints += t.getScore();
            }
            trArrayList.setScore(-curPoints);
            trArrayList.addOutput("Submissions in Gateway Computing should not use ArrayLists in their code \n");
            trArrayList.addOutput("An ArrayList Instance was found on line "+line);
            this.allTestResults.add(trArrayList);
            return false;
         }
      }
      trArrayList.addOutput("This submission properly does not use ArrayLists in their code");
      this.allTestResults.add(trArrayList);
      return false;
   }

   /**
      Runs a test to make sure that the student code does not use package declarations.
      This method goes line by line and checks if any line includes a package declaration.
      @param name the name of the java class
      @return whether the class declares a package
    */
   public boolean classDoesNotUsePackages(String name) {
      boolean passed = true;
      TestResult trArrayList = new TestResult(name + " Check for Package Use", 
                                                "" + this.diffNum , this.maxScore, this.visibility);
      this.diffNum++;
      String input = this.classNameToFileName(name, true);
      String result;
      Scanner s;
      try {
         s = new Scanner(new FileReader(input));
      } catch (FileNotFoundException e) {
         trArrayList.setScore(0);
         trArrayList.addOutput("Test failed to open file "+input);
         this.allTestResults.add(trArrayList);
         return false;
      }
      int lineNum = 0;
      while(s.hasNextLine()) {
         lineNum++;
         String out = s.nextLine();
         Scanner line = new Scanner(out);
         if (line.hasNext() && line.next().equals("package")) {
            int curPoints = 0;
            for (TestResult t : this.allTestResults) {
               curPoints += t.getScore();
            }
            trArrayList.setScore(-curPoints);
            trArrayList.addOutput("Submissions in Gateway Computing should not use package declarations in their code \n");
            trArrayList.addOutput("A Package declaration was found on line "+lineNum);
            this.allTestResults.add(trArrayList);
            return false;
         }
      }
      trArrayList.addOutput("This submission properly does not use ArrayLists in their code");
      this.allTestResults.add(trArrayList);
      return false;
   }

   /**
      ##TODO: Look at security
    */

   /** Method to add a seperately made test to the results.
       This allows for people to make child classes of the autograder
       if they need tests that dont currently exist that they would 
       prefer to avoid adding to this class.
       @param name the name of the test
       @param success whether the student passed the test
       @param extraOutput any additional information to display
    */
   public void addTestResult(String name, boolean success, String extraOutput) {
      TestResult tr = new TestResult(name, ""+this.diffNum, this.maxScore, this.visibility);
      tr.setScore((success) ? this.maxScore : 0);
      tr.addOutput(extraOutput);
      this.allTestResults.add(tr);
   }


   private String classNameToFileName(String name, boolean java) {
      if (java) {
         return name.contains(".java") ? name : name + ".java";
      } else {
         return name.contains(".class") ? name : name + ".class";
      }
   }

   /**
      Setter of the visibility of the tests.
      This method updates the current visibility for 
      any future tests. That visibility is set until updated 
      upon a future call to this method.
      This method takes in an int from 0-3 where they represent the following:
      <ul>
          <li>0 = visible</li>
          <li>1 = hidden</li>
          <li>2 = after_due_date</li>
          <li>3 = after_published</li>
      </ul>
      @param choice the int representing the desired visibility
    */
   public void setVisibility(int choice) {
      String[] vis = {"visible", "hidden", "after_due_date", "after_published"};
      if (choice > 3 || choice < 0) {
         choice = 1;
      }
      this.visibility = vis[choice];
   }


   /**
      A method to get the current visibility.
      This method returns the visibility as a string.
      @return the current visibility
    */
   public String getVisibility() {
      return this.visibility;
   }

   /**
      A method to set the point value of the tests.
      This method updates the points awarded for passing
      a test and the amount the test is worth. This
      autograder only works in pass fail as it breaks 
      each test to be the smallest possible component.
      The results will stay at this score until changed
      by calling this method again. A score must be positive.
      If the input is not positive then the score will
      automatically be set to 0.1
      @param score the new score value of a test
    */
   public void setScore(double score) {
      if (score < 0) {
         score = 0.1;
      }
      this.maxScore = score;
   }

   /**
      A method to get the current single test score.
      This returns the number of points that a test
      is worth.
      @return the point value of passing a test
    */
   public double currentScore() {
      return this.maxScore;
   }

}