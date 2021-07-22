package org.planit.utils.misc;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.function.Consumer;
import java.util.logging.Logger;

import org.planit.utils.exceptions.PlanItException;

/**
 * Lightweight File utilities
 * 
 * @author markr
 *
 */
public class FileUtils {
  
  /** logger to use */
  private static final Logger LOGGER = Logger.getLogger(FileUtils.class.getCanonicalName());
  
  /* dor character */
  public static final String DOT = ".";
  
  /* zip extension without dot */
  public static final String ZIP = "zip";
  
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
  
  /** Collect all files from a directory with the given extension
   * 
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
  
  
  /** Extract the file name without the extension, i.e. remove the last part of the string after the last "." encountered
   * 
   * @param fileName to use
   * @return fileName without extension
   */
  public static String getFileNameWithoutExtension(final String fileName) {
    if(fileName!= null && fileName.contains(".")) {
      return fileName.substring(0, fileName.lastIndexOf('.'));      
    }
    return fileName;
  }
  
  /** Call the callback for each file in the directory provided
   * 
   * @param pathToDir to check
   * @param callBack to use no each file found in dir
   */
  public static void callForEachFileIn(final String pathToDir, final Consumer<File> callBack) {
    if(pathToDir==null) {
      return;
    }
    if(callBack == null) {
      LOGGER.warning(String.format("No callback provided for any file in %s",pathToDir));
    }
    File[] filesInDir = new File(pathToDir).listFiles();
    for(int index=0;index<filesInDir.length;++index) {
      callBack.accept(filesInDir[index]);
    } 
  }

  /** Create from URL given it is a local file
   * 
   * @param url to use
   * @return file
   */
  public static File create(URL url) {
    
    if(UrlUtils.isLocal(url)) {
      try {
        return new File(url.toURI());
      }catch(URISyntaxException e) {
        LOGGER.warning(String.format("Unable to convert URL %s to file",url.toString()));
      }
    }
    
    return null;
  }
  
  /** Delete a directory by providing a file that represents a directory. In which case
   * we recursively delete all files in the directory and then the directory itself.
   * 
   * @param directoryToDelete
   * @return success of deletion
   */
  public static boolean deleteDirectory(final File directoryToDelete) {
    if(!directoryToDelete.isDirectory()) {
      return false;          
    }
    
    File[] filesInDir = directoryToDelete.listFiles();
    if(filesInDir != null){
        for (File file : filesInDir) {
          if(file.isDirectory()) {
            deleteDirectory(file);
          }else {
            file.delete();
          }
        }
    }
    return directoryToDelete.delete();
  }    
   
}
