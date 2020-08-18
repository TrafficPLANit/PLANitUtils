package org.planit.utils.misc;

import java.io.File;

/**
 * Lightweight File utilities
 * 
 * @author markr
 *
 */
public class FileUtils {
  
  public static final String DOT = ".";
  
  /** Collect the extension of a file. In case the file is hidden (starts with "." it does not count as extension)
   * 
   * @param file the file to get it from
   * @return the extension, if it does not exist return empty string
   */
  public static String getExtension(File file) {
    String fileName = file.getName();
    if(fileName.lastIndexOf(DOT) != -1 && fileName.lastIndexOf(DOT) != 0) {
      return fileName.substring(fileName.lastIndexOf(DOT)+1);
    }else {
      return "";      
    }
  }
}
