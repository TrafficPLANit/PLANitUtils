package org.goplanit.utils;

import org.goplanit.utils.geo.PlanitCrsUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PlanitCrsUtilsTest {

  @Test
  public void hasFirstTwoPlanarAxesWithLengthCompatibleUnitsTest() {
    var geographic = PlanitCrsUtils.createCoordinateReferenceSystem("EPSG:4326");
    var projected = PlanitCrsUtils.createCoordinateReferenceSystem("EPSG:3857");

    assertFalse(PlanitCrsUtils.hasFirstTwoPlanarAxesWithLengthCompatibleUnits(geographic));
    assertTrue(PlanitCrsUtils.hasFirstTwoPlanarAxesWithLengthCompatibleUnits(projected));
  }
}
