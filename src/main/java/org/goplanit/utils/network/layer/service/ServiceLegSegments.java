package org.goplanit.utils.network.layer.service;

import org.goplanit.utils.graph.ManagedGraphEntities;

/**
 * Container for service leg segments
 * 
 * @author markr
  */
public interface ServiceLegSegments extends ManagedGraphEntities<ServiceLegSegment> {
    
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

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ServiceLegSegments clone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ServiceLegSegments deepClone();

  /**
   * Verify if present
   *
   * @param serviceLegSegmentId to verify
   * @return true when present, false otherwise
   */
  public default boolean hasServiceLegSegment(long serviceLegSegmentId){
    return containsKey(serviceLegSegmentId);
  }

  /**
   * Verify if present
   *
   * @param parentLegSegment to verify
   * @return true when present, false otherwise
   */
  public default boolean hasServiceLegSegment(ServiceLegSegment parentLegSegment){
    return hasServiceLegSegment(parentLegSegment.getId());
  }
}
