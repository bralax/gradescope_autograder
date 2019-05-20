
public class Point {

	private double x;
	private double y;
	
	public Point(double x, double y) {
		super();
		this.x = x;
		this.y = y;
	}
   
	public double getX() {
		return this.x;
	}
   
	public void setX(double x) {
		this.x = x;
	}
   
	public double getY() {
		return this.y;
	}
   
	public void setY(double y) {
		this.y = y;
	}

   public boolean equals(Object o) {
      if (o instanceof Point) {
         Point p = (Point) o;
         return (this.x == p.x) && (this.y == p.y);
      }
      return false;
   }
   
   public String toString() {
      return "(" + this.x + "," + this.y + ")";
   }
}