import java.io.File;
import java.awt.Color;
import jh61b.grader.TestResult;


/**
   Example child class of the Autograder.
   Has an additional test for comparing the 
   output of pictures. 
   @see Autograder
   @author Brandon Lax
 */
public class PictureAutograder extends Autograder {

   /**
      The constructor of a picture Autograder.
    */
   public PictureAutograder() {
      super();
   }

      /**
      Runs a picture diff tests for a specific file.
      This currently is designed to work with the 
      pictures created for project 3 in Gateway
      Computing Spring 2019. Each picture 
      was named prefix_i_j.png.
      @param p the program to do diff tests on
      @param prefix the initial part of the name
      @param i the map number
      @param j the image number for the map
      @return whether the pictures match
   */
   public void pictureDiffTest(String p, 
                              String prefix, 
                              double cold,
                              double hot) {
      
      String sampleName = prefix + "sample" + "_"+cold+"_"+hot+".txt.x10.jpg";
      String resultName = prefix + "_"+cold+"_"+hot+".txt.x10.jpg";
      TestResult trDiff = new TestResult(p + 
                                         " Picture Diff Test for map "+prefix
                                         + " with cold temp: " + cold +
                                         " and Hot Temp " + hot,
                                            "" +  super.diffNum,
                                            super.maxScore, super.visibility);
      super.diffNum++;
      int faliure = 0;
   
   
      File fSample = new File(sampleName);
      File fUser = new File(resultName);
      if (fSample.exists() && fUser.exists()) {
         Picture sample = new Picture(sampleName);
         Picture result = new Picture(resultName);
         int[] comp = sample.compare(result, 5);
         if (comp[0] != -1) {
            trDiff.setScore(0);
            trDiff.addOutput("Falied on index: " 
                             + comp[0] + ", " + 
                             comp[1] + " Due to a difference with ");
            switch(comp[2]) {
              case 0:
                 trDiff.addOutput("The Red value of the pixel");
                 break;
              case 1:
                 trDiff.addOutput("The Green value of the pixel");
                 break;
              case 2:
                 trDiff.addOutput("The Blue value of the pixel");
                 break;
              case 3:
                 trDiff.addOutput("The Red & Green values of the pixel");
                 break;
              case 4:
                 trDiff.addOutput("The Blue & Green values of the pixel");
                 break;
              case 5:
                 trDiff.addOutput("The Red & Blue values of the pixel");
                 break;
              case 6:
                 trDiff.addOutput("The Red, Green & Blue values of the pixel");
                 break;
            }
            Color sampleColor = sample.get(comp[0], comp[1]);
            Color resultColor = result.get(comp[0], comp[1]);
            trDiff.addOutput("\n Sample Pixel Value: R = " 
                             + sampleColor.getRed() +
                             ", G = " + sampleColor.getGreen() 
                             + ", B =  " + sampleColor.getBlue());
            trDiff.addOutput("\n Result Pixel Value: R = " 
                             + resultColor.getRed() + ", G = " 
                             + resultColor.getGreen() + ", B =  " 
                             + resultColor.getBlue());
            faliure = 1;
         }
      }
         
      if (fSample.exists() && !fUser.exists()) {
         trDiff.setScore(0);
         trDiff.addOutput(resultName + " is missing.");
      } else if (!fSample.exists() && fUser.exists()) {
         trDiff.setScore(0);
         trDiff.addOutput("The sample code broke");
      } else if (faliure == 0) {
         trDiff.setScore(super.maxScore);
         trDiff.addOutput(sampleName + " & " + resultName + " Match.");
      }
      super.addTestResult(trDiff);

   }
}
