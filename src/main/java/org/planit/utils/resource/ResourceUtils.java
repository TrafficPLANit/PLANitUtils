package org.planit.utils.resource;

import java.net.URL;

/** Utilities to access resources in the Java environment
 * 
 * @author markr
 */
public class ResourceUtils {

  /** find the resource URL via this class' classloader
   * 
   * @param resourceLocation to find URL for
   * @return the URL of the resource
   */
  public static URL getResourceUrl(String resourceLocation) {
    /* collect the resource assuming it is available in the context where the utils are made available */
    return ResourceUtils.class.getClassLoader().getResource(resourceLocation);
  }
  
  
}
