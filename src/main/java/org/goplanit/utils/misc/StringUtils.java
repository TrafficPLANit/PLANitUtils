package org.goplanit.utils.misc;

import org.apache.commons.lang3.CharSet;

import java.nio.charset.Charset;

/**
 * Some simple string utilities
 * 
 * @author markr
 *
 */
public class StringUtils {

  /**
   * Create string ot use in String.format of form "0numPadding%d"
   * @param numPadding to apply
   * @return created String
   */
  private static String createPaddingFormatString(int numPadding){
    var sb = new StringBuilder("%0");
    sb.append(numPadding);
    sb.append("d");
    return sb.toString();
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
    String output = str + "= byte[";
    for (int i = 0; i < bytes.length; i++) {
      output += Byte.toString(bytes[i]);
      if (i < bytes.length - 1) {
        output += ", ";
      }
    }
    output += "]";
    return output;
  }

  /** print chars of the String
   *
   * @param str to print chars for
   * @return String with printed bytes
   */
  public static String printChars(String str) {
    char[] chars = str.toCharArray();
    String output = str + "= char[";
    for (int i = 0; i < chars.length; i++) {
      output += String.valueOf(chars[i]);
      if (i < chars.length - 1) {
        output += ", ";
      }
    }
    output += "]";
    return output;
  }
}
