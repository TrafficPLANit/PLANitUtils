package org.goplanit.utils.math;

import java.text.DecimalFormat;

/** compare doubles with a certain precision
 * @author markr
 *
 */
public class Precision {
  
  public static final double EPSILON_18 = 0.000000000000000001;
  
  public static final double EPSILON_15 = 0.000000000000001;
  
  public static final double EPSILON_12 = 0.000000000001;
  
  public static final double EPSILON_9 = 0.000000001;
  
  /** the default Epsilon unless indicated otherwise */
  public static final double EPSILON_6 = 0.000001;
  
  public static final double EPSILON_3 = 0.001;
  
  /** default decimal format used applies a maximum of 8 decimals and a minimum of 2 */
  public static final DecimalFormat DEFAULT_DECIMAL_FORMAT;
      
  /* initialise decimal format */
  static {
    DEFAULT_DECIMAL_FORMAT = (DecimalFormat) DecimalFormat.getInstance();
    DEFAULT_DECIMAL_FORMAT.setMaximumFractionDigits(8);
    DEFAULT_DECIMAL_FORMAT.setMinimumFractionDigits(2);
    DEFAULT_DECIMAL_FORMAT.setGroupingUsed(false);
  }  
  
  /** Compare using a custom precision epsilon
   * @param d1 double1
   * @param d2 double2
   * @param epsilon epsilon value
   * @return true when equal within epsilon, false otherwise
   */
  public static boolean isEqual(double d1, double d2, double epsilon) {
    return !isSmaller(d1, d2, epsilon) && !isGreater(d1, d2, epsilon); 
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
   * @return true when {@code d1 <= (d2 + epsilon)}
   */
  public static boolean isSmallerEqual(double d1, double d2, double epsilon) {
    return d1 <= (d2 + epsilon); 
  }
  
  /** isSmaller with Precision.EPSILON_6
   * @param d1 double1
   * @param d2 double2
   * @return true when  {@code (d1 + epsilon) < d2}
   */
  public static boolean isSmaller(double d1, double d2) {
    return isSmaller(d1,d2,EPSILON_6);
  }  
  
  /** isSmaller with epsilon
   * @param d1 double1
   * @param d2 double2
   * @param epsilon epsilon value
   * @return true when  {@code (d1 +epsilon) < d2 }
   */
  public static boolean isSmaller(double d1, double d2, double epsilon) {
    return (d1 + epsilon) < d2; 
  }
  
  /** isSmallerEqual with Precision.EPSILON_6
   * @param d1 double1
   * @param d2 double2
   * @return true when  {@code d1 <= (d2 + epsilon)}
   */
  public static boolean isSmallerEqual(double d1, double d2) {
    return isSmallerEqual(d1,d2,EPSILON_6);
  }    
  
  /** isGreaterEqual with epsilon
   * @param d1 double1
   * @param d2 double2
   * @param epsilon epsilon value
   * @return true when  {@code d1 >= (d2 - epsilon)}
   */
  public static boolean isGreaterEqual(double d1, double d2, double epsilon) {
    return d1 >= (d2- epsilon);
  }
  
  /** isGreaterEqual with Precision.EPSILON_6
   * @param d1 double1
   * @param d2 double2
   * @return true when  {@code d1 >= (d2- epsilon)}
   */
  public static boolean isGreaterEqual(double d1, double d2) {
    return isGreaterEqual(d1,d2,EPSILON_6);
  }   
  
  /** isGreaterEqual with epsilon
   * @param d1 double1
   * @param d2 double2
   * @param epsilon epsilon value
   * @return true when  {@code (d1 - epsilon) > d2}
   */
  public static boolean isGreater(double d1, double d2, double epsilon) {
    return (d1 - epsilon) > d2;
  }
  
  /** isGreater with Precision.EPSILON_6
   * @param d1 double1
   * @param d2 double2
   * @return true when  {@code (d1 - epsilon) > d2}
   */
  public static boolean isGreater(double d1, double d2) {
    return isGreater(d1,d2,EPSILON_6);
  }

  /** Verify if positive with Precision.EPSILON_6
   * @param d1 double 1
   * @return true when {@code d1 > EPSILON_6} 
   */
  public static boolean isPositive(double d1) {
    return isGreater(d1,0,EPSILON_6);
  }

  /** Opposite of {@link #isEqual(double, double)}
   * 
   * @param d1 to use
   * @param d2 to use
   * @return true when not equal, false otherwise
   */
  public static boolean isNotEqual(double d1, double d2) {
    return !isEqual(d1, d2);
  }    

}
