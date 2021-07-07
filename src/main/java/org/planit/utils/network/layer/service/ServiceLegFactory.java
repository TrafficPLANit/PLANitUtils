package org.planit.utils.network.layer.service;

import java.util.List;

import org.planit.utils.graph.GraphEntityFactory;
import org.planit.utils.network.layer.physical.Link;

/** Factory interface for creating service leg instances
 * 
 * @author markr
 *
 */
public interface ServiceLegFactory extends GraphEntityFactory<ServiceLeg> {

  /**
   * Create new service leg on container (not registered on nodes)
   *
   * @param nodeA the first service node 
   * @param nodeB the second service node
   * @return the created service leg
   */
  default public ServiceLeg registerNew(final ServiceNode nodeA, final ServiceNode nodeB){
    return registerNew(nodeA, nodeB, false);
  }
  
  /**
   * Create new service leg on container
   *
   * @param nodeA the first service node 
   * @param nodeB the second service node
   * @param registerOnNodes choice to register new leg on the service nodes or not
   * @return the created service leg
   */
  public abstract ServiceLeg registerNew(final ServiceNode nodeA, final ServiceNode nodeB, boolean registerOnNodes);  
  
  /**
   * Create new link on links container, allow to be registered on nodes if indicated)
   *
   * @param nodeA           the first node in this link
   * @param nodeB           the second node in this link
   * @param networkLayerLinks  the underlying links that make up this leg
   * @param registerOnNodes choice to register new edge on the vertices or not
   * @return the created link
   */
  public abstract ServiceLeg registerNew(final ServiceNode nodeA, final ServiceNode nodeB, final List<Link> networkLayerLinks, boolean registerOnNodes);      
  
}
