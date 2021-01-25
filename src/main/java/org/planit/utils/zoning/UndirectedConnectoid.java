package org.planit.utils.zoning;

import org.planit.utils.network.physical.Node;

/**
 * An undirected connectoid is accessed through a referenced node where all incoming/outgoing
 * edges/edge segments potentially have access. Hence it is undirected
 * 
 * @author markr
 *
 */
public interface UndirectedConnectoid extends Connectoid{

  /** Collect the physical node this connectoid proposes to create an acess point for its zone
   * 
   * @return access node
   */
  Node getAccessNode(); 
}
