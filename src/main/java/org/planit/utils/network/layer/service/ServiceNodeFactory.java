package org.planit.utils.network.layer.service;

import org.planit.utils.graph.GraphEntityFactory;
import org.planit.utils.network.layer.physical.Node;

/** Factory interface for creating service node instances
 * 
 * @author markr
 *
 */
public interface ServiceNodeFactory extends GraphEntityFactory<ServiceNode> {
 
  /** Create a new service node 
   * 
   * @param networkNode this service node references on a physical layer 
   * @return created service node
   */
  public abstract ServiceNode createNew(Node networkNode);   
  
 
  /** Create a new service node 
   * 
   * @param networkNode this service node references on a physical layer 
   * @return created service node
   */
  public abstract ServiceNode registerNew(Node networkNode);   


  
}
