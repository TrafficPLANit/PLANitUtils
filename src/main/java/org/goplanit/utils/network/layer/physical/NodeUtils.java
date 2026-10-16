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
}
