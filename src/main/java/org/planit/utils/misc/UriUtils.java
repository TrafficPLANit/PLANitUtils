package org.planit.utils.misc;

import java.net.JarURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.logging.Logger;

/**
 * Uitilities to modify and deal with URIs
 * 
 * @author markr
 *
 */
public class UriUtils {
  
  /** the logger to use */
  private static final Logger LOGGER = Logger.getLogger(UriUtils.class.getCanonicalName());
  
  /** jar uri scheme */
  private static final String JAR_SCHEME = "jar";
  
  /** fileuri scheme */
  private static final String FILE_SCHEME = "file";    
  
  /** Collect working directory as URI
   * 
   * @return URI of working directory
   * @throws URISyntaxException 
   */
  public static URI getWorkingDirUri() {
    return Path.of("").toUri();
  }    
  
  /** find user dir URI of JVM, which is the current working dir unless overridden upon instantiation of the Jvm
   * 
   * @return URI of JVM user dir property
   * @throws URISyntaxException 
   */
  public static URI getJvmUserDirUri() throws URISyntaxException {
    return ClassLoader.getSystemClassLoader().getResource(".").toURI();
  }  
  
  /** check if URI is contained within a JAR file
   * 
   * @param uri to check
   * @return true when within jar, false otherwise
   */
  public static boolean isInJar(final URI uri) {
    return JAR_SCHEME.equals(uri.getScheme());
  }
  
  /** Check if URI is a regular file in the default file system, e.g., a local file not in a zip or jar
   * 
   * @param uri to check
   * @return true when within jar, false otherwise
   */
  public static boolean isRegularFile(final URI uri) {
    return FILE_SCHEME.equals(uri.getScheme());
  }
  
  /** Make sure it is a directory by adding a trailing forward slash if not present
   * 
   * @return URI as directory
   */
  public static URI asDirectory(URI uri) {
    return !uri.toString().endsWith("/") ? URI.create(uri.toString().concat("/")) : uri;
  }
  
  /** collect only the jar URI part by stripping all beyond the jar, i.e., remove the part that points to a location within the jar
   * If this is not a jar URI, we return the original URI
   * 
   * @param uri to extract jar component from
   * @return URI with only jar component
   */
  public static URI jarUriOfJarEntry(URI uri) {

    try {
      JarURLConnection jarEntryConn = (JarURLConnection) uri.toURL().openConnection();
      return UriUtils.uriOfJarUrl(jarEntryConn.getJarFileURL());
    } catch (Exception e) {
      LOGGER.severe(String.format("Unable to extract jar component from uri %s", uri.toString()));
    }
    
    return uri;
}
  
  /** Specifically meant for URIs that have been made relative, e.g. no longer have a scheme (or are of type file), and therefore represent or provide a path.
   * We only collect the path component, if absent, we remove the scheme and try again. If still not possible
   * we return original uri. When path is found, we ensure that a "/" is included in the beginning to signal the uri location is to
   * be found from the root of whatever is the root of this application
   * 
   * @return URI as relative from root.
   */
  public static URI asRelativeUriFromRoot(URI uri) {
    URI relativeToRoot = URI.create(uri.toString());
    if(hasScheme(uri)) {
      relativeToRoot = removeInitialScheme(relativeToRoot);
      return asRelativeUriFromRoot(relativeToRoot);
    }
    return !relativeToRoot.toString().startsWith("/") ? URI.create("/"+relativeToRoot.toString()) : uri;
  }
  
  /** create URI without its initial scheme (if any), if no scheme, return original uri
   * 
   * @param uri to check
   * @return true when path is present false otherwise
   */
  public static URI removeInitialScheme(URI uri) {
    if(hasScheme(uri)) {
      return URI.create(uri.getSchemeSpecificPart().toString());
    }
    return uri;
  }    

  /** Check if scheme is present
   * 
   * @param uri to check
   * @return true when scheme is present false otherwise
   */
  public static boolean hasScheme(URI uri) {
    return uri.getScheme()!=null && !uri.getScheme().isBlank();
  }  
  
