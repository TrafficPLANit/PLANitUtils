package org.goplanit.utils.network.virtual.physical.conjugate;

import org.goplanit.utils.graph.ManagedGraphEntities;

import java.util.function.BiConsumer;

/**
 * Container to register and manager conjugate connectoid links.
 * 
 * @author markr
 *
 */
public interface ConjugateConnectoidLinks extends ManagedGraphEntities<ConjugateConnectoidLink> {
  
  /**
   * {@inheritDoc}
   */  
  @Override
  public abstract ConjugateConnectoidLinkFactory getFactory();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConjugateConnectoidLinks shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConjugateConnectoidLinks deepClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConjugateConnectoidLinks deepCloneWithMapping(
          BiConsumer<ConjugateConnectoidLink, ConjugateConnectoidLink> mapper);
}
