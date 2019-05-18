
public class AutograderMain {
   /**
      Main method of the autograder.
      Runs all of the tests
      @param args the files and diff test counts
      @throws Exception When something goes wrong with a test
    */
   public static void main(String[] args) throws Exception {
      
      Autograder gr = new Autograder();
      if (args.length < 4 || (args.length % 4) != 0) {
         System.out.println("Missing Command Line Arguments");
         return;
      }
      int progCount = args.length / 4;
      Program[] programs = new Program[progCount];
      boolean error = false;
      for (int i = 0; i < programs.length; i++) {
         programs[i] = new Program(args[4 * i], args[4 * i + 2], args[4 * i + 3], args[4 * i + 1]);
         if (programs[i].userWritten()) {
            programs[i].setExists(gr.testSourceExists(programs[i].name()));
            if (!programs[i].exists()) {
               error = true;
            }
         }
      }
      if (!error) {
         for (int i = 0; i < programs.length; i++) {
            boolean comps = !gr.testCompiles(programs[i].name(), programs[i].userWritten());
            error = error || comps;
            if (error) {
               programs[i].setExists(comps);
            } else if (programs[i].userWritten()){
               gr.testCheckstyle(programs[i].name());
               gr.testMethodCount(programs[i].name(), programs[i].unitCount());
               gr.testPublicInstanceVariables(programs[i].name());
            }
         }
      }

      for(int i = 0; i < programs.length; i++) {
         if(programs[i].exists() && !error) {
            if (programs[i].hasJunits()) {
               gr.junitTests(programs[i]);
            }
            gr.diffTests(programs[i]);
         }
      }
      gr.testRunFinished();
   }
}