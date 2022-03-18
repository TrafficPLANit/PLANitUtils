package org.goplanit.utils.time;

import org.goplanit.utils.id.ExternalIdAble;
import org.goplanit.utils.misc.StringUtils;

/**
 * Represents a time period within the day. Used to determine the duration and start time of trips for example.
 * We internally adopt seconds as the unit
 * 
 * @author markr
 *
 */
public interface TimePeriod extends ExternalIdAble{
  

  /**
   * Return the start time
   * 
   * @return start time
   */
  public abstract long getStartTimeSeconds();

  /**
   * Return the duration in seconds
   * 
   * @return duration
   */
  public abstract long getDurationSeconds();

  /**
   * Return the description
   * 
   * @return description of this TimePeriod
   */
  public abstract String getDescription();
    
  /** Verify if time period has a description
   * 
   * @return true when description present, false otherwise
   */
  public default boolean hasDescription() {
    return !StringUtils.isNullOrBlank(getDescription());
  }

  /** Get the duration in hours
   * 
   * @return duration in hours
   */
  public default Double getDurationHours() {
    return getDurationSeconds()/3600.0;
  }

}
