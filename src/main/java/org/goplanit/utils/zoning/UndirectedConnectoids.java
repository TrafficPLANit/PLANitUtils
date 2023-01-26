package org.goplanit.utils.zoning;


/**
 * container and factory class for undirected connectoids
 * 
 * @author markr
 *
 */
public interface UndirectedConnectoids extends Connectoids<UndirectedConnectoid> {

  /**
   * {@inheritDoc}
   */  
  @Override
  public abstract UndirectedConnectoidFactory getFactory();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract UndirectedConnectoids clone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract UndirectedConnectoids deepClone();
}
