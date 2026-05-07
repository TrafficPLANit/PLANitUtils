package org.goplanit.utils.zoning.connectoid;

/**
 * An undirected connectoid is accessed through a referenced node where all incoming/outgoing
 * edges/edge segments potentially have access. Hence, it is undirected
 * 
 * @author markr
 *
 */
public interface OdConnectoid extends Connectoid{
  
  /** the class to use for the additional directed connectoid id generation */
  public static final Class<OdConnectoid> OD_CONNECTOID_ID_CLASS = OdConnectoid.class;

  /** the class for undirected connectoid id generation
   *
   * @return class to use
   */
  public default Class<OdConnectoid> getUndirectedConnectoidIdClass(){
    return OD_CONNECTOID_ID_CLASS;
  }
  
  /** collect the undirected connectoid id
   * @return undirected connectoid id
   */
  public abstract long getOdConnectoidId();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract OdConnectoid shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract OdConnectoid deepClone();
}
