import java.lang.reflect.Method;


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
      String program = "Statistics";
      Autograder.compiler("Point.java");
      Autograder.compiler("DataSet.java");
      Autograder.compiler("StatisticsSample.java");
      gr.setScore(1.0);
      gr.setVisibility(0); //Visible
      boolean exists = gr.testSourceExists(program);
      gr.setVisibility(1); //Hidden
      if ( exists && gr.testCompiles(program)) {
         gr.testCheckstyle(program);
         DataSet ds = new DataSet(10);
         fillDataSet(ds);
         Point p = StatisticsSample.computeMean(ds);
         String[] arg =  {"DataSet"};
         gr.hasMethodTest(program, "computeMean" , arg);
         Method m = gr.getMethod(program, "computeMean", arg);
         gr.compTest(program, m, p, null, ds);
      } else {
         exists = false;
      }
      gr.testRunFinished();
   }

   private static void fillDataSet(DataSet d) {
      for (int i = 0; i < d.capacity(); i++) {
         d.add(new Point(i, i));
      }
   }
}