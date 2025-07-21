/*
 * A sample file for the checker to process.
 */

package edu.appstate.cs.examples;

public class BadNames {
  public void foo(String s) {
  	System.out.println(s);
  }

  public static void main(String[] args) {
    String m = "This is a message";
    BadNames b = new BadNames();
    b.foo(m);
    String thisNameIsTooLongForGoodPractice = "This name is way too long and should be avoided";
  }

  public void methodWithAVeryLongNameThatExceedsTheRecommendedLength(String p, String q) {

    System.out.println(p + " " + q);
    // This method name is also too long and should be avoided.
  }
}
