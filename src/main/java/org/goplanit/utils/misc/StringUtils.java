package org.goplanit.utils.misc;

import java.nio.charset.Charset;
import java.util.regex.Pattern;

/**
 * Some simple string utilities
 * 
 * @author markr
 *
 */
public class StringUtils {

  /** precompiled regex pattern to split a string based on encountering a non-alphanumeric character */
  public static final Pattern SPLIT_BY_NON_ALPHA_NUMERIC = Pattern.compile("[^a-zA-Z0-9]");

  /**
   * Create string ot use in String.format of form "0numPadding%d"
   * @param numPadding to apply
   * @return created String
   */
  private static String createPaddingFormatString(int numPadding){
    return "%0" + numPadding + "d";
  }

  /**
   * A String might have some byte order mark preceding the string (for example InputStreamReader might include this). These are not visible in the string
   * but do change the underlying bytes compared to a string without this BOM and comparing them will fail despite looking identical.
   * This method will remove the BOM from a copy of this string which is returned.
   *
   * @param value to remove BOM from
   * @return BOM-less copy
   */
  public static String removeBOM(String value){
    return value.replace("\uFeFF","");
  }
  
  /** split string by anything but alphanumeric characters, i.e., a-zA-Z0-9
   *  
   * @param toSplit string to split
   * @return split string
   */
  public static String[] splitByAnythingExceptAlphaNumeric(String toSplit) {
    return SPLIT_BY_NON_ALPHA_NUMERIC.split(toSplit);
  }

  /** Verify if null or blank
   * @param string to verify
   * @return true when null or blank
   */
  public static boolean isNullOrBlank(String string) {
    return string==null || string.isBlank();
  }

  /**
   *  check if the line contains the string when converted to lower case and removing all spaces
   *
   * @param checkThisIfIt the string to check and see if it contains the containsThis
   * @param containsThis the sub string to check for
   * @return true when it does contain it, false otherwise
   */
  public static boolean containsInLowerCaseNoSpaces(String checkThisIfIt, String containsThis) {
    return checkThisIfIt.replace(" ","").toLowerCase().contains(
            containsThis.replace(" ","").toLowerCase());
  }

  /**
   *  check if the line starts with the string when converted to lower case and removing all spaces
   *
   * @param checkThisIfIt the string to check and see if it starts with the startsWithThis
   * @param startsWithThis the sub string to check for
   * @return true when it does contain it, false otherwise
   */
  public static boolean startsWithInLowerCaseNoSpaces(String checkThisIfIt, String startsWithThis) {
    return checkThisIfIt.replace(" ","").toLowerCase().startsWith(
            startsWithThis.replace(" ","").toLowerCase());
  }

  /**
   *  check if the line ends with the string when converted to lower case and removing all spaces
   *
   * @param checkThisIfIt the string to check and see if it ends with the startsWithThis
   * @param endsWithThis the sub string to check for
   * @return true when it does contain it, false otherwise
   */
  public static boolean endsWithInLowerCaseNoSpaces(String checkThisIfIt, String endsWithThis) {
    return checkThisIfIt.replace(" ","").toLowerCase().endsWith(
            endsWithThis.replace(" ","").toLowerCase());
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
   * @param endString to remove if present
   * @return altered string
   */
  public static String removeEndingStringWhenPresent(String theString, String endString) {
    return theString.endsWith(endString) ? theString.substring(0, theString.length() - endString.length()) : theString;
  }

  /**
   * Value to be converted to string with given zero padding in from
   * @param value to convert
   * @param numPadding to apply
   * @return created string
   */
  public static String zeroPaddedStringOf(int value, int numPadding){
    String paddingFormatString = createPaddingFormatString(numPadding);
    return String.format(paddingFormatString,value);
  }

  /** print bytes of the String
   *
   * @param str to print bytes for
   * @param encoding to apply
   * @return String with printed bytes
   */
  public static String printBytes(String str, final Charset encoding) {
    byte[] bytes = str.getBytes(encoding);
    StringBuilder output = new StringBuilder(str + "= byte[");
    for (int i = 0; i < bytes.length; i++) {
      output.append(bytes[i]);
      if (i < bytes.length - 1) {
        output.append(", ");
      }
    }
    output.append("]");
    return output.toString();
  }

  /** print chars of the String
   *
   * @param str to print chars for
   * @return String with printed bytes
   */
  public static String printChars(String str) {
    char[] chars = str.toCharArray();
    StringBuilder output = new StringBuilder(str + "= char[");
    for (int i = 0; i < chars.length; i++) {
      output.append(chars[i]);
      if (i < chars.length - 1) {
        output.append(", ");
      }
    }
    output.append("]");
    return output.toString();
  }
}
