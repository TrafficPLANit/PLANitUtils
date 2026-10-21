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
    assertEquals(40, IntersectionConsolidatorSettings.DEFAULT_MAXIMUM_JOINING_REACH_METERS);
    assertEquals(0.05, IntersectionConsolidatorSettings.DEFAULT_MINIMUM_COVERAGE_GAIN_PER_MEMBER);
    assertEquals(0.05, IntersectionConsolidatorSettings.DEFAULT_COVERAGE_TIE_MARGIN);
    assertEquals(10, IntersectionConsolidatorSettings.DEFAULT_MAXIMUM_PARSED_INTERSECTIONS_TO_TRY_EVERY_SUBDIVISION);
    assertEquals(3, IntersectionConsolidatorSettings.DEFAULT_SUBDIVISIONS_KEPT_PER_SEARCH_STEP);
    assertEquals(30, IntersectionConsolidatorSettings.DEFAULT_PULL_IN_MARGIN_BEYOND_RADIUS_METERS);
    assertEquals(80, IntersectionConsolidatorSettings.DEFAULT_MAXIMUM_PULL_IN_CONNECTION_LENGTH_METERS);
    assertEquals(100, IntersectionConsolidatorSettings.DEFAULT_INTERNAL_LINK_LENGTH_WARNING_METERS);
    assertEquals(60, IntersectionConsolidatorSettings.DEFAULT_MAXIMUM_DISTANCE_REACHED_ONLY_THROUGH_INTERSECTION_METERS);
    assertEquals(2,
        IntersectionConsolidatorSettings.DEFAULT_MAXIMUM_DISTANCE_REACHED_ONLY_THROUGH_INTERSECTION_RADIUS_FACTOR);
    assertEquals(60, IntersectionConsolidatorSettings.DEFAULT_MAXIMUM_DISTANCE_SLIP_LANE_CROSSING_TO_JUNCTION_METERS);
  }
}
