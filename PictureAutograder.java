
import java.io.File;
import java.awt.Color;
import jh61b.grader.TestResult;

public class PictureAutograder extends Autograder {

   public PictureAutograder() {
      super();
   }

      /**
      Runs a picture diff tests for a specific file.
      All input files are named: {Program_Name}_Diff_#.in
      @param p the program to do diff tests on
      @param prefix the initial part of the name
      @param i the map number
      @param j the image number for the map
      @return whether the pictures match
   */
   public int pictureDiffTest(String p, 
                              String prefix, 
                              int i, 
                              int j) {
      
      String sampleName = prefix + "sample_" + i;
      String resultName = prefix + i;
      if (j >= 0) {
         sampleName += "_" + j + ".png";
         resultName += "_" + j + ".png";
      } else {
         sampleName += ".png";
         resultName += ".png";
      }
      TestResult trDiff = new TestResult(p + 
                                         " Picture Diff Test for "
                                         + resultName + 
                                         " with map # " + i,
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
                             comp[1] + " With a difference of: "
                             + comp[2]);
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
         trDiff.addOutput("The User Run has extra picutres.");
      } else if (faliure == 0) {
         trDiff.setScore(super.maxScore);
         trDiff.addOutput(sampleName + " & " + resultName + " Match.");
      }
      if (fSample.exists() || fUser.exists()) {
         super.addTestResult(trDiff);
         return faliure;
      }
      return -1;
   }
}