package org.planit.utils.network.layer.physical;

import org.planit.utils.graph.GraphEntityFactory;

/** Factory interface for creating links.
 * 
 * @author markr
 *
 */
public interface LinkFactory extends GraphEntityFactory<Link>{

  /**
   * Create new link on links container (not registered on nodes)
   *
   * @param nodeA the first vertex of this edge
   * @param nodeB the second vertex of this edge
   * @param lengthKm length of the link in km
   * @return the created edge
   */
  default public Link registerNew(final Node nodeA, final Node nodeB, double lengthKm){
    return registerNew(nodeA, nodeB, lengthKm, false);
  }
  
  /**
   * Create new link on links container, allow to be registered on nodes if indicated)
   *
   * @param nodeA           the first node in this link
   * @param nodeB           the second node in this link
   * @param lengthKm length of the link in km
   * @param registerOnNodes choice to register new edge on the vertices or not
   * @return the created link
   */
  public abstract Link registerNew(final Node nodeA, final Node nodeB, double lengthKm, boolean registerOnNodes);     
}
