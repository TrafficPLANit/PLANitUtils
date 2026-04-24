package org.goplanit.utils.zoning;


import java.util.function.BiConsumer;

/**
 * container and factory class for undirected connectoids
 * 
 * @author markr
 *
 */
public interface OdConnectoids extends Connectoids<OdConnectoid> {

  /**
   * {@inheritDoc}
   */  
  @Override
  public abstract OdConnectoidFactory getFactory();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract OdConnectoids shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract OdConnectoids deepClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract OdConnectoids deepCloneWithMapping(BiConsumer<OdConnectoid, OdConnectoid> mapper);
}
