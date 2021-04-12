package org.planit.utils.time;

import java.util.Comparator;
import java.util.logging.Logger;

import org.planit.utils.exceptions.PlanItException;

/**
 * some utilities for the TimePeriod interface
 * 
 * @author markr
 *
 */
public class TimePeriodUtils {
  
  /** the logger */
  private static final Logger LOGGER = Logger.getLogger(TimePeriodUtils.class.getCanonicalName());

  /**
   * custom comparator not by id but based on the start time and when equal duration
   * 
   * @return comparator by start time
   */
  public static Comparator<TimePeriod> comparatorByStartTime() {
    Comparator<TimePeriod> sortOnStartTime = new Comparator<TimePeriod>() {
      @Override
      public int compare(TimePeriod o1, TimePeriod o2) {
        long startTimeDiff = o1.getStartTimeSeconds() - o2.getStartTimeSeconds();
        if (startTimeDiff != 0) {
          return (int) startTimeDiff;
        } else {
          return (int) (o1.getDurationSeconds() - o2.getDurationSeconds());
        }
      }
    };
    return sortOnStartTime;
  }
  
  /**
   * Convert duration to seconds given start time using the 24-hour clock
   * 
   * @param fourDigitHour start time in 24-hour clock format (four digits exactly)
   * @return duration in seconds
   * @throws PlanItException thrown if the input time is not in the correct format
   */
  public static long convertHoursToSeconds(String fourDigitHour) throws PlanItException {
    long startTime;
    long startTimeHrs;
    long startTimeMins;
    PlanItException.throwIf(fourDigitHour.length() != 4, "Start time must contain exactly four digits");

    try {
      startTime = Integer.parseInt(fourDigitHour);
    } catch (NumberFormatException e) {
      LOGGER.severe(e.getMessage());
      throw new PlanItException("Start time must contain exactly four digits", e);
    }
    PlanItException.throwIf(startTime < 0, "Start time cannot be negative");
    PlanItException.throwIf(startTime > 2400, "Start time cannot be later than 2400");

    startTimeHrs = startTime / 100;
    startTimeMins = startTime % 100;

    PlanItException.throwIf(startTimeMins > 59, "Last two digits of start time cannot exceed 59");

    return (startTimeHrs * 3600) + (startTimeMins * 60);
  }

  /**
   * Convert hours to (whole) seconds
   * 
   * @param hoursFromMidnight the hours from midnight
   * @return secondsFromMidnight the seconds from midnight
   */
  public static long convertHoursToSeconds(double hoursFromMidnight) {
    return (Math.round(hoursFromMidnight * 3600));
  }

  /**
   * Convert seconds to 24h format from midnight to seconds
   * 
   * @param secondsFromMidnight seconds from midnight
   * @return hours from midnight
   */
  public static float convertSecondsToHours(int secondsFromMidnight) {
    return ((float) secondsFromMidnight) / 3600;
  }  

}
