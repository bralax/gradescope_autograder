# A Java Gradescope Autograder

This repo contains the autograder used in EN.500.112 Gateway Computing: JAVA at Johns Hopkins University.

The design of the autograder allows for a large number of options. The Gradescope autograder is set up into three parts.

## setup.sh
This is what is run in creating the the VM that the autograder runs in. The only thing that we currently have this file do
is install java on the VM. 

## run_autograder
This file does all the prep work for an individual submission. This is what gets run whenever a student submits their code.
The current version does a couple of things:
1. Deletes any leftover files (this is more for when running locally as every time a student submits it creates a new VM)
2. Compiles the autograder
3. runs AutograderMain.java

## Autograder/AutograderMain
This file contains the main method that runs the autograder. Autograder.java is designed to make this as customizable as possible.

Currently Autograder.java offers the following tests for use in the AutograderMain.java main method. For more detail about how to use these methods
see https://bralax.github.io/gradescope_autograder/index.html.

Base

* A Java source file exists
* A java source file compiles
* A java source file is checkstyle compliant (Two forms)
* A java source produces a proper standard out (diff test)
* A java source produces an expected output from a method (comparison method)
* A java source has a specific method
* A java source's methods match the expected output of a sample version 
* A java source passes a junit test
* A java source has an expected number of methods
* A java source has an expected number of methods
* A java source has no public instance methods
* A java source does not use ArrayLists
* A java source does not have package declarations
* A java source produces a correct file
* A java source has an expected number of public methods
* A java source has a specific field
   
Picture Autograder
* Two created pictures match within a specified margin

Drawing Autograder
* A java file matches a list of expected shapes
* A java file creates the same shapes as a sample program
* A java file creates an expected shape

 
If you need another test, reach out and it could be added in the future.

## Autograder Structure

* Autograder
   * results
      * results.json - the place where all the test results need to end up
   * source - All the files uploaded as the autograder
   * submission - All the files the user uploads
   * run_autograder - The running script is seperated from the autograder and run from the base directory.

## Checkstyle Testing
   There are two forms of the checkstyle testing. The first is to use a basic checkstyle setup. It says whether the test passes or fails based on whether there are any failures and prints the same output that you would expect from a tradition run of checkstyle.

   To use this format, you have to set the values of **CHECKSTYLE_JAR** and **CHECKSTYLE_XML** in Autograder.java to match the locations of the jar and xml that you are using.

   The other option is a little more complex. It runs the same test but takes of a specified amount of points either per type of error or per instance of an error. It then modifies the output to list each type of error and every line on which that error occurs.

   To use this format, you have to do two steps. First you must modify your checkstyle xml file to include the following lines:
   ```xml
   <module name="Checker">

     <!-- The next two lines have to be added for the java program to be able to Listen to the test being run. -->
     <module name="GatewayCheckstyleListener">
     </module>

     <!-- list all the checks you want here -->
   </module>
   ```
   Second you have to set **CHECKSTYLE_LISTEN_XML** at the top of Autograder.java to the location of the modified xml file.

## Comparison Tests
   The comparison tests are designed to be a simple interface for running tests that compare a students result of a method against either an expected value or a sample implementation.

   These tests rely on a specially formatted file which is:
   ```
   {Method Name}
   {Method Parameter Count}
   {Method Parameter Type 1}
   {Method Parameter Type 2}
   .
   .
   .
   {Method Parameter Type Count}
   {Method Parameter 1}
   {Method Parameter 2}
   .
   .
   .
   {Method Parameter Count}
   ```
   Each item should be in it's own row. The system can currently handle all of the primitive types (char, int, double, boolean, float, long) and String as well as their array types. If you need your method to handle parameters of types other than the ones provided, you will need to implement your own type of Class Converter for the object.

   To do this, you need to create a java class type that extends the abstract class ClassConverter found in brandon/convert. See brandon/convert for example implemetations. This file should contain 3 functions:
   1. A Constructor - This should set the baseClass and baseClassString for this class. They need to be set for correct converter to be used when running a comparison test.
   2. ```public Object convert(String)``` - Each class has to be represented in a single line of text. This method is designed to take that line of text and use it to make the desired object. All arrays so far have been implemented to break instances on a space (ie the string would be {item1,item2}).
   3. ```public String toString(Object)``` - This is the reverse of the convert method. This is used to convert an unidentified object back to it's string representation. This is used for outputting information in the final test output. You might be able to get away with using the .toString method of the class but this is not always the case which is why this method exists. That is especially true with array types where you don't get a simple string representation like that.

## Random Numbers
   The Autograder has built-in ways to handle random number generation to guarentee that all random number generators created in student submissions and sample implementations are seeded without asking the students to do it themselves. To use the techniques, you will have to add the following imports to the top of the students and sample java files (and remove of any import of java.util.Random):
   ```java
   import brandon.math.Random;
   import brandon.math.Math;
   ```
   These classes contain all the same code as the built-in Math and Random libraries but sead all random number generators. The random number generator system does not handle:
   1. Students that create a new Random number generator everytime that they run the random number generator section of their code
   2. Students that make more random number than expected (like calling ```rand.nextInt(10) + rand.nextInt(10)``` instead of ```rand.nextInt(20)```)

   See the ModifySubmission.java file in the examples folder for a sample of how to add the imports after a student uploads their submission. I would reccomend modifying the code after running a checkstyle test. Checkstyle takes off for unnecessary imports and a student submission most likely wont use both Random() and Math.random(). 
      
## TODO
   * Picture diff tests need give back better output on a runtime exception
   * Alllowing for checkstyle to ignore the first instance of a specified test