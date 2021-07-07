package org.planit.utils.network.layer.service;

import org.planit.utils.graph.GraphEntities;

/**
 * Container for service leg segments
 * 
 * @author markr
  */
public interface ServiceLegSegments extends GraphEntities<ServiceLegSegment> {
    
  /* do not derive from link segments since we require to override the factory method return type. This is only
   * allowed when the return type directly derives from the original return type. ServiceLegSegmentFactory cannot
   * derive from LinkSegmentFactory since the signature of the factory methods differs. Hence, we must derive from
   * the base interface instead which has an empty dummy factory return type which one can always overwrite and
   * the ServiceLegFactory is derived from */
  
  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ServiceLegSegmentFactory getFactory();
}
