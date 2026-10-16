package org.goplanit.utils;

import org.goplanit.utils.misc.StringUtils;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests of {@link StringUtils}
 */
public class StringUtilsTest {

  /** Elements are turned into text, sorted as text and joined with commas */
  @Test
  public void toSortedCommaSeparatedString() {
    assertEquals("", StringUtils.toSortedCommaSeparatedString(List.<Integer>of(), Object::toString));
    assertEquals("7", StringUtils.toSortedCommaSeparatedString(List.of(7), Object::toString));
    /* sorted as text, so 10 comes before 2 and 9 */
    assertEquals("10,2,9", StringUtils.toSortedCommaSeparatedString(List.of(9, 10, 2), Object::toString));
  }
}
