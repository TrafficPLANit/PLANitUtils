package org.goplanit.utils;

import org.goplanit.utils.settings.UserOverride;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests of {@link UserOverride}: a value the user gives in place of what the input would give, with or without the
 * reason for it
 *
 * @author markr
 */
public class UserOverrideTest {

  /** A value given without a reason has none, and gives null for it */
  @Test
  public void testWithoutReason() {
    var override = UserOverride.of(2);
    assertEquals(2, override.getValue());
    assertFalse(override.hasReason());
    assertNull(override.getReason());
  }

  /** A value given with a reason gives both */
  @Test
  public void testWithReason() {
    var override = UserOverride.of(2, "lane check p17, answered 2026-11-05");
    assertEquals(2, override.getValue());
    assertTrue(override.hasReason());
    assertEquals("lane check p17, answered 2026-11-05", override.getReason());
  }

  /** For showing to a user, the reason or, without one, the constant saying it is unknown */
  @Test
  public void testReasonOrUnknown() {
    assertEquals("checked on imagery", UserOverride.of(2, "checked on imagery").getReasonOrUnknown());
    assertEquals(UserOverride.REASON_UNKNOWN, UserOverride.of(2).getReasonOrUnknown());
    assertEquals(UserOverride.REASON_UNKNOWN, UserOverride.of(2, " ").getReasonOrUnknown());
  }

  /** The value is kept as given: the same object, not a copy */
  @Test
  public void testValueKeptAsGiven() {
    var lanes = List.of(2, 3);
    assertSame(lanes, UserOverride.of(lanes, "checked on imagery").getValue());
  }

  /** A blank reason counts as none, as an empty reason in a file says nothing */
  @Test
  public void testBlankReasonIsNone() {
    assertFalse(UserOverride.of(2, " ").hasReason());
    assertNull(UserOverride.of(2, "").getReason());
  }

  /** Overrides with the same value and reason are equal, so settings holding them can be compared */
  @Test
  public void testEquality() {
    assertEquals(UserOverride.of(2, "checked"), UserOverride.of(2, "checked"));
    assertEquals(UserOverride.of(2, "checked").hashCode(), UserOverride.of(2, "checked").hashCode());
    assertEquals(UserOverride.of(2), UserOverride.of(2, ""));
    assertNotEquals(UserOverride.of(2, "checked"), UserOverride.of(2, "other"));
    assertNotEquals(UserOverride.of(2), UserOverride.of(3));
  }

  /** An override needs a value: without one it would change nothing */
  @Test
  public void testNoValueRefused() {
    assertThrows(IllegalArgumentException.class, () -> UserOverride.of(null));
    assertThrows(IllegalArgumentException.class, () -> UserOverride.of(null, "checked"));
  }
}
