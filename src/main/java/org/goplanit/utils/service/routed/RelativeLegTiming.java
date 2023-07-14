package org.goplanit.utils.service.routed;

import org.goplanit.utils.network.layer.service.ServiceLegSegment;
import org.goplanit.utils.zoning.Zone;

import java.time.LocalTime;

/**
 * Refer to a service leg and its duration and dwell time on a scheduled routed trip
 * 
 * @author markr
 *
 */
public interface RelativeLegTiming extends Cloneable {

  /**
   * Collect parent leg segment
   * 
   * @return parent leg segment
   */
  public abstract ServiceLegSegment getParentLegSegment();

  /**
   * Collect duration
   * 
   * @return duration
   */
  public abstract LocalTime getDuration();

  /**
   * Collect dwell time
   * 
   * @return dwell time
   */
  public abstract LocalTime getDwellTime();

  /**
   * Verify if a parent leg segment is registered for this timing
   *
   * @return true when present, false otherwise
   */
  public default boolean hasParentLegSegment(){
    return getParentLegSegment() != null;
  }

  @Override
  public boolean equals(Object other);
}
