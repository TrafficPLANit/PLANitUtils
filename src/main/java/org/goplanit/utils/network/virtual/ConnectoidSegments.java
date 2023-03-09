package org.goplanit.utils.network.virtual;

import org.goplanit.utils.graph.GraphEntities;

import java.util.function.BiConsumer;

/**
 * Container to register and manage connectoid segments that define the points of access for
 * zones regarding infrastructure network (layer)
 * 
 * @author markr
 *
 */
public interface ConnectoidSegments extends GraphEntities<ConnectoidSegment>{
  
  /**
   * {@inheritDoc}
   */  
  @Override
  public abstract ConnectoidSegmentFactory getFactory();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConnectoidSegments shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConnectoidSegments deepClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConnectoidSegments deepCloneWithMapping(BiConsumer<ConnectoidSegment, ConnectoidSegment> mapper);
}
