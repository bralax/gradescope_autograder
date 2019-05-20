This is an example version of the autograder that works with Gateway Computing Project 2. 

The processing is split into two parts:
1. run_autograder
2. AutograderMain.java


# run_autograder
run_autograder copies the users Proj2B.java file from the submission directory to the source directory.
This is done so that the user code is in the same place as all of the running code.
This class does not use packages as it is an intro class so we cant rely on packages to know locations.
It then compiles all of the runner Code (AutograderMain/Autograder)
Then it calls AutograderMain with the following arguments

java_class_name #difftests #comparisontests

It then redirects the output of the method into results/results.json which is what junit uses to check test results.

# AutograderMain.java

1. It takes the information provided and turns it into a program object
2. It tests whether the source file exists
3. It checks whether the code compiles and is checkstyle compliant
4. It finally runs (if everything else passes) all the diff and comparison tests