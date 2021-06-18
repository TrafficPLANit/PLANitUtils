package org.planit.utils.zip;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;

import org.planit.utils.misc.UrlUtils;

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
   */
  public static PlanitZipInputStream getZipInputStream(URL zipFileLocation) {
    if(!UrlUtils.isLocalZipFile(zipFileLocation)) {
      return null;
    }
    PlanitZipInputStream zipStream = null; 
    try{
      FileInputStream fis = new FileInputStream(new File(zipFileLocation.toURI()));
      BufferedInputStream bis = new BufferedInputStream(fis);
      zipStream = PlanitZipInputStream.of( bis, fis);
      return zipStream;
    }catch(Exception e) {
      LOGGER.severe(String.format("Unable to create zip input stream for %s",zipFileLocation));
    }        
    
    return null;
  }
  
  /** Collect the input stream for a given zip file entry. The returned wrapper stream should be positioned at the beginning of the internal file 
   * Note that invoked needs to close the input stream when done!
   * 
   * @param zipFileLocation of the zip file
   * @param zipEntryFileName of the file within the zip
   * @return input stream created
   */
  public static PlanitZipInputStream getZipEntryInputStream(URL zipFileLocation, String zipEntryFileName) {
    PlanitZipInputStream zis = getZipInputStream(zipFileLocation);
    if(zis == null || zipEntryFileName==null) {
      return null;
    }
    
    try {
      for (ZipEntry entry; (entry = zis.getNextEntry()) != null;) {
        if (entry.getName().equals(zipEntryFileName)) {
            return zis;
        }
      }
    } catch (IOException e) {
      LOGGER.severe(String.format("Unable to collect next entry stream from zip file for %s",zipFileLocation));
    }
    
    return null;
  }  

}
