package org.goplanit.utils.zoning;

import org.goplanit.utils.id.ManagedIdEntityFactory;

/** Factory interface for centroids
 * 
 * @author markr
 *
 */
public interface CentroidFactory extends ManagedIdEntityFactory<Centroid>{

  /**
   * Create a new centroid
   *
   * @return the new centroid created
   */
  public abstract Centroid create();
  
  /**
   * Create a new centroid
   *
   *@param parentZone to use
   * @return the new centroid created
   */
  public abstract Centroid create(Zone parentZone);  
  
}
