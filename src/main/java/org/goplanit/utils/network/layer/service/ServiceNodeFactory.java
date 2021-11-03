package org.goplanit.utils.network.layer.service;

import org.goplanit.utils.graph.GraphEntityFactory;
import org.goplanit.utils.network.layer.physical.Node;

/** Factory interface for creating service node instances
 * 
 * @author markr
 *
 */
public interface ServiceNodeFactory extends GraphEntityFactory<ServiceNode> {
 
  /** Create a new service node 
   * 
   * @param parentNetworkNode this service node references on a physical layer 
   * @return created service node
   */
  public abstract ServiceNode createNew(Node parentNetworkNode);   
  
 
  /** Create a new service node 
   * 
   * @param parentNetworkNode this service node references on a physical layer 
   * @return created service node
   */
  public abstract ServiceNode registerNew(Node parentNetworkNode);   


  
}
