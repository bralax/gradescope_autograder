/** Class to represent a collection of 2D point values: (x,y) pairs.
*/
public class DataSet {

   /** A number used to generate sequential IDs. */
   private static int nextID = 0;

   /** The array holding the points. */
   private Point[] data;
   
   /** The actual number of points stored. */
   private int size;
   
   /** The id of this DataSet. */
   private int id;
   
   /** The mean of this DataSet. */
   private Point mean;

   /** The variance of this DataSet. */
   private double variance;
      
   /** Create a dataset with the given capacity.
     * @param capacity the maximum number of points.
     */
   public DataSet(int capacity) {
      this.data = new Point[capacity];
      this.size = 0;
      this.id = nextID++;
   }
   
   /** Get the capacity of the dataset.
     * @return the capacity
     */
   public int capacity() {
      return this.data.length;
   }
   
   /** Get the size of the dataset.
     * @return how many Points are in it
     */
   public int size() {
      return this.size;
   }
   
   /** Get the id of the dataset.
     * @return the id number
     */
   public int getID() {
      return this.id;
   }
   
   /** Add a point to the dataset, if there is room.
     * @param p the point to add
     * @return true if successful, false otherwise
     */
   public boolean add(Point p) {
      if (this.size < this.data.length) {
         this.data[this.size++] = p;
         return true;
      }
      return false;
   }
   
   /** Add an (x, y) point to the dataset, if there is room.
     * @param x the x coordinate of the point
     * @param y the y coordinate of the point
     * @return true if successful, false otherwise
     */
   public boolean add(double x, double y) {
      // HINT: call the above method
      return this.add(new Point(x, y));
   }
   
   /** Get a particular point from the dataset.
     * @param i the point number to retrieve
     * @return the point at that position
     */
   public Point getPoint(int i) {
      return this.data[i];
   }
   
   /** Create a string representation of the dataset,
     * formatted as ID#: [ (x1,y1) (x2,y2) ... (xn,yn) ]
     * @return the string
     */
   public String toString() {
      String result = "ID" + id + ": [ ";
      for (int i = 0; i < this.size; i++) {
         result += this.data[i] + " ";
      }
      result += "]";
      return result;
   }
   
   /** Get the mean of the dataset.
     * @return the point object representing the mean
     */
   public Point getMean() {
      return this.mean;
   }
   
   /** Set the mean of the dataset.
     * @param m : the point object representing the mean
     */
   public void setMean(Point m) {
      this.mean = m;
   }
   
   /** Get the variance of the dataset.
     * @return a double representing the variance
     */
   public double getVariance() {
      return this.variance;
   }
   
   /** Set the variance of the dataset.
     * @param v : the double representing the variance
     */
   public void setVariance(double v) {
      this.variance = v;
   }
   
   /** The main method. 
    * @param args : not applicable for muggles
    */
   public static void main(String[] args) {
      DataSet data = new DataSet(10);
      Point p1 = new Point(10, 20);
      Point p2 = new Point(20, 10);
      data.add(p1);
      data.add(p2);
      for (int i = 0; i < 10; i++) {
         if (! data.add(i, i * 10)) {
            System.out.println("add failed");
         }
      }
      System.out.println(data);
   }
}