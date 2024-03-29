package org.goplanit.utils.network.layer.physical;

import org.goplanit.utils.graph.GraphEntityFactory;

/** Factory interface for creating links.
 * 
 * @author markr
 *
 */
public interface LinkFactory<L extends Link> extends GraphEntityFactory<L>{
 
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
