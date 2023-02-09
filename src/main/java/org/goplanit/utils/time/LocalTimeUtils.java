package org.goplanit.utils.time;

import java.time.*;
import java.util.GregorianCalendar;

/**
 * Local time utilities
 */
public class LocalTimeUtils {

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


}
