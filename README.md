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


## TODO
   * Make the system for taking in parameters to a comparison test more robust.
      * Allow for custom objects as parameters
      * Use interfaces to allow this to work
   * Picture diff tests need give back better output on a runtime exception
   * Inform students when there is a character encoding issue
   * Add a test for existance of a field
   * Add a test for no public fields
