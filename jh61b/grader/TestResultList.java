package jh61b.grader;

import java.util.List;
import java.util.ArrayList;

public class TestResultList {
   private final long checksum;
   private List<TestResult> allTestResults;

   public TestResultList(long check) {
      this.allTestResults = new ArrayList<TestResult>();
      this.checksum = check;
   }

   public boolean add(TestResult t, long check) {
      if (check == this.checksum) {
         return this.allTestResults.add(t);
      }
      return false;
   }

   public boolean addAll(List<TestResult> t, long check) {
      if (check == this.checksum) {
         return this.allTestResults.addAll(t);
      }
      return false;
   }

   public TestResult[] toArray(long check) {
      if (check == this.checksum) {
         return this.allTestResults.toArray(new TestResult[this.allTestResults.size()]);
      }
      return null;
   }

}


