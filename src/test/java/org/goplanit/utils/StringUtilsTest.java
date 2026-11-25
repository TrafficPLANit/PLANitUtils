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

  /** Values of any type are turned into text in order, an enum's by their names, as whole numbers are */
  @Test
  public void toStrings() {
    assertEquals(List.of("12", "40"), StringUtils.toStrings(12, 40));
    assertEquals(List.of("DAYS", "HOURS"), StringUtils.toStrings(
        new java.util.concurrent.TimeUnit[]{java.util.concurrent.TimeUnit.DAYS, java.util.concurrent.TimeUnit.HOURS}));
    assertEquals(List.of(), StringUtils.toStrings(new Object[0]));
  }

  /** An enum constant's name read as a sentence: the first word capitalised, the rest in lower case, words apart */
  @Test
  public void toSentence() {
    assertEquals("Half days", StringUtils.toSentence(java.time.temporal.ChronoUnit.HALF_DAYS));
    assertEquals("Days", StringUtils.toSentence(java.util.concurrent.TimeUnit.DAYS));
  }

  /** Text is broken at the last space that keeps a line within the length; a longer word stays whole; breaks are kept */
  @Test
  public void wrapLineAtSpaces() {
    assertEquals(List.of("one two", "three"), StringUtils.wrapLineAtSpaces("one two three", 7));
    assertEquals(List.of("short"), StringUtils.wrapLineAtSpaces("short", 10));
    assertEquals(List.of("a", "unbreakable", "b"), StringUtils.wrapLineAtSpaces("a unbreakable b", 5));
    assertEquals(List.of("first", "second line"), StringUtils.wrapLineAtSpaces("first\nsecond line", 20));
    assertEquals(List.of(""), StringUtils.wrapLineAtSpaces("", 10));
  }

  /**
   * Text containing any of the parts is put in double quotes, with each double quote inside it doubled; text containing
   * none is left as it is
   */
  @Test
  public void quoteIfContainsAny() {
    assertEquals("plain", StringUtils.quoteIfContainsAny("plain", ","));
    assertEquals("\"a,b\"", StringUtils.quoteIfContainsAny("a,b", ","));
    assertEquals("\"say \"\"hi\"\"\"", StringUtils.quoteIfContainsAny("say \"hi\"", ",", "\""));
    assertEquals("\"two\nlines\"", StringUtils.quoteIfContainsAny("two\nlines", ";", "\n"));
    assertEquals("a,b", StringUtils.quoteIfContainsAny("a,b", ";"));
  }
}
