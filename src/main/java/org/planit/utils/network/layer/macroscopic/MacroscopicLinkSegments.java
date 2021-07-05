package org.planit.utils.network.layer.macroscopic;

import org.planit.utils.graph.GraphEntities;

/**
 * Wrapper around GraphEntities interface to support MacroscopicLinkSegments explicitly and create them on the container via
 * its dedicated factory class
 * 
 * @author markr
 *
 */
public interface MacroscopicLinkSegments extends GraphEntities<MacroscopicLinkSegment> {
  /* do not derive from LinkSegments since we require to override the factory method return type. This is only
   * allowed when the return type directly derives from the original return type. MacroscopicLinkSegmentsFactory cannot
   * derive from LinkSegmentFactory since the signature of the factory methods differs. Hence, we must derive from
   * the base interface instead which has an empty dummy factory return type which one can always overwrite and
   * the MacroscopicLinkSegmentFactory is derived from */
  
  /**
   * {@inheritDoc}
   */
  @Override
  public abstract MacroscopicLinkSegmentFactory getFactory();  
}
