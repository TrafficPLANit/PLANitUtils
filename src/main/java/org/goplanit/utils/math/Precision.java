package org.goplanit.utils.math;

import java.text.DecimalFormat;
import java.util.Comparator;

/** Compare doubles with a certain precision
 * @author markr
 *
 */
public class Precision {

  /** no tolerance, i.e., 0.0 */
  public static final double EPSILON_0 = 0.0;
  
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
  public static boolean equal(double d1, double d2, double epsilon) {
    return !smaller(d1, d2, epsilon) && !greater(d1, d2, epsilon); 
  }
     
  /** Compare using a Precision.EPSILON_6
   * @param d1 double1
   * @param d2 double2
   * @return true when equal within epsilon, false otherwise
   */
  public static boolean equal(double d1, double d2) {
    return equal(d1,d2,EPSILON_6);
  }
  
  /** isSmallerEqual with epsilon
   * @param d1 double1
   * @param d2 double2
   * @param epsilon epsilon value
   * @return true when {@code d1 <= (d2 + epsilon)}
   */
  public static boolean smallerEqual(double d1, double d2, double epsilon) {
    return d1 <= (d2 + epsilon); 
  }
  
  /** isSmaller with Precision.EPSILON_6
   * @param d1 double1
   * @param d2 double2
   * @return true when  {@code (d1 + epsilon) < d2}
   */
  public static boolean smaller(double d1, double d2) {
    return smaller(d1,d2,EPSILON_6);
  }  
  
  /** isSmaller with epsilon
   * @param d1 double1
   * @param d2 double2
   * @param epsilon epsilon value
   * @return true when  {@code (d1 +epsilon) < d2 }
   */
  public static boolean smaller(double d1, double d2, double epsilon) {
    return (d1 + epsilon) < d2; 
  }
  
  /** isSmallerEqual with Precision.EPSILON_6
   * @param d1 double1
   * @param d2 double2
   * @return true when  {@code d1 <= (d2 + epsilon)}
   */
  public static boolean smallerEqual(double d1, double d2) {
    return smallerEqual(d1,d2,EPSILON_6);
  }    
  
  /** isGreaterEqual with epsilon
   * @param d1 double1
   * @param d2 double2
   * @param epsilon epsilon value
   * @return true when  {@code d1 >= (d2 - epsilon)}
   */
  public static boolean greaterEqual(double d1, double d2, double epsilon) {
    return d1 >= (d2- epsilon);
  }
  
  /** isGreaterEqual with Precision.EPSILON_6
   * @param d1 double1
   * @param d2 double2
   * @return true when  {@code d1 >= (d2- epsilon)}
   */
  public static boolean greaterEqual(double d1, double d2) {
    return greaterEqual(d1,d2,EPSILON_6);
  }   
  
  /** isGreaterEqual with epsilon
   * @param d1 double1
   * @param d2 double2
   * @param epsilon epsilon value
   * @return true when  {@code (d1 - epsilon) > d2}
   */
  public static boolean greater(double d1, double d2, double epsilon) {
    return (d1 - epsilon) > d2;
  }
  
  /** isGreater with Precision.EPSILON_6
   * @param d1 double1
   * @param d2 double2
   * @return true when  {@code (d1 - epsilon) > d2}
   */
  public static boolean greater(double d1, double d2) {
    return greater(d1,d2,EPSILON_6);
  }

  /** Verify if positive with Precision.EPSILON_6
   * @param d1 double 1
   * @return true when {@code d1 > EPSILON_6} 
   */
  public static boolean positive(double d1) {
    return greater(d1,0,EPSILON_6);
  }
  
  /** Verify if positive with Precision.EPSILON_6
   * @param d1 double 1
   * @param epsilon to consider
   * @return true when {@code d1 > epsilon} 
   */
  public static boolean positive(double d1, double epsilon) {
    return greater(d1,0,epsilon);
  }  
  
  /** Verify if negative with Precision.EPSILON_6
   * @param d1 double 1
   * @return true when {@code d1 < -EPSILON_6} 
   */
  public static boolean negative(double d1) {
    return smaller(d1,0,EPSILON_6);
  }    
  
  /** Verify if non-zero with Precision.EPSILON_6
   * 
   * @param d1 double 1
   * @return true when {@code d1 < -EPSILON_6 or d1 > EPSILON_6} 
   */
  public static boolean nonZero(double d1) {
    return positive(d1) || negative(d1);
  }

  /** Opposite of {@link #equal(double, double)}
   * 
   * @param d1 to use
   * @param d2 to use
   * @return true when not equal, false otherwise
   */
  public static boolean notEqual(double d1, double d2) {
    return !equal(d1, d2);
  }
  
  /** Opposite of {@link #equal(double, double, double)}
   * 
   * @param d1 to use
   * @param d2 to use
   * @param epsilon to use
   * @return true when not equal, false otherwise
   */
  public static boolean notEqual(double d1, double d2, double epsilon) {
    return !equal(d1, d2, epsilon);
  }

  /**
   * A double based compare with delta comparator where the (closed range) precision determines
   * whether something is considered equal
   *
   * @param d1 first
   * @param d2 second
   * @param precision to apply
   * @return 0 when equal, -1 when d1 is smaller than s2, +1 when d2 is larger than d1
   */
  public static int compareWithEpsilon(Double d1, Double d2, double precision) {
    if(Precision.equal(d1,d2, precision)){
      return 0;
    }else if(Precision.smaller(d1,d2)){
      return -1;
    }else{
      return 1;
    }
  }

  /**
   * Create a double based comparator with a configurable precision
   *
   * @param epsilon to apply
   */
  public static Comparator<Double> createComparator(double epsilon){
    return (o1, o2) -> compareWithEpsilon(o1, o2, epsilon);
  }

  /**
   * Create a double based comparator that takes objects and casts them to doubles before comparing
   * with a configurable precision
   *
   * @param epsilon to apply
   */
  public static Comparator<Object> createComparatorWithCast(double epsilon){
    return (o1, o2) -> compareWithEpsilon((Double) o1, (Double) o2, epsilon);
  }
}
