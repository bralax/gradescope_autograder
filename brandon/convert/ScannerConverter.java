package brandon.convert;
import java.util.Scanner;

public class ScannerConverter extends ClassConverter{

   public ScannerConverter() {
      super.baseClass = Scanner.class;
      super.baseClassString = "Scanner";
   }
   
   @Override
   public Object convert(String arg) {
      return new Scanner(System.in);
   }

   @Override
   public String toString(Object input) {
      return "Scanner";
   }
}
