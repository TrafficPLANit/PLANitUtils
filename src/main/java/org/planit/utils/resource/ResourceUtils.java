package org.planit.utils.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    URL url = getResourceUrl(resourceLocation);
    if(url==null) {
      LOGGER.warning(String.format("Unable to create resource URL and therefore URI for %s",resourceLocation));
      return null;
    }
    return url.toURI(); 
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
   * @throws URISyntaxException thrown if error
   */  
  public static InputStreamReader getResourceAsInputStreamReader(final URI uri) {
    try {      
      /* depending on whether or not locaton is in a jar we create the input reader differently */
      if(isResourceInJar(uri)) {
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
   * @throws URISyntaxException thrown if error
   */  
  public static InputStream getResourceAsInputStream(final URI uri) {
    try {
      
      URI thisUri = Path.of("").toUri();
      /* extract relative location to cwd (if possible), so we can provide a location relative to this */     
      URI relativeResourceUri = thisUri.relativize(uri);
      LOGGER.warning("thisuri: "+thisUri.toString() );
      LOGGER.warning("desired uri: "+uri.toString() );
      LOGGER.warning("relativised desired uri: "+relativeResourceUri.toString() );
      
      Path finalResourcePath = null;
      if(relativeResourceUri.getScheme()!=null) {
        
        //TODO --> NOT WORKING UGLY AS WELL
        
        /** Consider using this for jar based approach instead and then obtain the input stream
         * or path from the resource URL instead or something
        URL[] urls = new URL[]{new File("/path/to/com.me.Y.jar").toURI().toURL())};
        URLClassLoader classLoader = new URLClassLoader(urls);
        URL resource = classLoader.findResource("/config/conf.txt");
        **/
        
        /* scheme still present, meaning that relative path could not be extracted (because uri is not somewhere in the cwd hierarchy) and therefore the
         * beginning of uri is kept (which contains the scheme). In that case we extract a path directly from the URI, otherwise the 
         * relativisation already removed the scheme (at the front) and is effectively a relative path already and no additional effort is needed (which would break
         * the call) and we simply collect the string representation
         */
        String resourcePathInFileSystem = null;
        FileSystem fs = FileSystems.getDefault(); //regular file system
        if(isResourceInJar(uri)) {
          /* collect jar file system within jar */
          JarURLConnection jarEntryConn = (JarURLConnection) uri.toURL().openConnection();
          URL jarLocationURL = jarEntryConn.getJarFileURL(); 
          LOGGER.warning("jar location url: "+jarLocationURL.toString() );
          LOGGER.warning("jar location path : "+Paths.get(jarLocationURL.toURI()).toString() );
          fs = FileSystems.newFileSystem(Paths.get(jarLocationURL.toURI()), null);
          /* determine location within jar file system of resource */
          resourcePathInFileSystem = jarEntryConn.getEntryName();          
        }else {
          /* determine location of resource within default file system */
          resourcePathInFileSystem = uri.getPath();
        }
        finalResourcePath = fs.getPath(resourcePathInFileSystem);        
        LOGGER.warning("scheme not null:"+ relativeResourceUri.getScheme().toString() );        
        fs.close();
      }else {
        /* relativised, so scheme and other preceding clutter is removed already and we know we obtain it relative to cwd so simply use relativised string location 
         * in the default file system's path form */
        finalResourcePath = Path.of(relativeResourceUri.toString());
      }
      LOGGER.warning("relative source location :"+ finalResourcePath.toString() );
        
      /* depending on whether or not location is in a jar we create the input reader differently */
      if(isResourceInJar(uri)) {
        LOGGER.warning("in jar use:"+ finalResourcePath.toString() );
        /* input stream */
        return getResourceAsStream(finalResourcePath.toString());
      }else {
        /* file based stream */
        LOGGER.warning("not in jar use:"+ finalResourcePath.toFile().toString() );
        return new FileInputStream(finalResourcePath.toFile());
      }
    }catch(Exception e) {
      e.printStackTrace();
      LOGGER.warning(e.getMessage());
      LOGGER.warning(String.format("Unable to create an input stream for resource %s",uri.toString()));
    }
      return null;
  }  
  
  
}
