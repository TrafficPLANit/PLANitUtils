package org.planit.utils.resource;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Collections;
import java.util.logging.Logger;

/** Utilities to access resources in the Java environment
 * 
 * @author markr
 */
public class ResourceUtils {
  
  /** logger to use */
  private static final Logger LOGGER = Logger.getLogger(ResourceUtils.class.getCanonicalName());
  
  /** jar uri scheme */
  private static final String JAR = "jar";

  /** find the resource URL via this class' classloader
   * 
   * @param resourceLocation to find URL for
   * @return the URL of the resource
   */
  public static URL getResourceUrl(String resourceLocation) {
    /* collect the resource assuming it is available in the context where the utils are made available */
    return ResourceUtils.class.getClassLoader().getResource(resourceLocation);
  }
  
  /** find the resource URI via this class' class loader
   * 
   * @param resourceLocation to find URI for
   * @return the URI of the resource
   * @throws URISyntaxException thrown if error
   */
  public static URI getResourceUri(String resourceLocation) throws URISyntaxException{
    return getResourceUrl(resourceLocation).toURI();
  }  

  /** check if URI is contained within a JAR file
   * 
   * @param uri to check
   * @return true when within jar, false otherwise
   */
  public static boolean isResourceInJar(URI uri) {
    return JAR.equals(uri.getScheme());
  }

  /** Collect the jar as a Filesystem to access its internals
   * 
   * @param uri to use
   * @return FileSystem of Jar
   * @throws IOException thrown if error
   */
  public static FileSystem getJarFileSystem(URI uri) throws IOException {
    return FileSystems.newFileSystem(uri, Collections.emptyMap(), ResourceUtils.class.getClassLoader());    
  }
  
  /** Collect a resource as a stream based on provided location
   * 
   * @param resourceLocation to use  
   * @return input stream
   */
  public static InputStream getResourceAsStream(final String resourceLocation) {
    /* some useful information difference between accessing resources cia .class and .class.getClassLoader on
     * https://stackoverflow.com/questions/3369794/how-to-read-a-file-from-jar-in-java/3370096. Generally, it appears
     * there is little benefit in using the class loader, it is simply more likely to fail, so we use the class instead */
    InputStream inputStream = ResourceUtils.class.getResourceAsStream(resourceLocation);    
    if(inputStream == null) {
      LOGGER.warning(String.format("Unable to create input stream for resource location %s", resourceLocation));  
    }
    return inputStream;
  }
  
  /** Collect a resource input stream reader, which is either file based or not depending whether or not the resource is located in a  jar or not
   * 
   * @param resourceLocation to use  
   * @param uri to deduce whether or not this resource resides within a jar or not, which drives the type of input reader that is to be
   * created, if null we attempt to create it using the resourceLocation
   * @return input stream reader created, or null if something happened
   * @throws URISyntaxException thrown if error
   */  
  public static InputStreamReader getResourceAsInputStreamReader(Path resourceLocation, final URI uri) {
    try {
      URI localUri = (uri==null) ? getResourceUri(resourceLocation.toString()) : uri;
      if(localUri==null) {
        return null;
      }
      
      /* depending on whether or not locaton is in a jar we create the input reader differently */
      if(isResourceInJar(localUri)) {
        /* input stream */
        return new InputStreamReader(getResourceAsStream(resourceLocation.toString()));
      }else {
        /* file based */
        return new InputStreamReader(new FileInputStream(resourceLocation.toFile()), "UTF-8");
      }
    }catch(Exception e) {
      LOGGER.warning(e.getMessage());
      LOGGER.warning(String.format("Unable to create an input stream reader for resource %s",resourceLocation.toString()));
    }
    return null;
  }  
  
  
}
