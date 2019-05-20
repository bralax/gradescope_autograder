import java.util.Scanner;

/** Solution to the encryption project.
 */
public class Proj2BSample {

   /** How many letters in the alphabet. */
   private static final int ALPHA = 26;

   /** Main method, execution begins here.
    *  @param args not used
    */
   public static void main(String[] args) {
   
      Scanner kb = new Scanner(System.in);
      char option;
      String message = "This is the original message.";
      String newmsg;  // new message, before validation
      int shift;  // for shift encryption
      int jump, interval;  // for jump encryption
      
      assert validateMessage(message) == true :
            "validateMessage(message) with default message failed";
       
       assert shiftEncrypt (message, -6).equals ("Nbcm cm nby ilcachuf gymmuay.") :
            "Shift encrypt failed!";
       
       assert mirrorEncrypt (message).equals ("Gsrh rh gsv lirtrmzo nvhhztv.") :
            "Mirror encrypt failed!";
       
       assert jumpEncrypt (message,5).equals ("Tieg ahs imgi oneestras. hils"):
            "Jump encrypt failed!";
   
      do {
           // print menu
         System.out.println("Encryption menu options:");
         System.out.println("d - display current message");
         System.out.println("r - read new message");
         System.out.println("s - shift encrypt");
         System.out.println("m - mirror encrypt");
         System.out.println("j - jump encrypt");
         System.out.println("q - quit program");
         System.out.print("enter option letter -> ");
         option = kb.nextLine().toLowerCase().charAt(0);
      
           // error check and process
         switch (option) {
            case 'q':
               System.out.println("Goodbye!");
               break;
            case 'd':
               System.out.println("Current message: " + message);
               break;
            case 'r':
               System.out.print("Enter new message: ");
               newmsg = kb.nextLine();
               if (validateMessage(newmsg)) {
                  message = newmsg;
               } else {
                  System.out.println("invalid message, keeping current");
               }
               break;
            case 's':
               System.out.print("Enter shift value -25 to 25: ");
               shift = kb.nextInt();
               kb.nextLine();
               if (shift < -(ALPHA - 1) || shift > ALPHA - 1) {
                  System.out.println("invalid shift value");
               } else {
                  System.out.println("Current message:  " + message);
                  System.out.println("Shift encryption: "
                                  + shiftEncrypt(message, shift));
               }
               break;
            case 'm':
               System.out.println("Current message:   " + message);
               System.out.println("Mirror encryption: "
                                  + mirrorEncrypt(message));
               break;
            case 'j':
               interval = (int) Math.sqrt(message.length());
               System.out.print("Enter jump interval between 2-"
                                + interval + "-> ");
               jump = kb.nextInt();
               kb.nextLine();
               // validate jump
               if (jump < 2 || jump > interval) {
                  System.out.println("invalid jump value");
               } else {
                  System.out.println("Current message: " + message);
                  System.out.println("Jump encryption: "
                                     + jumpEncrypt(message, jump));
               }
               break;
            default:
               System.out.println("invalid option, try again");
         }
         System.out.println();
      } while (option != 'q');
   }

   /** Validate the format of a messge - must start with a
       capital letter and end with '.', '?' or '!'.
    *  @param message the string to validate
    *  @return true if valid, false otherwise
    */
   public static boolean validateMessage(String message) {
   
      if (message.length() < 2) {
         return false;
      }
      char first = message.charAt(0);
      char last = message.charAt(message.length() - 1);
      if (first < 'A' || first > 'Z') {
         return false;
      }
      if (last != '.' && last != '?' && last != '!') {
         return false;
      }
      return true; 
   }

   /** Do shift encryption on the message, affecting only
       the alphabet letters. Preserve capitalization and all
       other characters.
    *  @param message the string to encrypt
    *  @param shiftValue to amount to shift - a positive value
       means shift right and a negative value means shift left:
       'A' + 6 == 'G', 'a' + -6 == 'u'
    *  @return a new string that is the encrypted version of message.
    */
   public static String shiftEncrypt(String message, int shiftValue) {
      String sencrypt = "";
      char letter;
   
      for (int i = 0; i < message.length(); i++) {
         letter = message.charAt(i);
         if (letter >= 'A' && letter <= 'Z') {
            letter = (char) (letter + shiftValue);
            if (letter < 'A') {
               letter += ALPHA;
            } else if (letter > 'Z') {
               letter -= ALPHA;
            }
         } else if (letter >= 'a' && letter <= 'z') {
            letter = (char) (letter + shiftValue);
            if (letter < 'a') {
               letter += ALPHA;
            } else if (letter > 'z') {
               letter -= ALPHA;
            }
         }
         sencrypt += letter;
      }
      return sencrypt; 
   }

   /** Do mirror encryption on the message, affecting only
       the alphabet letters. Preserve capitalization and all
       other characters.
    *  @param message the string to encrypt
    *  @return a new string that is the encrypted version of message.
    */
   public static String mirrorEncrypt(String message) {
      String mencrypt = "";
      String upper   = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
      String umirror = "ZYXWVUTSRQPONMLKJIHGFEDCBA";
      String lower = upper.toLowerCase();
      String lmirror = umirror.toLowerCase();
      int pos;
      char letter;
   
      for (int i = 0; i < message.length(); i++) {
         letter = message.charAt(i);
         pos = upper.indexOf(letter);
         if (pos >= 0) {
            mencrypt += umirror.charAt(pos);
         } else {
            pos = lower.indexOf(letter);
            if (pos >= 0) {
               mencrypt += lmirror.charAt(pos);
            } else {
               mencrypt += letter;
            }
         }
      }
      return mencrypt;
   }

   /** Do jump encryption on the message, affecting ALL characters
       in the given message.
    *  @param message the string to encrypt
    *  @param jumpValue the interval size to use when jumping; value
       should be at most the square root of the message length
    *  @return a new string that is the encrypted version of message.
    */
   public static String jumpEncrypt(String message, int jumpValue) {
      String jencrypt = "";
      int idx = 0;
      int cols = (int) Math.ceil(message.length() / (double) jumpValue);
   
      for (int row = 0; row < jumpValue; row++) {
         for (int col = 0; col < cols; col++) {
            idx = jumpValue * col + row;
            if (idx < message.length()) {
               jencrypt += message.charAt(idx);
                   //     System.out.print(message.charAt(idx));
            }
         }
       //   System.out.println();
      }
      return jencrypt;
   }
}
