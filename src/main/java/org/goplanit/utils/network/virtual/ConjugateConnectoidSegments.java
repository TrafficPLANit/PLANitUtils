package org.goplanit.utils.network.virtual;

import org.goplanit.utils.graph.GraphEntities;

/**
 * Container to register and manage conjugate connectoid segments that define the points of access for
 * zones regarding conjugate infrastructure network (layer)
 * 
 * @author markr
 *
 */
public interface ConjugateConnectoidSegments extends GraphEntities<ConjugateConnectoidSegment>{
  
  /**
   * {@inheritDoc}
   */  
  @Override
  public abstract ConjugateConnectoidSegmentFactory getFactory();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConjugateConnectoidSegments clone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConjugateConnectoidSegments deepClone();
}
