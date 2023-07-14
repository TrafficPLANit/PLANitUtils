package org.goplanit.utils.zoning;


import java.util.function.BiConsumer;

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
  public abstract UndirectedConnectoids shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract UndirectedConnectoids deepClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract UndirectedConnectoids deepCloneWithMapping(BiConsumer<UndirectedConnectoid, UndirectedConnectoid> mapper);
}
