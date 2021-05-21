package org.planit.utils.misc;

import java.io.File;
import java.io.FilenameFilter;

import org.planit.utils.exceptions.PlanItException;

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
  
  /** collect all files from a directory with the given extension
   * @param pathToDir path to dir
   * @param fileExtension the file extension, e.g. ".xml"
   * @return the list of files that match this extension in the dir
   * @throws PlanItException thrown if error
   */
  public static File[] getFilesWithExtensionFromDir(final String pathToDir, final String fileExtension) throws PlanItException {
    PlanItException.throwIfNull(pathToDir,String.format("path directory is null when collecting files"));
    PlanItException.throwIfNull(fileExtension,String.format("file extension to use is null when collecting files from directory"));
    
    /* the dir */
    File directoryPath = new File(pathToDir);    
    PlanItException.throwIf(!directoryPath.isDirectory(),String.format("%s is not a valid directory",directoryPath ));    
    
    /* the filter */
    FilenameFilter fileExtensionFilter = new FilenameFilter(){
       public boolean accept(File dir, String name) {
          String lowercaseName = name.toLowerCase();
          if (lowercaseName.endsWith(fileExtension)) {
             return true;
          } else {
             return false;
          }
       }
    };

    /* collect matches */
    return directoryPath.listFiles(fileExtensionFilter);
  }
  
  
  /** extract the file name without the extension, i.e. remove the last part of the string after the last "." encountered
   * @param fileName to use
   * @return fileName without extension
   */
  public static String getFileNameWithoutExtension(final String fileName) {
    if(fileName!= null && fileName.contains(".")) {
      return fileName.substring(0, fileName.lastIndexOf('.'));      
    }
    return fileName;
  }
}
