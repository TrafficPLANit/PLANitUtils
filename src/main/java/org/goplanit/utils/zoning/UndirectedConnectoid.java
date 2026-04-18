package org.goplanit.utils.zoning;

import org.goplanit.utils.graph.directed.DirectedVertex;

/**
 * An undirected connectoid is accessed through a referenced node where all incoming/outgoing
 * edges/edge segments potentially have access. Hence, it is undirected
 * 
 * @author markr
 *
 */
public interface UndirectedConnectoid extends Connectoid<ConnectoidAccessZoneEntry>{
  
  /** the class to use for the additional directed connectoid id generation */
  public static final Class<UndirectedConnectoid> UNDIRECTED_CONNECTOID_ID_CLASS = UndirectedConnectoid.class;

  /** the class for undirected connectoid id generation
   *
   * @return class to use
   */
  public default Class<UndirectedConnectoid> getUndirectedConnectoidIdClass(){
    return UNDIRECTED_CONNECTOID_ID_CLASS;
  }
  
  /** collect the undirected connectoid id
   * @return undirected connectoid id
   */
  public abstract long getUndirectedConnectoidId();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract UndirectedConnectoid shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract UndirectedConnectoid deepClone();
}
