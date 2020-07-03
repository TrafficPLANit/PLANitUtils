package org.planit.math;

/** compare doubles with a certain precision
 * @author markr
 *
 */
public class Precision {
  
  public static final double EPSILON_6 = 0.000001;
  
  /** Compare using a custom precision epsilon
   * @param d1 double1
   * @param d2 double2
   * @param epsilon epsilon value
   * @return true when equal within epsilon, false otherwise
   */
  public static boolean isEqual(double d1, double d2, double epsilon) {
    return ( (d1 - epsilon) <= d2 && ((d1 + epsilon) >= d2));
  }
     
  /** Compare using a Precision.EPSILON_6
   * @param d1 double1
   * @param d2 double2
   * @return true when equal within epsilon, false otherwise
   */
  public static boolean isEqual(double d1, double d2) {
    return isEqual(d1,d2,EPSILON_6);
  }
  
  /** isSmallerEqual with epsilon
   * @param d1 double1
   * @param d2 double2
   * @param epsilon epsilon value
   * @return true when d1 <= (d2 + epsilon)
   */
  public static boolean isSmallerEqual(double d1, double d2, double epsilon) {
    return d1 <= (d2 + epsilon); 
  }
  
  /** isSmallerEqual with Precision.EPSILON_6
   * @param d1 double1
   * @param d2 double2
   * @return true when (d1 + epsilon) <= d2
   */
  public static boolean isSmallerEqual(double d1, double d2) {
    return isSmallerEqual(d1,d2,EPSILON_6);
  }  
  
  /** isGreaterEqual with epsilon
   * @param d1 double1
   * @param d2 double2
   * @param epsilon epsilon value
   * @return true when d1 >= (d2 - epsilon)
   */
  public static boolean isGreaterEqual(double d1, double d2, double epsilon) {
    return d1 >= (d2- epsilon);
  }
  
  /** isGreaterEqual with Precision.EPSILON_6
   * @param d1 double1
   * @param d2 double2
   * @return true when (d1 - epsilon) >= d2
   */
  public static boolean isGreaterEqual(double d1, double d2) {
    return isGreaterEqual(d1,d2,EPSILON_6);
  }   

}
