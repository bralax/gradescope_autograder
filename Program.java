   /**
      Small class representing a Java program.
      This is used in the sample main as a 
      way to abstract out information so 
      that the autograder does not have to be
      updated every time from scratch for a new
      project and can just be updated in the 
      run_autograder file. 
      @author Brandon Lax
      @see AutograderMain
    */
   public class Program {
      /**The name of the program.*/
      private String name;
   
      /**The number of cooresponding diff tests.*/
      private int testCount;
      
      /**The number of cooresponding unit tests.*/
      private int unitCount;
   
      /**Whether the file exists in the submission.*/
      private boolean exists;

      /**Whether the file was written by the student*/
      private boolean userWritten;

      /**Whether there are junits for the file*/
      private boolean junits;
      /**
         Public constructor of the class.
         @param newName the name of the program
         @param diffCount the number of diff tests
         @param unitCount the number of comparison tests
         @param written stores low bit is user written and 2's bit is junits or not
       */
      Program(String newName, String diffCount, String unitCount, String written) {
         this.name = newName;
         this.testCount = Integer.parseInt(diffCount);
         this.unitCount = Integer.parseInt(unitCount);
         this.exists = true;
         this.userWritten = (Integer.parseInt(written) & 1) > 0;
         this.junits = (Integer.parseInt(written) & 2) > 0;
      }
      
      /**
         returns whether the file exists.
         @return exists
       */
      public boolean exists() {
         return this.exists;
      }
   
      /** Set whether a submission file exists.
          @param b whether it exists
       */
      public void setExists(boolean b) {
         this.exists = b;
      }
   
      /**
         Getter for the name.
         @return the name
       */
      public String name() {
         return this.name;
      }
      
      /**
         Getter for the test count.
         @return the test count
       */
      public int testCount() {
         return this.testCount;
      }
      
      /**
         Getter for the unitCount.
         @return the unit Count
       */
      public int unitCount() {
         return this.unitCount;
      }

      /**
         Metho to check who wrote the java file.
         @return true if written by student false if a starter file
       */
      public boolean userWritten() {
         return this.userWritten;
      }

      /**
         Method to check if the file has junits attacked.
         @return true if junits false otherwise
       */
      public boolean hasJunits() {
         return this.junits;
      }
   }