  /** Check if path is present
   * 
   * @param uri to check
   * @return true when path is present false otherwise
   */
  public static boolean hasPath(URI uri) {
    return uri.getPath()!=null && !uri.getPath().isBlank();
  }

  /** Create a URI knowing that the passed in URL points to a jar file, i.e., we transform something from
   * "file:/<path>/xyz.jar to "jar:file:/<path>/xyz.jar!/".
   * 
   * @param jarFileURL to create URI for
   * @return created URI
   */
  public static URI uriOfJarUrl(URL jarFileURL) {
    return URI.create("jar:"+jarFileURL.toString()+ "!/");    
  }

  /** Extract relative URI based on provided URI and the current working directory of the application.
   * If there is no overlap copy of original URI is returned
   * 
   * @param uri to relativise
   * @return created URI
   */
  public static URI asRelativeUriFromWorkingDir(final URI uri) {
    URI workingDirUri = getWorkingDirUri();    
    return workingDirUri.relativize(uri);
  }

  /** Extract relative URI based on provided URI and the user.dir property set for the application.
   * If there is no overlap copy of original URI is returned
   * 
   * @param URI to relativise
   * @return created URI
   * @throws URISyntaxException thrown if error
   */  
  public static URI asRelativeUriFromUserDir(URI uri) throws URISyntaxException {
    URI workingDirUri = getJvmUserDirUri();    
    return workingDirUri.relativize(uri);
  }

  /** Extract relative URI based on provided URI, knowing that the uri points to a location within a jar. We relativise against the jar location
   * stripping all but the internal location relative to the jar. If the uri is not pointing to a jar location, the original uri is returned
   * 
   * @param URI to relativise
   * @return created URI
   */    
  public static URI asRelativeUriFromJar(URI uri) {
    if(!isInJar(uri)) {
      return uri;
    }
    
    /* extract location of jar */
    URI jarLocationURI = UriUtils.jarUriOfJarEntry(uri);                  
    /* remove scheme, so that jar is treated as file, and the path can be collected which is used for relativisation, otherwise
     * path is null */
    URI desiredUriWithoutScheme = UriUtils.removeInitialScheme(uri);
    URI jarLocationURIWithoutScheme = UriUtils.removeInitialScheme(jarLocationURI);           
    return jarLocationURIWithoutScheme.relativize(desiredUriWithoutScheme);
  }

  /** Method that attempts to strip everything from the URI to make it a relative URI. this is useful if the URI points to a resource that should 
   * be available within the application. Given that resources are to be streamed from a String, apply this method and convert to String afterward
   * to let Java search for the resource. Often we cannot provide the (absolute) URI because it does not work in case it, for example, points to non 
   * file like objects, or files contained within other file systems, such as jars. When providing the correct relative URI, Java can work around this
   * whereas otherwise it cannot.
   * <p>
   * We attempt three ways to make the URI relative: (i) relative against the current working directory, (ii) relative against the user.dir, (iii) relative
   * against the URI jar file (if applicable). If none succeed, the original URI is returned
   * 
   * @param uri to convert
   * @return relativised URI
   * @throws URISyntaxException thrown if error
   */
  public static URI asRelativeUri(URI uri) throws URISyntaxException {

    URI relativeResourceUri = asRelativeUriFromWorkingDir(uri);     
    if(relativeResourceUri.getScheme()!=null) {        
      /* scheme present, so relativisation did not succeed, try to use JVM working dir instead, which might be overridden compared to overall application dir that instantiated this Java run */
      relativeResourceUri = UriUtils.asRelativeUriFromUserDir(uri);
    }      
    if(relativeResourceUri.getScheme()!=null && UriUtils.isInJar(uri)) {
      /* again did not work, so likely we are running this jar from other dir and user.dir was not overridden or not to a common location for resource 
       * last attempt is to see if resource is in a jar and when resources of this jar are available, we relativise against the jar location instead and hope
       * the resource can be located that way*/
      relativeResourceUri = UriUtils.asRelativeUriFromJar(uri);
      /* make sure that we search from root of jar since relativisation removes shared initial "/" as well, which is not what we want */
      relativeResourceUri = UriUtils.asRelativeUriFromRoot(relativeResourceUri);
    }
    return relativeResourceUri;
  }    

}
