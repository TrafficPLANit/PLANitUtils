package org.goplanit.utils.network.virtual;

import org.goplanit.utils.graph.ManagedGraphEntities;

import java.util.function.BiConsumer;

/**
 * Container to register and manage conjugate connectoid segments that define the points of access for
 * zones regarding conjugate infrastructure network (layer)
 * 
 * @author markr
 *
 */
public interface ConjugateConnectoidSegments extends ManagedGraphEntities<ConjugateConnectoidSegment> {
  
  /**
   * {@inheritDoc}
   */  
  @Override
  public abstract ConjugateConnectoidSegmentFactory getFactory();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConjugateConnectoidSegments shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConjugateConnectoidSegments deepClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConjugateConnectoidSegments deepCloneWithMapping(BiConsumer<ConjugateConnectoidSegment, ConjugateConnectoidSegment> mapper);
}
