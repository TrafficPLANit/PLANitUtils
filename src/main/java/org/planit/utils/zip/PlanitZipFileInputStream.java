package org.planit.utils.zip;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * A wrapper around a ZipFile's InputStream that keeps the underlying file open while an entry is being streamed. 
 * Useful when creating a the zip file stream in one location and we want to return 
 * it to another location without the underlying file being closed
 * 
 * @author markr
 *
 */
public class PlanitZipFileInputStream extends InputStream{
    
  private final ZipFile zipFile;
  
  private final InputStream wrappedInputStream;
  
  /** Constructor 
   * 
   * @param zipFile the stream originates from
   * @param entry to base stream on
   * @throws IOException thrown if error
   */
  protected PlanitZipFileInputStream(ZipFile zipFile, ZipEntry entry) throws IOException {
    super();
    this.zipFile = zipFile;
    this.wrappedInputStream = zipFile.getInputStream(entry);    
  }

  /** create PLANit zip input stream containing all input streams that need to remain in scope for the input stream to be accessible
   * 
   * @param zipFile to use
   * @param entry to base stream on
   * @return zip file input stream
   * @throws IOException thrown if error
   */
  public static PlanitZipFileInputStream of(ZipFile zipFile, ZipEntry entry) throws IOException {
    return new PlanitZipFileInputStream(zipFile, entry);
  }
    
  /**
   * close the streams
   * 
   * @throws IOException  thrown if error
   */
  @Override
  public void close() throws IOException {
    super.close();
    wrappedInputStream.close();
    zipFile.close();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int read() throws IOException {
    return wrappedInputStream.read();
  }

}
