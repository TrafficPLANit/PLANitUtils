package org.planit.utils.math;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * some utility functions for converting types easily
 * 
 * @author markr
 *
 */
public class TypeConversionUtil {

  
  /** convert a string that can be interpreted as a long 
   * to a type that parses long via {@code .valueOf(long theLong)}
   * 
   * @param <T>
   * @param longValue in string form
   * @return the parsed entry in type T
   */
  public static BigInteger toBigInteger(String longValue) {
    return BigInteger.valueOf(Long.valueOf(longValue));    
  }
  
  /** convert a string that can be interpreted as a long 
   * to a type that parses long via {@code .valueOf(long theLong)}
   * 
   * @param <T>
   * @param longValue in string form
   * @return the parsed entry in type T
   */
  public static BigDecimal longStringToBigDecimal(String longValue) {
    return BigDecimal.valueOf(Long.valueOf(longValue));    
  }  
  
  /** convert a string that can be interpreted as a double 
   * to a type that parses double via {@code .valueOf(double theDouble)}
   * 
   * @param <T>
   * @param doubleValue in string form
   * @return the parsed entry in type T
   */
  public static BigDecimal doubleStringToBigDecimal(String theDouble) {
    return BigDecimal.valueOf(Double.valueOf(theDouble));    
  }   
}
