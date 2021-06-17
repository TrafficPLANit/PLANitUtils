package org.planit.utils.misc;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

/** Utilities to deal with Java URL instances
 *   
 * @author markr
 *
 */
public class UrlUtils {
  
  /** the logger */
  private static final Logger LOGGER = Logger.getLogger(UrlUtils.class.getCanonicalName());

  
  /** file protocol string */
  public static final String PROTOCOL_FILE = "file";

  
  /** Test if URL is a local file
   * 
   * @param url to verify
   * @return true when local, false otherwise
   */
  public static boolean isLocalFile(URL url) {
    String scheme = url.getProtocol();
    return !hasHost(url) && PROTOCOL_FILE.equalsIgnoreCase(scheme);
  }

  /** Verify if the URL has a has specified
   * 
   * @param url to check
   * @return true when host present, false otherwise
   */
  public static boolean hasHost(URL url) {
    String host = url.getHost();
    return host != null && !"".equals(host);
  }
  
  /** Construct a URL based on a given local file location
   * 
   * @param path to convert
   * @return URL representation
   */
  public static URL createFromPath(String path) {
    try {
      return Paths.get(path).toUri().toURL();
    }catch(Exception e) {
      LOGGER.severe(String.format("Unable to create URL from path string %s", path));
    }
    return null;
  }
  
  /** Construct a URL based no a given path
   * 
   * @param path to convert
   * @return URL representation
   */
  public static URL createFromPath(Path path) {
    try {
      return path.toUri().toURL();
    } catch (MalformedURLException e) {
      LOGGER.severe(String.format("Unable to create URL from Path %s", path));
    }
    return null;
  }  
}