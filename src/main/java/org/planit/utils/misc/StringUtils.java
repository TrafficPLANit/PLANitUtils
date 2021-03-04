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
}