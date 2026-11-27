package org.goplanit.utils.time;

import org.goplanit.utils.exceptions.ExceptionUtils;

import java.io.Serializable;
import java.time.DayOfWeek;

/**
 * A time in the week with the type of its day: a day of the week, a minute of the day, and whether that day is a public
 * holiday or a school holiday
 * <p>
 * What holds at a time often differs by time of day, day of the week and holidays, such as the price rule of a toll
 * road at 08:00 on a Tuesday in term time: {@code TimeInWeekWithDayType.of(DayOfWeek.TUESDAY, 480, false, false)}. It
 * says nothing of a date or year: which days are holidays is for whoever asks, such as a run of an average weekday in
 * term time.
 * </p>
 *
 * @author markr
 */
public final class TimeInWeekWithDayType implements Serializable {

  /** generated UID */
  private static final long serialVersionUID = 1L;

  /** the minutes in a day */
  private static final int MINUTES_PER_DAY = 1440;

  /** the day of the week */
  private final DayOfWeek dayOfWeek;

  /** the minute of the day, 0 to 1439 */
  private final int minuteOfDay;

  /** true when the day is a public holiday */
  private final boolean publicHoliday;

  /** true when the day is in the school holidays */
  private final boolean schoolHoliday;

  /**
   * Constructor
   *
   * @param dayOfWeek the day of the week
   * @param minuteOfDay the minute of the day, 0 to 1439
   * @param publicHoliday true when the day is a public holiday
   * @param schoolHoliday true when the day is in the school holidays
   */
  private TimeInWeekWithDayType(DayOfWeek dayOfWeek, int minuteOfDay, boolean publicHoliday, boolean schoolHoliday) {
    this.dayOfWeek = dayOfWeek;
    this.minuteOfDay = minuteOfDay;
    this.publicHoliday = publicHoliday;
    this.schoolHoliday = schoolHoliday;
  }

  /**
   * A time in the week with the type of its day
   *
   * @param dayOfWeek the day of the week
   * @param minuteOfDay the minute of the day, 0 for midnight up to 1439 for 23:59
   * @param publicHoliday true when the day is a public holiday
   * @param schoolHoliday true when the day is in the school holidays
   * @return the moment
   * @throws IllegalArgumentException when the minute lies outside the day, or the day is null
   */
  public static TimeInWeekWithDayType of(
      DayOfWeek dayOfWeek,
      int minuteOfDay,
      boolean publicHoliday,
      boolean schoolHoliday) {
    ExceptionUtils.throwIllegalArgumentIf(dayOfWeek == null, "A time in the week needs a day of the week");
    ExceptionUtils.throwIllegalArgumentIf(minuteOfDay < 0 || minuteOfDay >= MINUTES_PER_DAY,
        "Minute of the day %d refused, it must be 0 to %d", minuteOfDay, MINUTES_PER_DAY - 1);
    return new TimeInWeekWithDayType(dayOfWeek, minuteOfDay, publicHoliday, schoolHoliday);
  }

  /**
   * The day of the week
   *
   * @return the day
   */
  public DayOfWeek getDayOfWeek() {
    return dayOfWeek;
  }

  /**
   * The minute of the day
   *
   * @return 0 for midnight up to 1439 for 23:59
   */
  public int getMinuteOfDay() {
    return minuteOfDay;
  }

  /**
   * Whether the day is a public holiday
   *
   * @return true when it is
   */
  public boolean isPublicHoliday() {
    return publicHoliday;
  }

  /**
   * Whether the day is in the school holidays
   *
   * @return true when it is
   */
  public boolean isSchoolHoliday() {
    return schoolHoliday;
  }

  /**
   * The day, the time of day and the holidays, on one line, such as {@code TUESDAY 08:00}
   *
   * @return the text
   */
  @Override
  public String toString() {
    return String.format("%s %02d:%02d%s%s", dayOfWeek, minuteOfDay / 60, minuteOfDay % 60,
        publicHoliday ? ", public holiday" : "", schoolHoliday ? ", school holiday" : "");
  }
}
