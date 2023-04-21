package org.goplanit.utils;

import org.goplanit.utils.time.ExtendedLocalTime;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ExtendedLocalTimeTest {

  @Test
  public void extendedLocalTimeTest(){
    var beforeMidnight = ExtendedLocalTime.of("23:59:59");
    assertFalse(beforeMidnight.exceedsSingleDay());
    assertEquals(beforeMidnight.toString(), "23:59:59");
    assertNull(beforeMidnight.asLocalTimeAfterMidnight());

    var afterMidnight = ExtendedLocalTime.of("24:00:00");
    assertTrue(afterMidnight.exceedsSingleDay());
    assertEquals(afterMidnight.toString(), "24:00:00");
    assertEquals(afterMidnight.asLocalTimeAfterMidnight().format(DateTimeFormatter.ISO_LOCAL_TIME), "00:00:00");

    var newTime = afterMidnight.minus(beforeMidnight);
    assertNotNull(newTime);
    assertEquals(newTime, ExtendedLocalTime.of(LocalTime.of(0,0,1)));
    newTime = beforeMidnight.minus(afterMidnight);
    assertNull(newTime);

    newTime = beforeMidnight.plus(beforeMidnight);
    assertNotNull(newTime);
    assertEquals(newTime, ExtendedLocalTime.of("47:59:58"));
    assertEquals(newTime.asLocalTimeAfterMidnight().format(DateTimeFormatter.ISO_LOCAL_TIME), "23:59:58");

    afterMidnight = ExtendedLocalTime.of("33:33:33");
    assertTrue(afterMidnight.exceedsSingleDay());
    assertEquals(afterMidnight.toString(), "33:33:33");

    var afterNextMidnight = ExtendedLocalTime.of("48:00:00");
    assertNull(afterNextMidnight);

    assertTrue(beforeMidnight.isBefore(afterMidnight));
    assertFalse(afterMidnight.isBefore(beforeMidnight));
    assertTrue(afterMidnight.isAfter(beforeMidnight));
    assertFalse(beforeMidnight.isAfter(afterMidnight));

    assertEquals(beforeMidnight.compareTo(afterMidnight), -1);
    assertEquals(beforeMidnight.compareTo(beforeMidnight), 0);
    assertEquals(beforeMidnight.compareTo(ExtendedLocalTime.of("23:59:59")), 0);
    assertTrue(beforeMidnight.equals(ExtendedLocalTime.of("23:59:59")));

  }
}
