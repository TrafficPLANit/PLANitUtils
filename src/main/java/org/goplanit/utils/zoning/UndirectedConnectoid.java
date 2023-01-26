package org.goplanit.utils.zoning;

/**
 * An undirected connectoid is accessed through a referenced node where all incoming/outgoing
 * edges/edge segments potentially have access. Hence it is undirected
 * 
 * @author markr
 *
 */
public interface UndirectedConnectoid extends Connectoid{
  
  /** the class to use for the additional directed connectoid id generation */
  public static final Class<UndirectedConnectoid> UNDIRECTED_CONNECTOID_ID_CLASS = UndirectedConnectoid.class;
  
  
  /** collect the undirected connectoid id
   * @return undirected connectoid id
   */
  public abstract long getUndirectedConnectoidId();
  
  /** the class for undirected connectoid id generation
   * 
   * @return class to use
   */
  public default Class<UndirectedConnectoid> getUndirectedConnectoidIdClass(){
    return UNDIRECTED_CONNECTOID_ID_CLASS;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract UndirectedConnectoid clone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract UndirectedConnectoid deepClone();
}
