package org.goplanit.utils.time;

import org.goplanit.utils.misc.StringUtils;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Simple immutable extension to Local Time to allow for additional time beyond midnight within a single time entity, e.g. 25:01:01 (HH:mm:ss).
 * <p>
 *   This class does not support nano seconds and only allows for time to be expanded up to hour 47, i.e., not beyond another midnight.
 *   Also we only support strings to be parsed and output of the form HH:mm:ss, to keep things simple
 * </p>
 */
public class ExtendedLocalTime implements Comparable<ExtendedLocalTime>{

  /** Logger to use */
  private static final Logger LOGGER = Logger.getLogger(ExtendedLocalTime.class.getCanonicalName());

  private final LocalTime beforeMidnight;

  private final LocalTime beyondMidnight;

  /**
   * Constructor
   *
   * @param beforeMidnight to use
   * @param beyondMidnight to use
   */
  private ExtendedLocalTime(LocalTime beforeMidnight, LocalTime beyondMidnight){
    this.beforeMidnight = beforeMidnight;
    this.beyondMidnight = beyondMidnight;
  }

  /**
   * Verify if nanos are valid, i.e., within 48h and not negative
   *
   * @param nanos to verify
   * @return true when valid, false otherwise
   */
  public static boolean isNanosValid(long nanos){
    return nanos <= (LocalTime.MAX.toNanoOfDay() * 2)+1 && nanos >= 0;
  }

  /**
   * Create extended local time from regular local time
   *
   * @param localtime to use as base for new extende local time
   * @return created extended local time
   */
  public static ExtendedLocalTime of(LocalTime localtime){
    return new ExtendedLocalTime(localtime, null);
  }

  /**
   * Create from nanos, logs warning and returns null if not valid
   *
   * @param nanos to use
   * @return created extended local time
   */
  public static ExtendedLocalTime of(long nanos){
    if(!isNanosValid(nanos)){
      LOGGER.warning("Cannot create extended local time beyond 48h or with negative value");
      return null;
    }

    if(nanos > LocalTime.MAX.toNanoOfDay()){
      return ofBeyondMidnight(LocalTime.ofNanoOfDay(nanos - (LocalTime.MAX.toNanoOfDay()+1)));
    }

    return of(LocalTime.ofNanoOfDay(nanos));
  }

  /**
   * Create extended local time from regular local time that is assumed to be beyond midnight
   *
   * @param localtime to use as base for new extende local time
   * @return created extended local time
   */
  public static ExtendedLocalTime ofBeyondMidnight(LocalTime localtime){
    return new ExtendedLocalTime(LocalTime.MAX, localtime);
  }

  /**
   * Factory method taking HH:mm:ss where hours are allowed to exceed 24 to reflect a time running passed the day (but not beyond the next day), e.g. it should
   * be less than 48
   *
   * @param hh_mm_ss string to extract time from
   * @return created extended local time
   */
  public static ExtendedLocalTime of(String hh_mm_ss){
    var hhmmssSplit = StringUtils.splitByAnythingExceptAlphaNumeric(hh_mm_ss);
    if(hhmmssSplit == null || hhmmssSplit.length != 3){
      LOGGER.severe(String.format("Invalid extended local time string format, expected HH:MM:SS, found %s",hh_mm_ss));
    }

    var hours = Integer.parseInt(hhmmssSplit[0]);
    if(hours > 47){
      LOGGER.severe(String.format("Invalid extended local time, expected hours should be less than 48, found %s",hours));
      return null;
    }

    var min = Integer.parseInt(hhmmssSplit[1]);
    if(min > 59){
      LOGGER.severe(String.format("Invalid extended local time, expected minutes should be less than 59, found %s",min));
      return null;
    }

    var sec = Integer.parseInt(hhmmssSplit[2]);
    if(sec > 59){
      LOGGER.severe(String.format("Invalid extended local time, expected seconds should be less than 59, found %s",sec));
      return null;
    }

    LocalTime beforeMidnight = null;
    LocalTime beyondMidnight = null;
    if(hours > 23){
      beforeMidnight = LocalTime.MAX;

      // add one nanosecond to increment to the next second, which is  added to the after midnight part for good form (not used),
      // since we do not support nano seconds in the input, we implicitly assume they are set at 0 for the after midnight part
      // ensuring no overflow can occur onto the seconds or minutes, etc.
      int hoursBeyondMidnight = (int) (hours - TimeUnit.DAYS.toHours(1));
      beyondMidnight = LocalTime.of(hoursBeyondMidnight, min, sec);
    }else{
      beforeMidnight = LocalTime.of(hours, min, sec);
    }

    return new ExtendedLocalTime(beforeMidnight, beyondMidnight);
  }

