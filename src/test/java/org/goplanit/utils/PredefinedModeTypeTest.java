package org.goplanit.utils;

import org.goplanit.utils.mode.DriverType;
import org.goplanit.utils.mode.PredefinedModeType;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests of {@link PredefinedModeType}
 */
public class PredefinedModeTypeTest {

  /**
   * Each predefined mode type says who drives it: one of the people travelling in it for the cars, the motorbike, the
   * bicycle and the goods vehicles, someone else for a taxi, ride share or public transport, nobody when walking. The
   * custom type does not know
   */
  @Test
  public void eachModeTypeSaysWhoDrivesIt() {
    var expected = new EnumMap<PredefinedModeType, DriverType>(PredefinedModeType.class);
    for (var modeType : new PredefinedModeType[]{PredefinedModeType.CAR, PredefinedModeType.CAR_SHARE,
        PredefinedModeType.CAR_HIGH_OCCUPANCY, PredefinedModeType.MOTOR_BIKE, PredefinedModeType.BICYCLE,
        PredefinedModeType.GOODS_VEHICLE, PredefinedModeType.HEAVY_GOODS_VEHICLE,
        PredefinedModeType.LARGE_HEAVY_GOODS_VEHICLE}) {
      expected.put(modeType, DriverType.TRAVELLER);
    }
    for (var modeType : new PredefinedModeType[]{PredefinedModeType.TAXI, PredefinedModeType.RIDE_SHARE,
        PredefinedModeType.BUS, PredefinedModeType.TRAM, PredefinedModeType.LIGHTRAIL, PredefinedModeType.SUBWAY,
        PredefinedModeType.TRAIN, PredefinedModeType.FERRY}) {
      expected.put(modeType, DriverType.OPERATOR);
    }
    expected.put(PredefinedModeType.PEDESTRIAN, DriverType.NONE);

    for (var modeType : PredefinedModeType.getPredefinedModeTypes()) {
      assertEquals(expected.get(modeType), modeType.getDriverType(), modeType.name());
    }
  }
}
