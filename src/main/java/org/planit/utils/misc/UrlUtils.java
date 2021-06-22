package org.planit.utils.misc;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
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

  
  /** Test if URL is local, i.e. a local file or directory 
   * 
   * @param url to verify
   * @return true when local, false otherwise
   */
  public static boolean isLocal(URL url) {
    String scheme = url.getProtocol();
    return !hasHost(url) && PROTOCOL_FILE.equalsIgnoreCase(scheme);
  }
  
  /** Test if URL is a local file
   * 
   * @param url to verify
   * @return true when local, false otherwise
   */
  public static boolean isLocalFile(URL url) {
    return isLocal(url) && new File(url.getFile()).isFile();
  }
  
  /** Test if URL is a local file
   * 
   * @param url to verify
   * @return true when local, false otherwise
   */
  public static boolean isLocalDirectory(URL url) {
    return isLocal(url) && new File(url.getFile()).isDirectory();
  }  
  
  /** Test if URL is a local file
   * 
   * @param url to verify
   * @return true when local, false otherwise
   */
  public static boolean isLocalZipFile(URL url) {
    return isLocalFile(url) && url.getFile().endsWith(FileUtils.DOT+FileUtils.ZIP);
  }   

  /** Verify if the URL has a host specified
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
  
  /** Append the given relative path to the URL. Implementation based on answer by Martin Senne via
   * https://stackoverflow.com/questions/7498030/append-relative-url-to-java-net-url
   * 
   * @param baseUrl base url
   * @param relativePath to append
   * @return combined URL
   */
  public static URL appendRelativePathToURL(URL baseUrl, String relativePath) {
    /*
      foo://example.com:8042/over/there?name=ferret#nose
      \_/   \______________/\_________/ \_________/ \__/
       |           |            |            |        |
    scheme     authority       path        query   fragment
       |   _____________________|__
      / \ /                        \
      urn:example:animal:ferret:nose

    see https://en.wikipedia.org/wiki/Uniform_Resource_Identifier
    */
    try {

        URI baseUri = baseUrl.toURI();

        String relPathToAdd = StringUtils.removeInitialStringWhenPresent(relativePath, "/");
        String pathWithoutTrailingSlash = StringUtils.removeEndingStringWhenPresent(baseUri.getPath(), "/");
        String combinedRawPath = pathWithoutTrailingSlash + "/" + relPathToAdd;

        return new URI(baseUri.getScheme(),baseUri.getAuthority(),combinedRawPath,baseUri.getQuery(),baseUri.getFragment()).toURL();
    } catch (Exception e) {
      LOGGER.warning(String.format("Unable to append relativePath %s to base URL %s",relativePath, baseUrl.toString()));
    }
    return null;
  }
}
