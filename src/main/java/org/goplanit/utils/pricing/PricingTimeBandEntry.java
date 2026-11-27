package org.goplanit.utils.pricing;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import org.goplanit.utils.time.TimeInWeekWithDayType;

/**
 * When a time band applies: on some days, on holidays or not, in one interval of the day of at most a day
 * <p>
 * For example weekdays from 06:30 to 09:30: days Monday to Friday, starting at minute 390 of the day and lasting 180
 * minutes. An entry may run past midnight, its days being the days it starts. Its time is in minutes, as a
 * {@link TimeInWeekWithDayType}'s; the unit it is written in, such as hours for 07:00 to 10:00, stays with it only so a
 * file read and written back is the same, see {@link #getPersistTemporalUnit()}.
 * </p>
 *
 * @author markr
 */
public interface PricingTimeBandEntry extends Serializable {

  /**
   * The days of the week the entry starts on
   *
   * @return unmodifiable set of the days, empty when not given, meaning every day
   */
  public abstract Set<DayOfWeek> getDays();

  /**
   * Whether public holidays count
   *
   * @return the setting, {@link HolidayInclusion#INCLUDED} when not given
   */
  public abstract HolidayInclusion getPublicHolidays();

  /**
   * Whether school holidays count
   *
   * @return the setting, {@link HolidayInclusion#INCLUDED} when not given
   */
  public abstract HolidayInclusion getSchoolHolidays();

  /**
   * The minute of the day the interval starts at, as a {@link TimeInWeekWithDayType}'s minute of the day
   *
   * @return 0 for midnight up to 1439 for 23:59
   */
  public abstract int getStartMinuteOfDay();

  /**
   * How long the interval lasts, from its start, past midnight when it runs into the next day
   *
   * @return the duration in minutes, at least 1 and at most one day
   */
  public abstract int getDurationMinutes();

  /**
   * The unit the entry's start and duration are written in, so a file read and written back is the same; it is not
   * part of when the entry applies
   *
   * @return minutes, hours or days, dividing both the start and the duration
   */
  public abstract ChronoUnit getPersistTemporalUnit();

  /**
   * Set the unit the entry's start and duration are written in
   *
   * @param persistTemporalUnit minutes, hours or days
   * @throws IllegalArgumentException when it is none of those, or does not divide the start and the duration
   */
  public abstract void setPersistTemporalUnit(ChronoUnit persistTemporalUnit);

  /**
   * Whether the entry covers a moment: the moment's day is one of its days and the moment lies in its interval, or in
   * its part past midnight on the next day, and its holiday settings hold on that day
   *
   * @param moment to check
   * @return true when it covers the moment
   */
  public abstract boolean covers(TimeInWeekWithDayType moment);

  /**
   * Whether this entry and another overlap: they share a day, their intervals in minutes intersect, and their holiday
   * settings can both hold on one day, see {@link HolidayInclusion#overlaps(HolidayInclusion)}
   *
   * @param other entry to check against
   * @return true when they overlap
   */
  public abstract boolean overlaps(PricingTimeBandEntry other);
}
