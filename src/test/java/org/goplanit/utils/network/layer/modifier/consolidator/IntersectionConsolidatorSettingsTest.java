package org.goplanit.utils.network.layer.modifier.consolidator;

import java.util.List;
import java.util.Set;

import org.goplanit.utils.id.IdMapperType;
import org.goplanit.utils.misc.Pair;
import org.goplanit.utils.settings.SettingsSchema;
import org.goplanit.utils.settings.SettingsSource;
import org.goplanit.utils.settings.SettingsYamlLoader;
import org.goplanit.utils.settings.UserOverride;
import org.goplanit.utils.settings.annotations.SettingsHeader;
import org.goplanit.utils.settings.annotations.SettingsKeyLevel;
import org.goplanit.utils.settings.annotations.SettingsSection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the values consolidation and collapse run with by default, and the lists of nodes they take
 *
 * @author markr
 */
public class IntersectionConsolidatorSettingsTest {

  /** Settings holding the consolidation settings as a section, as the settings of a converter do */
  @SettingsHeader(key = "planitConsolidatorTestSettings", version = 1)
  @SettingsSection(key = "test", level = SettingsKeyLevel.STANDARD,
      description = "Settings holding the consolidation settings")
  public static final class TestConsolidatorHolderSettings {

    /** the consolidation settings held */
    private final IntersectionConsolidatorSettings intersectionConsolidatorSettings =
        new IntersectionConsolidatorSettings();

    /** @return the consolidation settings held */
    @SettingsSection(key = "intersectionConsolidator", level = SettingsKeyLevel.UNCOMMON,
        description = "How the nodes of one junction are found and joined into one intersection")
    public IntersectionConsolidatorSettings getIntersectionConsolidatorSettings() {
      return intersectionConsolidatorSettings;
    }
  }

  /**
   * Every value of the consolidation settings read back from a file, each away from its default
   */
  @Test
  public void everyValueReadsBackFromAFile() {
    var schema = new SettingsSchema(TestConsolidatorHolderSettings.class);
    var settings = new SettingsYamlLoader(schema, List.of(SettingsSource.ofText(schema,
        "test:\n  intersectionConsolidator:\n    maximumJoiningReachMeters: 41\n" +
            "    minimumCoverageGainPerMember: 0.06\n    coverageTieMargin: 0.07\n" +
            "    maximumParsedIntersectionsToTryEverySubdivision: 8\n    subdivisionsKeptPerSearchStep: 4\n" +
            "    pullInMarginBeyondRadiusMeters: 31\n    maximumPullInConnectionLengthMeters: 81\n" +
            "    internalLinkLengthWarningMeters: 101\n" +
            "    maximumDistanceReachedOnlyThroughIntersectionMeters: 61\n" +
            "    maximumDistanceReachedOnlyThroughIntersectionRadiusFactor: 2.5\n" +
            "    maximumDistanceSlipLaneCrossingToJunctionMeters: 62\n" +
            "    maximumDistanceBetweenCrossingsSideBySideMeters: 32\n    includeActiveModesInMovements: true\n")))
        .create(TestConsolidatorHolderSettings.class).getIntersectionConsolidatorSettings();

    assertEquals(41, settings.getMaximumJoiningReachMeters());
    assertEquals(0.06, settings.getMinimumCoverageGainPerMember());
    assertEquals(0.07, settings.getCoverageTieMargin());
    assertEquals(8, settings.getMaximumParsedIntersectionsToTryEverySubdivision());
    assertEquals(4, settings.getSubdivisionsKeptPerSearchStep());
    assertEquals(31, settings.getPullInMarginBeyondRadiusMeters());
    assertEquals(81, settings.getMaximumPullInConnectionLengthMeters());
    assertEquals(101, settings.getInternalLinkLengthWarningMeters());
    assertEquals(61, settings.getMaximumDistanceReachedOnlyThroughIntersectionMeters());
    assertEquals(2.5, settings.getMaximumDistanceReachedOnlyThroughIntersectionRadiusFactor());
    assertEquals(62, settings.getMaximumDistanceSlipLaneCrossingToJunctionMeters());
    assertEquals(32, settings.getMaximumDistanceBetweenCrossingsSideBySideMeters());
    assertTrue(settings.isIncludeActiveModesInMovements());
  }

  /**
   * No subdivision kept per search step is refused: the search would have nothing to go on from
   */
  @Test
  public void noSubdivisionKeptPerSearchStepIsRefused() {
    var settings = new IntersectionConsolidatorSettings();
    assertThrows(IllegalArgumentException.class, () -> settings.setSubdivisionsKeptPerSearchStep(0));
    assertEquals(IntersectionConsolidatorSettings.DEFAULT_SUBDIVISIONS_KEPT_PER_SEARCH_STEP,
        settings.getSubdivisionsKeptPerSearchStep());
  }

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
    assertFalse(new IntersectionConsolidatorSettings().isIncludeActiveModesInMovements());
  }

  /**
   * A copy of the settings holds whether active modes are included in movements, as set before copying
   */
  @Test
  public void includeActiveModesInMovementsIsCopied() {
    var settings = new IntersectionConsolidatorSettings();
    settings.setIncludeActiveModesInMovements(true);
    assertTrue(new IntersectionConsolidatorSettings(settings).isIncludeActiveModesInMovements());
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

    assertEquals(List.of(UserOverride.of(Pair.of(Set.of("1", "2"), IdMapperType.EXTERNAL_ID))),
        copy.getNodeIdsToJoinIntoOneIntersection());
    assertEquals(List.of(UserOverride.of(Pair.of(Set.of("3", "4"), IdMapperType.XML))),
        copy.getNodeIdsToKeepInSeparateIntersections());
    assertEquals(2, settings.getNodeIdsToJoinIntoOneIntersection().size());
  }

  /**
   * A list keeps the reason it was added with, and so does a copy of the settings
   */
  @Test
  public void aListKeepsItsReason() {
    var settings = new IntersectionConsolidatorSettings();
    settings.addNodeIdsToJoinIntoOneIntersection(List.of("1", "2"), IdMapperType.EXTERNAL_ID, "one controller");
    settings.addNodeIdsToKeepInSeparateIntersections(List.of("3", "4"), IdMapperType.EXTERNAL_ID, "two controllers");
    var copy = new IntersectionConsolidatorSettings(settings);

    assertEquals("one controller", copy.getNodeIdsToJoinIntoOneIntersection().get(0).getReason());
    assertEquals("two controllers", copy.getNodeIdsToKeepInSeparateIntersections().get(0).getReason());
  }

  /**
   * A list needs at least two different node ids. A shorter one, of one node, of the same node twice, or empty, is
   * refused and not stored, for either kind of list
   */
  @Test
  public void listsWithFewerThanTwoDifferentNodesAreRefused() {
    var settings = new IntersectionConsolidatorSettings();

    assertThrows(IllegalArgumentException.class,
        () -> settings.addNodeIdsToJoinIntoOneIntersection(List.of("1"), IdMapperType.EXTERNAL_ID));
    assertThrows(IllegalArgumentException.class,
        () -> settings.addNodeIdsToJoinIntoOneIntersection(List.of("2", "2"), IdMapperType.EXTERNAL_ID));
    assertThrows(IllegalArgumentException.class,
        () -> settings.addNodeIdsToKeepInSeparateIntersections(List.of(), IdMapperType.EXTERNAL_ID));
    assertThrows(IllegalArgumentException.class,
        () -> settings.addNodeIdsToKeepInSeparateIntersections(List.of("3"), IdMapperType.EXTERNAL_ID, "checked"));

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
