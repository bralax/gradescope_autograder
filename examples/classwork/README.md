This is an example version of the autograder that works with Gateway Computing classwork activity.

The processing is split into two parts:
1. run_autograder
2. AutograderMain.java


# run_autograder
run_autograder copies the users Proj2B.java file from the submission directory to the source directory.
This is done so that the user code is in the same place as all of the running code.
This class does not use packages as it is an intro class so we cant rely on packages to know locations.
It then compiles all of the runner Code (AutograderMain/Autograder)
Then it calls AutograderMain


It then redirects the output of the method into results/results.json which is what junit uses to check test results.

# AutograderMain.java

1. It compiles Point.java, DataSet.java, and StatisticsSample.java to use later
2. It checks if the source file exists and whether it compiles
3. It then checks if it is checkstyle compliant
4. Finally it checks if a computeMethod exists and whether it produces the right output for one sample DataSet