package org.planit.utils.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.Collections;
import java.util.logging.Logger;

import org.planit.utils.misc.UriUtils;

/** Utilities to access resources in the Java environment
 * 
 * @author markr
 */
public class ResourceUtils {
  
  /** logger to use */
  private static final Logger LOGGER = Logger.getLogger(ResourceUtils.class.getCanonicalName());
  
  /** find the resource URL via this class' classloader
   * 
   * @param resourceLocation to find URL for
   * @return the URL of the resource
   */
  public static URL getResourceUrl(final String resourceLocation) {
    /* collect the resource assuming it is available in the context where the utils are made available */
    return ResourceUtils.class.getClassLoader().getResource(resourceLocation);
  }
  
  /** find the resource URI via this class' class loader
   * 
   * @param resourceLocation to find URI for
   * @return the URI of the resource
   * @throws URISyntaxException thrown if error
   */
  public static URI getResourceUri(final String resourceLocation) throws URISyntaxException{
    URL url = getResourceUrl(resourceLocation);
    if(url==null) {
      LOGGER.warning(String.format("Unable to create resource URL and therefore URI for %s",resourceLocation));
      return null;
    }
    return url.toURI(); 
  }  
  
  /** Collect the jar as a FileSystem to access its internals
   * 
   * @param uri to use
   * @return FileSystem of Jar
   * @throws IOException thrown if error
   */
  public static FileSystem getJarFileSystem(final URI uri) throws IOException {
    return FileSystems.newFileSystem(uri, Collections.emptyMap(), ResourceUtils.class.getClassLoader());    
  }
  
  /** Collect a resource as a stream based on provided location
   * 
   * @param resourceLocation to use  
   * @return input stream
   */
  public static InputStream getResourceAsStream(final String resourceLocation) {
    /* some useful information difference between accessing resources via .class and .class.getClassLoader on
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
   * @param uri to deduce whether or not this resource resides within a jar or not, which drives the type of input reader that is to be
   * created, if null we attempt to create it using the resourceLocation
   * @return input stream reader created, or null if something happened
   */  
  public static InputStreamReader getResourceAsInputStreamReader(final URI uri) {
    try {      
      /* depending on whether or not location is in a jar we create the input reader differently */
      if(UriUtils.isInJar(uri)) {
        /* input stream */
        return new InputStreamReader(getResourceAsInputStream(uri));
      }else {
        /* file based */
        return new InputStreamReader(getResourceAsInputStream(uri), "UTF-8");
      }
    }catch(Exception e) {
      LOGGER.warning(e.getMessage());
      LOGGER.warning(String.format("Unable to create an input stream reader for resource %s",uri.toString()));
    }
    return null;
  }  
  
  /** Collect a resource input stream, which is either file based or not depending whether or not the resource is located in a  jar or not
   * 
   * @param uri to deduce whether or not this resource resides within a jar or not, which drives the type of input reader that is to be
   * created, if null we attempt to create it using the resourceLocation
   * @return input stream reader created, or null if something happened
   */  
  public static InputStream getResourceAsInputStream(final URI uri) {
    try {
      
      /* we must convert the universal URI back to a local string that we know we can use to extract an input stream. This is more complicated than it 
       * seems, since depending on whether or not we run within the IDE, externally, or are invoked by a third party process, both the URI and the context
       * i.e. current working dir differ and in the end we only want a relative location to the root directory of this process for this to work properly
       * So:
       * 1) IDE: all local files relative to current working dir of the project --> 
       *          current working dir can be used to create relative path and use file stream to parse as it is a regular file
       * 2) RUNNING JAR IN SAME DIR --> 
       *          current working dir is jar dir s can be used to create relative path, but files are within jar so cannot create file input stream (they are in jar file system, not default file system and therefore are
       *          not considered a regular file in Java, so FileInputStream does not work, use class loader stream instead
       * 3) RUNNING JAR from other dir -->
       *          current working dir is NOT jar dir, create relative path by first finding jar URI and then relative against that instead, also use class loader stream
       */
      URI relativeResourceUri = UriUtils.asRelativeUri(uri);      
        
      /* depending on whether or not location is in a jar we create the input reader differently */
      if(UriUtils.isInJar(uri)) {
        /* input stream */
        return getResourceAsStream(relativeResourceUri.toString()/*"/logging.properties"*/);
      }else {
        /* file based stream */
        return new FileInputStream(new File(uri));
      }
    }catch(Exception e) {
      e.printStackTrace();
      LOGGER.warning(e.getMessage());
      LOGGER.warning(String.format("Unable to create an input stream for resource %s",uri.toString()));
    }
    return null;
  }  
  
  
}
