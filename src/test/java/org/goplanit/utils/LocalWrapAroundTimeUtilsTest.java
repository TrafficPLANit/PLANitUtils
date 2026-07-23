package org.goplanit.utils;

import org.goplanit.utils.time.LocalTimeUtils;
import org.junit.jupiter.api.Test;
import java.time.LocalTime;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class LocalWrapAroundTimeUtilsTest {

  // =========================================================================
  // 1. STANDARD DAY SHIFT CASES (NO WRAP-AROUND)
  // =========================================================================

  @Test
  public void testStandardDay_ValidChronologicalTrip() {
    var refStart = LocalTime.of(8, 0);
    var refEnd = LocalTime.of(17, 0);
    var checkStart = LocalTime.of(9, 0);
    var checkEnd = LocalTime.of(12, 0);

    assertTrue(LocalTimeUtils.isValidOrderForWrapAroundDayAnchors(refStart, refEnd, checkStart, checkEnd),
        "Trip from 9AM to 12PM should be valid within a standard 8AM to 5PM shift");
  }

  @Test
  public void testStandardDay_InvalidOrder() {
    var refStart = LocalTime.of(8, 0);
    var refEnd = LocalTime.of(17, 0);
    var checkStart = LocalTime.of(14, 0);
    var checkEnd = LocalTime.of(10, 0);

    assertFalse(LocalTimeUtils.isValidOrderForWrapAroundDayAnchors(refStart, refEnd, checkStart, checkEnd),
        "Trip should fail because the end time is chronologically before the start time");
  }

  @Test
  public void testStandardDay_EntirelyOutsideShift() {
    var refStart = LocalTime.of(8, 0);
    var refEnd = LocalTime.of(17, 0);
    var checkStart = LocalTime.of(18, 0);
    var checkEnd = LocalTime.of(20, 0);

    assertFalse(LocalTimeUtils.isValidOrderForWrapAroundDayAnchors(refStart, refEnd, checkStart, checkEnd),
        "Trip from 6PM to 8PM should fail because it falls entirely outside the 8AM to 5PM window");
  }

  // =========================================================================
  // 2. OVERNIGHT / WRAP-AROUND SHIFT CASES
  // =========================================================================

  @Test
  public void testWrapShift_ValidCrossMidnightTrip() {
    // Javadoc scenario: 3:00 AM to 2:59 AM the next day
    var refStart = LocalTime.of(3, 0);
    var refEnd = LocalTime.of(2, 59);
    var checkStart = LocalTime.of(4, 0);
    var checkEnd = LocalTime.of(2, 0);

    assertTrue(LocalTimeUtils.isValidOrderForWrapAroundDayAnchors(refStart, refEnd, checkStart, checkEnd),
        "Trip from 4AM to 2AM next day should be valid inside a 3AM to 2:59AM shift");
  }

  @Test
  public void testWrapShift_ValidBeforeMidnightOnly() {
    var refStart = LocalTime.of(22, 0); // 10 PM
    var refEnd = LocalTime.of(6, 0);   // 6 AM
    var checkStart = LocalTime.of(22, 30);
    var checkEnd = LocalTime.of(23, 30);

    assertTrue(LocalTimeUtils.isValidOrderForWrapAroundDayAnchors(refStart, refEnd, checkStart, checkEnd),
        "Trip sitting entirely before midnight (10:30PM to 11:30PM) should be valid");
  }

  @Test
  public void testWrapShift_ValidAfterMidnightOnly() {
    var refStart = LocalTime.of(22, 0); // 10 PM
    var refEnd = LocalTime.of(6, 0);   // 6 AM
    var checkStart = LocalTime.of(1, 0);
    var checkEnd = LocalTime.of(3, 0);

    assertTrue(LocalTimeUtils.isValidOrderForWrapAroundDayAnchors(refStart, refEnd, checkStart, checkEnd),
        "Trip sitting entirely after midnight (1AM to 3AM) should be valid");
  }

  @Test
  public void testWrapShift_InvalidChronologicalFlow() {
    var refStart = LocalTime.of(2, 0);  // 2 AM
    var refEnd = LocalTime.of(1, 0);   // 1 AM next day
    var checkStart = LocalTime.of(0, 0); // Midnight
    var checkEnd = LocalTime.of(3, 0);  // 3 AM (Backward path relative to shift start)

    assertFalse(LocalTimeUtils.isValidOrderForWrapAroundDayAnchors(refStart, refEnd, checkStart, checkEnd),
        "Trip from midnight to 3AM is travelling backwards on this timeline and must be invalid");
  }

  @Test
  public void testWrapShift_CrossesDeadZone() {
    var refStart = LocalTime.of(22, 0); // 10 PM
    var refEnd = LocalTime.of(6, 0);   // 6 AM
    var checkStart = LocalTime.of(5, 0);
    var checkEnd = LocalTime.of(23, 0); // Crosses daylight hours (dead zone)

    assertFalse(LocalTimeUtils.isValidOrderForWrapAroundDayAnchors(refStart, refEnd, checkStart, checkEnd),
        "Trip cannot traverse the dead-zone timeframe outside of the shift context");
  }

  // =========================================================================
  // 3. BOUNDARY & IDENTITY TESTS
  // =========================================================================

  @Test
  public void testBoundary_TripStartsExactlyOnOpening() {
    var refStart = LocalTime.of(22, 0);
    var refEnd = LocalTime.of(6, 0);
    var checkStart = LocalTime.of(22, 0);
    var checkEnd = LocalTime.of(1, 0);

    assertTrue(LocalTimeUtils.isValidOrderForWrapAroundDayAnchors(refStart, refEnd, checkStart, checkEnd),
        "Trip starting exactly at the shift start boundary (10 PM) should be inclusive and valid");
  }

  @Test
  public void testBoundary_TripEndsExactlyOnClosing() {
    var refStart = LocalTime.of(22, 0);
    var refEnd = LocalTime.of(6, 0);
    var checkStart = LocalTime.of(1, 0);
    var checkEnd = LocalTime.of(6, 0);

    assertTrue(LocalTimeUtils.isValidOrderForWrapAroundDayAnchors(refStart, refEnd, checkStart, checkEnd),
        "Trip ending exactly at the shift end boundary (6 AM) should be inclusive and valid");
  }

  @Test
  public void testIdentity_ZeroDurationStandardDay() {
    var refStart = LocalTime.of(8, 0);
    var refEnd = LocalTime.of(17, 0);
    var checkStart = LocalTime.of(12, 0);
    var checkEnd = LocalTime.of(12, 0);

    assertFalse(LocalTimeUtils.isValidOrderForWrapAroundDayAnchors(refStart, refEnd, checkStart, checkEnd),
        "Zero duration trip (12PM to 12PM) should fail if your rules forbid it");
  }

  @Test
  public void testIdentity_ZeroDurationWrapShift() {
    var refStart = LocalTime.of(22, 0);
    var refEnd = LocalTime.of(6, 0);
    var checkStart = LocalTime.of(23, 0);
    var checkEnd = LocalTime.of(23, 0);

    assertFalse(LocalTimeUtils.isValidOrderForWrapAroundDayAnchors(refStart, refEnd, checkStart, checkEnd),
        "Zero duration wrap shift trip (11PM to 11PM) should fail if your rules forbid it");
  }
}
