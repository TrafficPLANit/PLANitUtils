package org.goplanit.utils.network.virtual.physical;

import org.goplanit.utils.graph.ManagedGraphEntities;

import java.util.function.BiConsumer;

/**
 * Container to register and manager connectoid links.
 * 
 * @author markr
 *
 */
public interface ConnectoidLinks extends ManagedGraphEntities<ConnectoidLink> {
  
  /**
   * {@inheritDoc}
   */  
  @Override
  public abstract ConnectoidLinkFactory getFactory();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConnectoidLinks shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConnectoidLinks deepClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConnectoidLinks deepCloneWithMapping(BiConsumer<ConnectoidLink, ConnectoidLink> mapper);
}
