package org.planit.utils.zip;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.ZipInputStream;

/**
 * A wrapper around a ZipInputStream that keeps the underlying input streams alive. Useful when creating a zip input stream in one location and we want to return 
 * it to another location without the underlying streams going out of scope
 * 
 * @author markr
 *
 */
public class PlanitZipInputStream extends ZipInputStream{
   
  private final BufferedInputStream bis;
  
  private final FileInputStream fis;
  
  /** Constructor 
   * 
   * @param zipInputStream to use
   * @param bis to use
   * @param fis to use
   */
  protected PlanitZipInputStream(BufferedInputStream bis, FileInputStream fis) {
    super(bis);
    this.bis = bis;
    this.fis= fis;
  }

  /** create PLANit zip input stream containing all input streams that need to remain in scope for the input stream to be accessible
   * 
   * @param bis to use
   * @param fis to use
   * @return zip input stream
   */
  public static PlanitZipInputStream of(BufferedInputStream bis, FileInputStream fis) {
    return new PlanitZipInputStream(bis, fis);
  }
    
  /**
   * close the streams
   * 
   * @throws IOException  thrown if error
   */
  public void close() throws IOException {
    super.close();
    bis.close();
    fis.close();
  }

}
