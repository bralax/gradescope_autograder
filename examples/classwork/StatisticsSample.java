import java.io.FileReader;
import java.io.IOException; 
import java.util.Scanner;

/** Implements some statistical measures for a 
 *  particular dataset.
 */
public class StatisticsSample {

   /**
    * Computes the mean x and the mean y of the dataset.
    * @param data : the input dataset object
    * @return the Point object representing the mean of the dataset
    */
   public static Point computeMean(DataSet data) {
      int range = data.size();
      double meanX = 0.0;
      double meanY = 0.0;
      
      for (int i = 0; i < range; i++) {
         Point p = data.getPoint(i);
         meanX += p.getX();
         meanY += p.getY();
      }
      
      return new Point(meanX / range, meanY / range);
   }
   
   /**
    * Computes variance in the points within the dataset.
    * @param data : the input dataset object
    * @return a double representing the variance of the dataset
    */
   public static double computeVariance(DataSet data) {
      int range = data.size();
      
      double meanX = data.getMean().getX();
      double meanY = data.getMean().getY();
      
      double varX = 0.0;
      double varY = 0.0;
            
      for (int i = 0; i < range; i++) {
         Point p = data.getPoint(i);
         varX += (p.getX() - meanX) * (p.getX() - meanX);
         varY += (p.getY() - meanY) * (p.getY() - meanY);
      }
      
      varX /= range;
      varY /= range;
            
      return Math.sqrt(varX + varY);
   }
   
   /**
    * Compare the similarity between the two datasets.
    * @param dOne : first of the two dataset objects
    * @param dTwo : second of the two dataset objects
    * @return true if the datasets are similar, false otherwise
    */
   public static boolean compare(DataSet dOne, DataSet dTwo) {
      
      double dx = Math.pow(dOne.getMean().getX() - dTwo.getMean().getX(), 2);
      double dy = Math.pow(dOne.getMean().getY() - dTwo.getMean().getY(), 2);
      
      double disMean = Math.sqrt(dx + dy);
      double disVari = Math.abs(dOne.getVariance() - dTwo.getVariance());
      
      if (disMean < 5 && disVari < 1) {
         return true;
      }
      
      return false;
   }
   
   /** The main method. 
    * @param args : not applicable for muggles
    * @throws IOException if read/write operation fails
    */
   public static void main(String[] args) throws IOException {
      
      DataSet[] datas = new DataSet[2];
      for (int i = 0; i < datas.length; i++) {
         datas[i] = new DataSet(200);
      }
      
      double x, y;
      int category;
      
      // read input from train.txt (plain text file)
      // first value is x coord, second value is y coord, 
      // third is classification == dataset
      Scanner infile = new Scanner(new FileReader("data.txt"));
      while (infile.hasNext()) {
         x = infile.nextDouble();
         y = infile.nextDouble();
         category = infile.nextInt();
         datas[category].add(x, y);
      }
      
      // compute and set the mean and the variance of the datasets
      for (int i = 0; i < datas.length; i++) {
         datas[i].setMean(computeMean(datas[i]));
         datas[i].setVariance(computeVariance(datas[i]));
      }
      
      // System.out.println(datas[0].getMean().getX());
      // System.out.println(datas[0].getMean().getY());
      // System.out.println(datas[0].getVariance());
            
      // Compare all datasets
      System.out.println(compare(datas[0], datas[1]));
          
   }

}