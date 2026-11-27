package org.goplanit.utils.pricing;

import java.util.EnumSet;
import java.util.Set;

/**
 * The kind of period a cap or a distance band counts over, each value standing for every period of its kind: one use of
 * a scheme, or calendar hours, days, weeks or months
 * <p>
 * A cap of 12.25 per use limits each trip; a cap of 60 per week limits the trips of each week; a distance band per day
 * counts the kilometres travelled on the scheme each day. The periods are calendar periods: clock hours, days, weeks
 * Monday to Sunday, months. A distance band takes every period but an hour.
 * </p>
 *
 * @author markr
 */
public enum PricingPeriod {

  /** one use of the scheme: a continuous run through its link segments */
  USE,

  /** a clock hour */
  HOUR,

  /** a day */
  DAY,

  /** a week, Monday to Sunday */
  WEEK,

  /** a calendar month */
  MONTH;

  /**
   * This period and the shorter ones each of its starts begins too, as calendar periods do: a new day begins a new
   * hour, a new week or month a new day and hour, while a new month need not begin a new week
   *
   * @return the periods started with this one
   */
  public Set<PricingPeriod> findPeriodsEqualOrSmaller() {
    switch (this) {
      case USE:
      case HOUR:
        return EnumSet.of(this);
      case DAY:
        return EnumSet.of(this, HOUR);
      default:
        return EnumSet.of(this, DAY, HOUR);
    }
  }
}
