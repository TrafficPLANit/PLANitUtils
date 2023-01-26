package org.goplanit.utils.network.virtual;

import org.goplanit.utils.graph.GraphEntities;

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
  public abstract ConnectoidSegments clone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConnectoidSegments deepClone();
}
