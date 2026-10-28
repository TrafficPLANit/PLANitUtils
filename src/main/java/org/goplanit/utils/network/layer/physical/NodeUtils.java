package org.goplanit.utils.network.layer.physical;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.goplanit.utils.graph.directed.EdgeSegment;

/**
 * Utilities for nodes that are unfit for inclusion in the class itself
 *
 * @author markr
 */
public class NodeUtils {

  /** minimum number of links meeting at a junction node: where a way branches, or another way joins it */
  private static final int MIN_LINKS_AT_JUNCTION = 3;

  /** Constructor to disallow instance creation */
  private NodeUtils() {
  }

  /**
   * The link segments of a stream that meet a condition, by id so the order is the same every time
   *
   * @param <LS> link segment type
   * @param segments to choose from
   * @param condition to meet
   * @return the link segments meeting the condition, by id
   */
  @SuppressWarnings("unchecked")
  private static <LS extends EdgeSegment> List<LS> findById(
      Stream<? extends EdgeSegment> segments, Predicate<? super LS> condition) {
    return segments.map(segment -> (LS) segment).filter(condition)
        .sorted(Comparator.comparingLong(EdgeSegment::getId)).collect(Collectors.toList());
  }

  /**
   * Find the link segments entering a node that meet a condition, by id so the order is the same every time
   *
   * @param <LS> link segment type
   * @param node to look at
   * @param condition to meet
   * @return the link segments ending at the node that meet the condition, by id
   */
  public static <LS extends EdgeSegment> List<LS> findEntryLinkSegments(Node node, Predicate<? super LS> condition) {
    return findById(node.streamEntrySegments(), condition);
  }

  /**
   * Find the link segments leaving a node that meet a condition, by id so the order is the same every time
   *
   * @param <LS> link segment type
   * @param node to look at
   * @param condition to meet
   * @return the link segments starting at the node that meet the condition, by id
   */
  public static <LS extends EdgeSegment> List<LS> findExitLinkSegments(Node node, Predicate<? super LS> condition) {
    return findById(node.streamExitSegments(), condition);
  }

  /**
   * Verify if three or more links meet at a node, a junction node. Every link counts, footways included when they are
   * part of the network. Where only two links meet, the way simply passes through.
   *
   * @param node to check
   * @return true when three or more links meet there, false otherwise
   */
  public static boolean isWhereThreeOrMoreLinksMeet(Node node) {
    return node.getNumberOfEdges() >= MIN_LINKS_AT_JUNCTION;
  }

  /**
   * Verify if three or more links meet at a node with a segment, in either direction, that motorised road traffic can
   * use, see {@link #isWhereThreeOrMoreLinksWithSegmentMatchingMeet(Node, Predicate)}. Motorised traffic can go on
   * along more than one link there, or another road joins it there. A crosswalk, where footways meet the road, is not
   * one.
   *
   * @param node to check
   * @return true when the road branches there, false otherwise
   */
  public static boolean isWhereThreeOrMoreMotorisedRoadLinksMeet(Node node) {
    return isWhereThreeOrMoreLinksWithSegmentMatchingMeet(node, LinkSegmentUtils.permitsMotorisedRoadMode());
  }

  /**
   * Verify if three or more links meet at a node with a segment, in either direction, that meets a condition, see
   * {@link LinkUtils#hasSegmentMatching(Link, Predicate)}. So a way of such links goes on along more than one link
   * there, or another one joins it there.
   *
   * @param <LS> type of the links' segments
   * @param node to check
   * @param condition a segment must meet
   * @return true when such a way branches there, false otherwise
   */
  public static <LS extends LinkSegment> boolean isWhereThreeOrMoreLinksWithSegmentMatchingMeet(
      Node node, Predicate<? super LS> condition) {
    return node.getNumberOfEdges(edge -> LinkUtils.hasSegmentMatching((Link) edge, condition)) >=
        MIN_LINKS_AT_JUNCTION;
  }
}
