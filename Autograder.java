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
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
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
   Classs representing an autograder.
   It's main method is the running of the autograder
   and instances can be made to store all important information.
*/
public class Autograder extends RunListener {

   /**The list of all tests performed.*/
   private List<TestResult> allTestResults;
   
   /**The current junit test.*/
   private TestResult currentJunitTestResult;

   /** The value of each test.*/
   private double maxScore;
   
   /**The current test number we are on.*/
   protected int diffNum;


   private String visibility;
   /**
      The main class constructor.
      Initializes the list of all tests.
   */
   public Autograder() {
      this.allTestResults = new ArrayList<TestResult>();
      this.diffNum = 1;
      this.visibility = "hidden";
      this.maxScore = 0.1;
   }

   public void addTestResult(TestResult t) {
      this.allTestResults.add(t);
   }
   
   
   /** Code to run at the end of test run. 
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
    * Check if source file exists.
    * @param programName the program name
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

   public int compiler(String fileName) {
      JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
      return compiler.run(null, null, null, fileName);
   }


   /** Function to test if a class compiles.
       @param programName the name of the java file to tes       @return whether the class compiled
    */
   public boolean testCompiles(String programName, boolean save) {
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
      if (save) {
         this.allTestResults.add(trCompilation);
      }
      return passed;
   }
   
   
   /**
    * Checks if checkstyle passed.
    * @param programName the program name
    */
   public void testCheckstyle(String programName) {
      TestResult trCheck = new TestResult(programName + "Checkstyle Compliant",
                                          "Pre-Test",
                                           this.maxScore, this.visibility);
      String checkstyle = "/autograder/source/checkstyle/";
      
      String result;
      try {
         String proc = "java -jar " + checkstyle + "checkstyle-8.10.1-all.jar" +
            " -c " + checkstyle + "check112.xml /autograder/source/" +
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
      All input files are named: {Program_Name}_Test_#.in
      @param p the program to do diff tests on
      @param sampleFile true if using a sample program false if just comparing to a file.
   */
   public void diffTests(String name, int count, boolean sampleFile) {
      this.diffTests(name, count, sampleFile, count);
   }

   /**
      Runs a all the diff tests for a specific file.
      All input files are named: {Program_Name}_Test_#.in
      @param p the program to do diff tests on
      @param sampleFile true if using a sample program false if just comparing to a file.
   */
   public void diffTests(String name, int count, boolean sampleFile, int numVisible) {
      PrintStream originalOut = System.out;
      InputStream originalIn = System.in;
      if (sampleFile) {
         this.compiler(name+"Sample.java");
      }
      for (int i = 0; i < count; i++) {
         TestResult trDiff = new TestResult(name + " Diff Test #" + i,
                                            "" + this.diffNum,
                                            this.maxScore, this.visibility);
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
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            Throwable et = e.getCause();
            Exception es;
            if(et instanceof Exception) {
               es = (Exception) et;
            } else {
               es = e;
            }
            es.printStackTrace(pw);
            String sStackTrace = sw.toString(); // stack trace as a string
            trDiff.setScore(0);
            trDiff.addOutput("ERROR: Students code threw " + 
                             e + "\n Stack Trace: " +
                             sStackTrace);
         }
         this.allTestResults.add(trDiff);
         System.setOut(originalOut);
         System.setIn(originalIn);
      }
   }

   public void compTest(String programName,  Method m, int ret, Object caller, Object... args) {
      Integer i = ret;
      this.compTest(programName, m, i, caller, args);
   }

   public void compTest(String programName, Method m, boolean ret, Object caller, Object... args) {
      Boolean i = ret;
      this.compTest(programName, m, i, caller, args);
   }

   public void compTest(String programName, Method m, char ret, Object caller, Object... args) {
      Character i = ret;
      this.compTest(programName, m, i, caller, args);
   }

   public void compTest(String programName, Method m, double ret, Object caller, Object... args) {
      Double i = ret;
      this.compTest(programName, m, i, caller, args);
   }

   public void compTest(String programName, Method m, Object ret, Object caller, Object... args) {
      TestResult trComp = new TestResult(programName + " Unit Test # " + this.diffNum,
                                         "" + this.diffNum,
                                         this.maxScore, this.visibility);
      this.diffNum++;
      if (m != null) {
         try {
            Object t = m.invoke(caller, args);
            if (t.equals(ret)) {
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
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            Throwable et = e.getCause();
            Exception es;
            if(et instanceof Exception) {
               es = (Exception) et;
            } else {
               es = e;
            }
            es.printStackTrace(pw);
            String sStackTrace = sw.toString(); // stack trace as a string
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


   public static Method getMethod(String programName,
                                  String methodName, 
                                  String[] argTypes) {
      Class<?> args[] = getClasses(argTypes);
      if (args != null) {
         return getMethod(programName, methodName, args);
      }
      return null;

   }

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
      Runs a all the diff tests for a specific file.
      All input files are named: {Program_Name}_Test_#.in
      @param programName the program to do comparison tests on
      @param testCount the number of tests to perform
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

    */
   public void junitTests(String programName) {
      PrintStream original = System.out;
      System.setOut(new PrintStream(
         new OutputStream() {
            public void write(int b) {
            
            }
         }));
      String fileName = programName + "Tests.java";
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
      @param programName the name of the java class
      @param quantity the number of methods the class needs
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
      Runs a test to make sure that the student submitted enough methods.
      @param programName the name of the java class
      @param quantity the number of methods the class needs
      @return whether the class has enough methods
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
      Runs a test to make sure that the student submitted enough methods.
      @param programName the name of the java class
      @param quantity the number of methods the class needs
      @return whether the class has enough methods
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

   public void addTestResult(String name, boolean success, String extraOutput) {
      TestResult tr = new TestResult(name, ""+this.diffNum, this.maxScore, this.visibility);
      tr.setScore((success) ? this.maxScore : 0);
      tr.addOutput(extraOutput);
      this.allTestResults.add(tr);
   }

   public void setVisibility(int choice) {
      String[] vis = {"visible", "hidden", "after_due_date", "after_published"};
      if (choice > 3 || choice < 0) {
         choice = 1;
      }
      this.visibility = vis[choice];
   }

   public String getVisibility() {
      return this.visibility;
   }

   public void setScore(double score) {
      if (score < 0) {
         score = 0.1;
      }
      this.maxScore = score;
   }

   public double currentScore() {
      return this.maxScore;
   }

}