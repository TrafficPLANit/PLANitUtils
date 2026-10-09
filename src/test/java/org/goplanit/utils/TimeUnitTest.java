package org.goplanit.utils;

import org.goplanit.utils.misc.ComparablePair;
import org.goplanit.utils.misc.Pair;
import org.goplanit.utils.unit.TimeUnit;
import org.goplanit.utils.unit.Unit;
import org.goplanit.utils.unit.UnitType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TimeUnitTest {

  @Test
  public void timeUnitTest(){

    assertEquals(1, TimeUnit.convertMilliSecondTo(UnitType.HOUR, 3_600_000));
    assertEquals(1, TimeUnit.convertSecondTo(UnitType.HOUR, 3_600));
    assertEquals(1, TimeUnit.convertMinuteTo(UnitType.HOUR, 60));
    assertEquals(1, TimeUnit.convertHourTo(UnitType.HOUR, 1));

    var timeUnit = Unit.of(UnitType.HOUR);
    double tu_ms = timeUnit.convertTo(Unit.MILLISECOND,2);
    assertEquals(7_200_000, tu_ms);
  }

}
