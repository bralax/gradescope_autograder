
public class AutograderMain {
   
   private static final double SCORE = 0.1;
   /**
      Main method of the autograder.
      Runs all of the tests
      @param args the files and diff test counts
      @throws Exception When something goes wrong with a test
   */
   public static void main(String[] args) throws Exception {
      Autograder gr = new Autograder();
      if (args.length < 3 || (args.length % 3) != 0) {
         System.out.println("Missing Command Line Arguments");
         return;
      }
      int progCount = args.length / 3;
      Program[] programs = new Program[progCount];
      gr.setScore(1.0);
      for (int i = 0; i < programs.length; i++) {
         programs[i] = new Program(args[3 * i], args[3 * i + 1], args[3 * i + 2], "01");
         gr.setVisibility(0); //Visible
         boolean exists = !gr.testSourceExists(programs[i].name());
         gr.setVisibility(1); //Hidden
         if ( exists ||
             !gr.testCompiles(programs[i].name())) {
            programs[i].setExists(false);
         } else {
            gr.testCheckstyle(programs[i].name());
         }
      }
      gr.setScore(0.1);
      for (int i = 0; i < programs.length; i++) {
         if (programs[i].exists()) {
            gr.stdOutDiffTests(programs[i].name(), programs[i].testCount(), true);
            gr.comparisonTests(programs[i].name(), programs[i].unitCount(), null);
         }
      }
      gr.testRunFinished();
   }
}
