package org.goplanit.utils.zip;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.goplanit.utils.misc.FileUtils;
import org.goplanit.utils.misc.StringUtils;
import org.goplanit.utils.misc.UrlUtils;

/**
 * Utilities for accessing content in zip files
 * 
 * @author markr
 *
 */
public class ZipUtils {
  
  /** logger to use */
  private static final Logger LOGGER = Logger.getLogger(ZipUtils.class.getCanonicalName());
  
  /** Collect the zip input stream for a given zip file (starting point). 
   * Note that invoked needs to close the input stream when done!
   * 
   * @param zipFileLocation to use
   * @return input stream created
   * @throws URISyntaxException thrown if error
   * @throws FileNotFoundException thrown if error
   */
  public static PlanitZipInputStream createZipInputStream(URL zipFileLocation) throws FileNotFoundException, URISyntaxException {
    if(!UrlUtils.isLocalZipFile(zipFileLocation)) {
      return null;
    }

    FileInputStream fis = new FileInputStream(new File(zipFileLocation.toURI()));
    BufferedInputStream bis = new BufferedInputStream(fis);
    PlanitZipInputStream zipStream = PlanitZipInputStream.of( bis, fis);
    return zipStream;       
  }
  
  /** Create a zip file from a URL
   * 
   * @param zipUrl to create zip file for
   * @return created zip file
   * @throws ZipException thrown if error
   * @throws IOException thrown if error
   */
  public static ZipFile createZipFile(URL zipUrl) throws ZipException, IOException {
    return new ZipFile(FileUtils.create(zipUrl));
  }

  /** Collect the input stream for a given zip file entry. The returned wrapper stream should be positioned at the beginning of the internal file
   * Note that invoked needs to close the input stream when done! This approach uses a ZipInputStream rather than a ZipFile for performance reasons
   * as it appears to be around 5 times faster for a regular sized zip file
   *
   * @param zipFileLocation of the zip file
   * @param zipEntryFileName of the file within the zip
   * @param logInfo when true log extensive information on entries in zip file, file size and whether the file exists for debugging purposes
   * @return input stream created, if file not found by iterating over entries null is returned
   * @throws URISyntaxException thrown if error
   * @throws IOException  thrown if input stream cannot be closed when necessary
   */
  public static PlanitZipInputStream createZipEntryInputStream(URL zipFileLocation, String zipEntryFileName, boolean logInfo) throws URISyntaxException, IOException {
    var file = UrlUtils.asLocalPath(zipFileLocation).toAbsolutePath().toFile();
    if(logInfo) LOGGER.info(String.format("[LOG INFO] Zip File exists: %s",file.exists()));

    if(file.exists() && logInfo){
      LOGGER.info(String.format("[LOG INFO] Zip File size: %s",file.length()));
      var zip = new ZipFile(file);
      var content = zip.stream().map(ZipEntry::getName).collect(Collectors.joining(","));
      LOGGER.info(String.format("[LOG INFO] Zip file contents %s ",content));
      zip.close();
    }

    PlanitZipInputStream zis = createZipInputStream(zipFileLocation);
    positionZipEntryInputStream(zis,zipEntryFileName);
    return zis;
  }
  
  /** see {@link #createZipEntryInputStream(URL, String, boolean)} only we do not log any info
   * 
   * @param zipFileLocation of the zip file
   * @param zipEntryFileName of the file within the zip
   * @return input stream created, if file not found by iterating over entries null is returned
   * @throws URISyntaxException thrown if error
   * @throws IOException  thrown if input stream cannot be closed when necessary
   */
  public static PlanitZipInputStream createZipEntryInputStream(URL zipFileLocation, String zipEntryFileName) throws URISyntaxException, IOException {
    return createZipEntryInputStream(zipFileLocation, zipEntryFileName, false);
  }
  
  /** Collect the input stream for a given zip file entry. The returned wrapper stream is positioned at the beginning of the internal file 
   * 
   * @param zipFile to use
   * @param zipEntryFileName of the file within the zip
   * @return input stream created, if file not found by iterating over entries null is returned
   * @throws URISyntaxException thrown if error
   * @throws IOException  thrown if input stream cannot be closed when necessary
   */
  public static PlanitZipFileInputStream createZipEntryInputStream(ZipFile zipFile, String zipEntryFileName) throws URISyntaxException, IOException {
    if(zipFile== null || StringUtils.isNullOrBlank(zipEntryFileName)) {
      return null;
    }
    
    final Enumeration<? extends ZipEntry> entries = zipFile.entries();
    while (entries.hasMoreElements()){
        final ZipEntry entry = entries.nextElement();
        if (entry.getName().equals(zipEntryFileName)) {
          return PlanitZipFileInputStream.of(zipFile,entry); 
      }
    }
    return null;
  }  
  
  /** Move the passed in zip input stream to the starting point of the given zip file entry.
   * 
   * @param zipInputStream to use
   * @param zipEntryFileName of the file within the zip
   * @return true when successfully positioned the input stream, false otherwise
   */
  public static boolean positionZipEntryInputStream(ZipInputStream zipInputStream, String zipEntryFileName) {
    boolean success = false;
    LOGGER.info(String.format("TEST finding entry in zip! %s", zipEntryFileName));
    try {
      for (ZipEntry entry; (entry = zipInputStream.getNextEntry()) != null;) {
        LOGGER.info(String.format("TEST Moving to next entry! %s",entry.getName()));
        if (entry.getName().equals(zipEntryFileName)) {
            success = true;
            LOGGER.info(String.format("TEST FOUND IT",entry.getName()));
            break;
        }
      }
    } catch (IOException e) {
      LOGGER.severe("Unable to collect next entry stream from zip input stream");
    }
    
    return success;
  }  

}