  /**
   * Check if extended time exceeds midnight, i.e. hour is smaller than 47 and greater than 23
   *
   * @return true when extended beyond midnight, false otherwise
   */
  public boolean exceedsSingleDay() {
    return beyondMidnight != null;
  }

  /**
   * Extract the component before midnight as a new LocalTime instance
   * @return created local time based on time before midnight
   */
  public LocalTime asLocalTimeBeforeMidnight() {
    return LocalTime.from(beforeMidnight);
  }

  /**
   * Extract the component after midnight as a new LocalTime instance
   * @return created local time based on time after midnight, null if time does not exceed single day
   */
  public LocalTime asLocalTimeAfterMidnight() {
    if(!exceedsSingleDay()){
      return null;
    }
    return LocalTime.from(this.beyondMidnight);
  }

  /**
   * verify if other time occurs before this time
   *
   * @param other to compare to
   * @return true when before, false otherwise
   */
  public boolean isBefore(ExtendedLocalTime other){
    return compareTo(other) < 0;
  }

  /**
   * verify if other time occurs before this time
   *
   * @param other to compare to
   * @return true when before, false otherwise
   */
  public boolean isAfter(ExtendedLocalTime other){
    return compareTo(other) > 0;
  }

  /**
   * subtract other from this and return newly created extended time reflecting the new time. One cannot subtract to invalid time, e.g.
   * negative time is not allowed, so this time must be larger than other
   *
   * @param other to subtract
   * @return newly created time, null if invalid
   */
  public ExtendedLocalTime minus(final ExtendedLocalTime other){
    long totalNanos = this.toNanoOfExtendedDay() - other.toNanoOfExtendedDay();
    if(isNanosValid(totalNanos)){
      return of(totalNanos);
    }else{
      LOGGER.severe("Subtracting two local extended times would result in negative time, not allowed");
      return null;
    }
  }

  /**
   * add other to this and return newly created extended time reflecting the new time. One cannot add to invalid time, e.g.,
   * negative time is not allowed, so this time must be larger than other
   *
   * @param other to subtract
   * @return newly created time, null if invalid
   */
  public ExtendedLocalTime plus(final ExtendedLocalTime other){
    long totalNanos = this.toNanoOfExtendedDay() + other.toNanoOfExtendedDay();
    if(isNanosValid(totalNanos)){
      return of(totalNanos);
    }else{
      LOGGER.severe("Adding two local extended times would exceed 48 hours, not allowed");
      return null;
    }
  }

  /** get nanos of this extended time
   *
   * @return nanos
   */
  public long toNanoOfExtendedDay(){
    return this.beforeMidnight.toNanoOfDay() + (exceedsSingleDay() ? this.beyondMidnight.toNanoOfDay()+1 : 0);
  }

  /**
   * As string
   *
   * @return hh:mm:ss format
   */
  @Override
  public String toString(){
    if(!exceedsSingleDay()){
      return beforeMidnight.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    String extendedHours = StringUtils.zeroPaddedStringOf((int) (beyondMidnight.getHour() + TimeUnit.DAYS.toHours(1)), 2);
    return String.join(
        ":",
        extendedHours, /* HH */
        StringUtils.zeroPaddedStringOf(beyondMidnight.getMinute(),2), /* mm */
        StringUtils.zeroPaddedStringOf(beyondMidnight.getSecond(),2)); /* ss */
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object obj) {
    if(this == obj){
      return true;
    }

    if(!(obj instanceof ExtendedLocalTime)){
      return false;
    }

    var other = (ExtendedLocalTime) obj;
    return
        /* before midnight is the same and... */
        this.beforeMidnight.equals(other.beforeMidnight) &&
            /* ... either both after midnight is also the same, or they both do not have an after midnight part */
            ( (this.exceedsSingleDay() && this.beyondMidnight.equals(other.beyondMidnight)) || !other.exceedsSingleDay());
  }

  /**
   *  When before 0 value -1, when after value 1, otherwise 0
   */
  @Override
  public int compareTo(ExtendedLocalTime o) {
    if( this.equals(o)) {
      return 0;
    }

    long diff = toNanoOfExtendedDay() - o.toNanoOfExtendedDay();
    return diff<0 ? -1 : 1;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return Objects.hash(this.beforeMidnight, this.beyondMidnight);
  }

}
