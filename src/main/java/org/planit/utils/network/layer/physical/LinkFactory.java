package org.planit.utils.network.layer.physical;

import org.planit.utils.exceptions.PlanItException;
import org.planit.utils.graph.GraphEntityFactory;

/** Factory interface for creating links.
 * 
 * @author markr
 *
 * @param <L> type of link
 */
public interface LinkFactory<L extends Link> extends GraphEntityFactory<L>{

  /**
   * Create new link on links container (not registered on nodes)
   *
   * @param nodeA the first vertex of this edge
   * @param nodeB the second vertex of this edge
   * @param lengthKm length of the link in km
   * @return the created edge
   * @throws PlanItException thrown if there is an error
   */
  default public L registerNew(final Node nodeA, final Node nodeB, double lengthKm) throws PlanItException{
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
   * @throws PlanItException thrown if there is an error
   */
  public abstract L registerNew(final Node nodeA, final Node nodeB, double lengthKm, boolean registerOnNodes) throws PlanItException;     
}
