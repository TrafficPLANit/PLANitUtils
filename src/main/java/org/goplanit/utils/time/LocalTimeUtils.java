package org.goplanit.utils.time;

import java.time.*;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

/**
 * Local time utilities
 */
public class LocalTimeUtils {

  // 86400 = total seconds i the day
  public static final long SECONDS_IN_DAY = TimeUnit.DAYS.toSeconds(1);

  /**
   * Create gregorian calendar from time only, supplementing date to now and default time zone
   *
   * @param localTime to convert
   * @return gregorian calendar
   */
  public static GregorianCalendar toGregorianCalendar(LocalTime localTime){
    return GregorianCalendar.from(toZonedDateTime(localTime));
  }

  /**
   * Create LocalDateTime from time only, supplementing date to now()
   *
   * @param localTime to convert
   * @return gregorian calendar
   */
  public static LocalDateTime toLocalDateTime(LocalTime localTime){
    return localTime.atDate(LocalDate.now());
  }

  /**
   * Create ZonedDateTime from time only, supplementing date to now() and zone to default time zone
   *
   * @param localTime to convert
   * @return zoned date time
   */
  public static ZonedDateTime toZonedDateTime(LocalTime localTime){
    return toLocalDateTime(localTime).atZone(ZoneId.systemDefault());
  }

  /**
   * Verify if a given start-end time in LocalTime is valid given we are working with a time period that may wrap around
   * a day, e.g., the period does not run from midnight-midnight but we have a period from let's say 3:00AM-2:59AM the
   * next day. In such case a start time of 4AM and an end time of 2AM is valid, despite the within day time being not
   * in chronological order. If the period time does not wrap aaround, the normal startTime<end time rules apply
   *
   * @param timePeriodStartTime to use as reference
   * @param timePeriodEndTime to use as reference
   * @param startTime start time of to check combination start-end time
   * @param endTime end time of to check combination start-end time
   * @return true when valid, false otherwise
   */
  public static boolean isValidOrderForWrapAroundDayAnchors(
      LocalTime timePeriodStartTime, LocalTime timePeriodEndTime, LocalTime startTime, LocalTime endTime){

    boolean withinPeriodIndividually = isValidForWrapAroundDayAnchors(timePeriodStartTime, timePeriodEndTime, startTime) &&
    isValidForWrapAroundDayAnchors(timePeriodStartTime, timePeriodEndTime, endTime);
    if(!withinPeriodIndividually){
      return withinPeriodIndividually;
    }

    if(timePeriodStartTime.isBefore(timePeriodEndTime)){
      // no wrap around --> check order
      return !endTime.isBefore(startTime);
    }

    // wrap around case 1--> |---e--->*---*------s------>|
    boolean case1 = !startTime.isBefore(timePeriodEndTime) && !endTime.isAfter(timePeriodEndTime);
    // wrap around case 2--> |------>*---*------s----e-->|
    boolean case2 = !startTime.isBefore(timePeriodStartTime) && !endTime.isBefore(startTime);
    // wrap around case 3--> |--s--e-->*---*------------>|
    boolean case3 =
        !startTime.isAfter(timePeriodEndTime) && !endTime.isBefore(startTime) && !endTime.isAfter(timePeriodEndTime);
    return case1 || case2 || case3;

  }

  /**
   * Verify if a given time in LocalTime is valid given we are working with a time period that may wrap around
   * a day, e.g., the period does not run from midnight-midnight but we have a period from let's say 7:00AM-2:00AM the
   * next day. In such case a time of 1AM is valid, but 5 AM is not.
   *
   * @param timePeriodStartTime to use as reference
   * @param timePeriodEndTime to use as reference
   * @param timeToCheck time to check
   * @return true when valid, false otherwise
   */
  public static boolean isValidForWrapAroundDayAnchors(
      LocalTime timePeriodStartTime, LocalTime timePeriodEndTime, LocalTime timeToCheck){
    boolean refBeforeOrAtEndTime = !timeToCheck.isAfter(timePeriodEndTime);
    boolean refAfterOrAtStartTime = !timeToCheck.isBefore(timePeriodStartTime);
    if(timePeriodStartTime.isBefore(timePeriodEndTime)){
      // no wrap around --> normal check
      return refAfterOrAtStartTime && refBeforeOrAtEndTime;
    }else{
      // wrap around --> before end time (so between 0-end), or after start (between start-0)
      return refAfterOrAtStartTime || refBeforeOrAtEndTime;
    }

  }

  /**
   * Formats raw total seconds into an unbounded HH:mm:ss string, allowing hours to safely exceed
   * 24 (e.g., 97198 -> "26:59:58").
   *
   * @param totalSeconds raw duration or wall-clock offset in seconds
   * @return a zero-padded formatted time string
   */
  public static String formatHhMmSs(long totalSeconds) {
    if (totalSeconds < 0) {
      throw new IllegalArgumentException("Time in seconds cannot be negative: " + totalSeconds);
    }
    long hours = totalSeconds / 3600;
    long minutes = (totalSeconds % 3600) / 60;
    long seconds = totalSeconds % 60;
    return String.format("%02d:%02d:%02d", hours, minutes, seconds);
  }


}
