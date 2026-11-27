package org.goplanit.utils;

import org.goplanit.utils.pricing.HolidayInclusion;
import org.goplanit.utils.time.TimeInWeekWithDayType;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests of the value types road pricing is asked with: the moment a mode is priced at, and whether the holiday settings
 * of two time band entries can both hold on one day
 *
 * @author markr
 */
public class PricingValueTypesTest {

  /** A moment holds the day, minute of the day and holidays it is made with; a minute outside the day is refused */
  @Test
  public void momentRefusesAMinuteOutsideTheDay() {
    var moment = TimeInWeekWithDayType.of(DayOfWeek.TUESDAY, 1439, true, false);
    assertEquals(DayOfWeek.TUESDAY, moment.getDayOfWeek());
    assertEquals(1439, moment.getMinuteOfDay());
    assertTrue(moment.isPublicHoliday());
    assertFalse(moment.isSchoolHoliday());

    assertThrows(IllegalArgumentException.class, () -> TimeInWeekWithDayType.of(DayOfWeek.TUESDAY, -1, false, false));
    assertThrows(IllegalArgumentException.class, () -> TimeInWeekWithDayType.of(DayOfWeek.TUESDAY, 1440, false, false));
  }

  /**
   * Holidays only and holidays excluded never both hold on one day, either way round; every other pair can, as an
   * entry including holidays holds on a holiday and on any other day alike ("Holiday entries apart")
   */
  @Test
  public void holidayInclusionsOverlapUnlessOnlyMeetsExcluded() {
    for (var first : HolidayInclusion.values()) {
      for (var second : HolidayInclusion.values()) {
        boolean onlyAgainstExcluded = (first == HolidayInclusion.ONLY && second == HolidayInclusion.EXCLUDED)
            || (first == HolidayInclusion.EXCLUDED && second == HolidayInclusion.ONLY);
        assertEquals(!onlyAgainstExcluded, first.overlaps(second), first + " against " + second);
      }
    }
  }

  /**
   * Any day meets holidays included, a day that is no holiday meets holidays excluded, a holiday meets holidays only
   */
  @Test
  public void dayMeetsAHolidayInclusionByWhetherItIsAHoliday() {
    assertTrue(HolidayInclusion.INCLUDED.isAgreeWith(true));
    assertTrue(HolidayInclusion.INCLUDED.isAgreeWith(false));
    assertFalse(HolidayInclusion.EXCLUDED.isAgreeWith(true));
    assertTrue(HolidayInclusion.EXCLUDED.isAgreeWith(false));
    assertTrue(HolidayInclusion.ONLY.isAgreeWith(true));
    assertFalse(HolidayInclusion.ONLY.isAgreeWith(false));
  }
}
