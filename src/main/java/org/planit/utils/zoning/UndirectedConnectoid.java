package org.planit.utils.zoning;

/**
 * An undirected connectoid is accessed through a referenced node where all incoming/outgoing
 * edges/edge segments potentially have access. Hence it is undirected
 * 
 * @author markr
 *
 */
public interface UndirectedConnectoid extends Connectoid{
 
  
  /** collect the undirected connectoid id
   * @return undirected connectoid id
   */
  long getUndirectedConnectoidId();
}
