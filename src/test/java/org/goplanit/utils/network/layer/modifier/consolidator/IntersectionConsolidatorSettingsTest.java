package org.goplanit.utils.network.layer.modifier.consolidator;

import java.util.List;
import java.util.Set;

import org.goplanit.utils.id.IdMapperType;
import org.goplanit.utils.misc.Pair;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the values consolidation and collapse run with by default, and the lists of nodes they take
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
    assertEquals(30, IntersectionConsolidatorSettings.DEFAULT_MAXIMUM_DISTANCE_BETWEEN_CROSSINGS_SIDE_BY_SIDE_METERS);
  }

  /**
   * A copy of the settings holds the distance between crossings side by side set before copying
   */
  @Test
  public void distanceBetweenCrossingsSideBySideIsCopied() {
    var settings = new IntersectionConsolidatorSettings();
    settings.setMaximumDistanceBetweenCrossingsSideBySideMeters(12);
    assertEquals(12,
        new IntersectionConsolidatorSettings(settings).getMaximumDistanceBetweenCrossingsSideBySideMeters());
  }

  /**
   * The lists of nodes to join into one intersection, or to keep in separate intersections, start empty. Each call adds
   * one list, with the kind of id its node ids are, and a copy of the settings holds the same lists, which do not
   * change with the settings copied
   */
  @Test
  public void nodeListsAreAddedOneByOneAndCopied() {
    var settings = new IntersectionConsolidatorSettings();
    assertTrue(settings.getNodeIdsToJoinIntoOneIntersection().isEmpty());
    assertTrue(settings.getNodeIdsToKeepInSeparateIntersections().isEmpty());

    settings.addNodeIdsToJoinIntoOneIntersection(List.of("1", "2"), IdMapperType.EXTERNAL_ID);
    settings.addNodeIdsToKeepInSeparateIntersections(List.of("3", "4"), IdMapperType.XML);
    var copy = new IntersectionConsolidatorSettings(settings);
    settings.addNodeIdsToJoinIntoOneIntersection(List.of("6", "7"), IdMapperType.ID);

    assertEquals(List.of(Pair.of(Set.of("1", "2"), IdMapperType.EXTERNAL_ID)),
        copy.getNodeIdsToJoinIntoOneIntersection());
    assertEquals(List.of(Pair.of(Set.of("3", "4"), IdMapperType.XML)), copy.getNodeIdsToKeepInSeparateIntersections());
    assertEquals(2, settings.getNodeIdsToJoinIntoOneIntersection().size());
  }

  /**
   * A list needs at least two different node ids. A shorter one, of one node, of the same node twice, or empty, is not
   * stored, for either kind of list
   */
  @Test
  public void listsWithFewerThanTwoDifferentNodesAreIgnored() {
    var settings = new IntersectionConsolidatorSettings();

    settings.addNodeIdsToJoinIntoOneIntersection(List.of("1"), IdMapperType.EXTERNAL_ID);
    settings.addNodeIdsToJoinIntoOneIntersection(List.of("2", "2"), IdMapperType.EXTERNAL_ID);
    settings.addNodeIdsToKeepInSeparateIntersections(List.of(), IdMapperType.EXTERNAL_ID);
    settings.addNodeIdsToKeepInSeparateIntersections(List.of("3"), IdMapperType.EXTERNAL_ID);

    assertTrue(settings.getNodeIdsToJoinIntoOneIntersection().isEmpty());
    assertTrue(settings.getNodeIdsToKeepInSeparateIntersections().isEmpty());
  }

  /**
   * The nodes excluded from collapse start empty, each call adds to them, and a copy of the settings holds the same
   * nodes, which do not change with the settings copied
   */
  @Test
  public void nodesExcludedFromCollapseAreAddedAndCopied() {
    var settings = new IntersectionCollapserSettings();
    assertTrue(settings.getNodeIdsExcludedFromCollapse().isEmpty());

    settings.addNodeIdsExcludedFromCollapse(List.of("1"), IdMapperType.EXTERNAL_ID);
    var copy = new IntersectionCollapserSettings(settings);
    settings.addNodeIdsExcludedFromCollapse(List.of("2", "3"), IdMapperType.ID);

    assertEquals(List.of(Pair.of(Set.of("1"), IdMapperType.EXTERNAL_ID)), copy.getNodeIdsExcludedFromCollapse());
    assertEquals(2, settings.getNodeIdsExcludedFromCollapse().size());
  }
}
