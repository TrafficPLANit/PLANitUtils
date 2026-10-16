package org.goplanit.utils.network.layer.modifier.consolidator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the values consolidation runs with by default
 *
 * @author markr
 */
public class IntersectionConsolidatorSettingsTest {

  /**
   * The default values are those of the spec
   */
  @Test
  public void defaultValues() {
    assertEquals(25, IntersectionConsolidatorSettings.DEFAULT_MAXIMUM_INTERNAL_LINK_LENGTH_METERS);
    assertEquals(0.05, IntersectionConsolidatorSettings.DEFAULT_MINIMUM_COVERAGE_GAIN_PER_MEMBER);
    assertEquals(0.001, IntersectionConsolidatorSettings.DEFAULT_COVERAGE_TIE_MARGIN);
    assertEquals(10, IntersectionConsolidatorSettings.DEFAULT_MAXIMUM_PARSED_INTERSECTIONS_PER_INTERSECTION_CLUSTER);
    assertEquals(100, IntersectionConsolidatorSettings.DEFAULT_INTERNAL_LINK_LENGTH_WARNING_METERS);
    assertEquals(50, IntersectionConsolidatorSettings.DEFAULT_MAXIMUM_DISTANCE_REACHED_ONLY_THROUGH_INTERSECTION_METERS);
    assertEquals(60, IntersectionConsolidatorSettings.DEFAULT_MAXIMUM_DISTANCE_SLIP_LANE_CROSSING_TO_JUNCTION_METERS);
  }
}
