package org.goplanit.utils.network.layer.physical;

import org.goplanit.utils.graph.ManagedGraphEntities;

/**
 * Primary managed container for conjugate linkSegments explicitly and create them on the container via
 * its dedicated factory class
 * 
 * @author markr
  */
public interface ConjugateLinkSegments extends ManagedGraphEntities<ConjugateLinkSegment> {
  /* do not derive from ConjugateEdgeSegments<E> since we require to override the factory method return type. This is only
   * allowed when the return type directly derives from the original return type. LinkSegmentsFactory cannot
   * derive from EdgeSegmentFactory since the signature of the factory methods differs. Hence, we must derive from
   * the base interface instead which has an empty dummy factory return type which one can always overwrite and
   * the LinkSegmentFactory is derived from */
  
  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConjugateLinkSegmentFactory getFactory();
  
  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConjugateLinkSegments clone();  
}
