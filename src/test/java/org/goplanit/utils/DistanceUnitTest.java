package org.goplanit.utils;

import org.goplanit.utils.unit.DistanceUnit;
import org.goplanit.utils.unit.Unit;
import org.goplanit.utils.unit.UnitType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Distances converted between metres and kilometres, each unit also to itself
 *
 * @author markr
 */
public class DistanceUnitTest {

  /** 2 km is 2,000 m and 2,000 m is 2 km, and either unit converted to itself keeps the value */
  @Test
  public void testDistanceUnits() {
    assertEquals(2_000, DistanceUnit.convertKmTo(UnitType.METER, 2));
    assertEquals(2, DistanceUnit.convertKmTo(UnitType.KM, 2));
    assertEquals(2, DistanceUnit.convertMeterTo(UnitType.KM, 2_000));
    assertEquals(2_000, DistanceUnit.convertMeterTo(UnitType.METER, 2_000));

    assertEquals(2_000, Unit.KM.convertTo(Unit.METER, 2));
    assertEquals(2, Unit.KM.convertTo(Unit.KM, 2));
  }
}
