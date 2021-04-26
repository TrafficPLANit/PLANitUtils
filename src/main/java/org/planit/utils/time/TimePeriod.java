package org.planit.utils.time;

import org.planit.utils.id.ExternalIdable;

/**
 * Represents a time period within the day. Used to determine the duration and start time of trips for example.
 * We internally adopt seconds as the unit
 * 
 * @author markr
 *
 */
public interface TimePeriod extends ExternalIdable{
  

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

}