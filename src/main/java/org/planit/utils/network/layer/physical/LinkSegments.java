package org.planit.utils.network.layer.physical;

import org.planit.utils.graph.GraphEntities;
import org.planit.utils.id.ManagedIdEntities;

/**
 * Primary managed container for linkSegments explicitly and create them on the container via
 * its dedicated factory class
 * 
 * @author markr
  */
public interface LinkSegments extends GraphEntities<LinkSegment>, ManagedIdEntities<LinkSegment> {
  /* do not derive from EdgeSegments<E> since we require to override the factory method return type. This is only
   * allowed when the return type directly derives from the original return type. LinkSegmentsFactory cannot
   * derive from EdgeSegmentFactory since the signature of the factory methods differs. Hence, we must derive from
   * the base interface instead which has an empty dummy factory return type which one can always overwrite and
   * the LinkSegmentFactory is derived from */
  
  /**
   * {@inheritDoc}
   */
  @Override
  public abstract LinkSegmentFactory getFactory();
  
  /**
   * {@inheritDoc}
   */
  @Override
  public abstract LinkSegments clone();  
}
