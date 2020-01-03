import java.io.FileReader;
import java.util.Scanner;
import java.io.IOException;
import java.io.FileWriter;
import java.io.PrintWriter;

public class ModifySubmission {

   public static void main(String[] args) {
      try {
         FileReader fr = new FileReader("Deck.java");
         Scanner scnr = new Scanner(fr);
         FileWriter fOut = new FileWriter("DeckMod.java");
         PrintWriter pr = new PrintWriter(fOut);
         int count = 0;
         pr.println("import brandon.Math;");
         while(scnr.hasNextLine()) {
            String line = scnr.nextLine();
            line = line.replace("Deck", "DeckMod");
            String checkLine = line.replaceAll("\\s+"," ").toLowerCase();
            if (checkLine.contains("private") && checkLine.contains("randomint")) {
               System.out.println("Found the method");
               pr.println("private int randomInt(int low, int high) {\n return low + (int) (Math.random() * (high - low));"
                          +"\n}");
               count = count + 1;
            }
            if (count == 0) {
               pr.println(line);
            } else if (checkLine.contains("}")) {
               count = 0;
            }
         }
         fOut.close();
         fr.close();
      } catch (Exception e) {
         System.out.println("Failed to modify Files");
      }
   }
}
