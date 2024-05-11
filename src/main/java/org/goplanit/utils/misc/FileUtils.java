package org.goplanit.utils.misc;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;
import java.util.logging.Logger;

import org.goplanit.utils.exceptions.PlanItException;
import org.goplanit.utils.exceptions.PlanItRunTimeException;
import org.xml.sax.InputSource;

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
   */
  public static File[] getFilesWithExtensionFromDir(final String pathToDir, final String fileExtension){
    PlanItRunTimeException.throwIf(StringUtils.isNullOrBlank(pathToDir),String.format("Path directory is null or blank when collecting files"));
    PlanItRunTimeException.throwIf(StringUtils.isNullOrBlank(fileExtension),String.format("File extension to use is null or blank when collecting files from directory"));
    
    /* the dir */
    File directoryPath = new File(pathToDir);
    PlanItRunTimeException.throwIf(!directoryPath.isDirectory(),String.format("%s is not a valid directory",directoryPath ));
    
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
   * @param directoryToDelete the directory to delete
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

  /**
   * Given a location, construct input source
   *
   * @param filePath to use
   * @param charSetEncoding to apply
   * @return created input source
   * @throws FileNotFoundException thrown if error
   * @throws UnsupportedEncodingException  thrown if error
   */
  public static InputSource getFileContentAsInputSource(String filePath, String charSetEncoding) throws FileNotFoundException, UnsupportedEncodingException {
    File file = new File(filePath);
    InputStream inputStream= new FileInputStream(file);
    Reader reader = new InputStreamReader(inputStream, charSetEncoding);
    InputSource is = new InputSource(reader);
    is.setEncoding(charSetEncoding);
    return is;
  }

  /**
   * Construct a string based on the file contents
   *
   * @param filePath to use
   * @param charSetEncoding to apply
   * @return created string representation
   * @throws IOException thrown if error
   */
  public static String parseFileContentAsString(String filePath, String charSetEncoding) throws IOException {
    // input source
    InputSource is = getFileContentAsInputSource(filePath, charSetEncoding);

    // convert to string
    Reader r= is.getCharacterStream();
    StringBuilder stringBuilder = new StringBuilder();
    int c;
    while((c = r.read()) > -1)
      stringBuilder.appendCodePoint(c);

    return stringBuilder.toString();
  }

  /**
   * Construct a string based on the file contents in UTF8 character encouding
   *
   * @param filePath to use
   * @return created string representation
   * @throws IOException thrown if error
   */
  public static String parseUtf8FileContentAsString(String filePath) throws IOException {
    return parseFileContentAsString(filePath, "UTF8");
  }

  /**
   * Convenience method to create directories from provided string if they do not exist. In case
   * of an IO exception a warning is issued and exception is caught
   *
   * @param outputDirectory to create if needed
   * @return true if directory already existed of exists after creation, false otherwise
   */
  public static boolean createDirectoryFrom(String outputDirectory) {
    try {
      var outputDir = Paths.get(outputDirectory);
      Files.createDirectories(outputDir);
      return Files.exists(outputDir);
    } catch (IOException e) {
      LOGGER.warning("Unable to create directory" + outputDirectory);
    }
    return false;
  }
}
