package org.planit.utils.misc;

/**
 * Some simple string utilities
 * 
 * @author markr
 *
 */
public class StringUtils {

  
  /** split string by anything but alpha numeric characters, i.e., a-zA-Z0-9
   *  
   * @param toSplit string to split
   * @return split string
   */
  public static String[] splitByAnythingExceptAlphaNumeric(String toSplit) {
    return toSplit.split("[^a-zA-Z0-9]");
  }

  /** Verify if null or blank
   * @param string to verify
   * @return true when null or blank
   */
  public static boolean isNullOrBlank(String string) {
    return string==null || string.isBlank();
  }

  /** Remove the given string from the beginning of the string if present and return result
   * 
   * @param theString to alter
   * @param initialString to remove if present
   * @return altered string
   */
  public static String removeInitialStringWhenPresent(String theString, String initialString) {
    return theString.startsWith(initialString) ? theString.substring(initialString.length()) : theString;
  }
  
  /** Remove the given string from the end of the string if present and return result
   * 
   * @param theString to alter
   * @param initialString to remove if present
   * @return altered string
   */
  public static String removeEndingStringWhenPresent(String theString, String endString) {
    return theString.endsWith(endString) ? theString.substring(0, theString.length() - endString.length()) : theString;
  }  
}
