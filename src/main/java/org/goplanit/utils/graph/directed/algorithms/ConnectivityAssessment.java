package org.goplanit.utils.graph.directed.algorithms;

import org.goplanit.utils.graph.Edge;
import org.goplanit.utils.graph.algorithms.ConnectedComponents;
import org.goplanit.utils.graph.directed.DirectedVertex;
import org.goplanit.utils.graph.directed.EdgeSegment;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Compare what a graph is weakly connected by against what it is strongly connected by, and classify the
 * difference.
 * <p>
 * The gap between the two is the interesting part. A vertex sitting inside the largest weakly connected component
 * looks healthy to any undirected check, yet may be unusable in practice because direction prevents reaching it,
 * leaving it, or both. Those three cases have different consequences, so they are reported separately rather than
 * as a single count.
 * </p>
 * <p>
 * This deliberately returns a value and logs nothing. Callers need the members and not a message: filtering
 * demand to routable infrastructure needs the strongly connected set, and treating traps needs the trapped
 * vertices as objects. What to report, at which level, and whether to report at all is the caller's decision.
 * </p>
 *
 * @author markr
 */
public class ConnectivityAssessment {

  /**
   * Assess connectivity for the given scope.
   * <p>
   * The strongly connected components are determined <i>within</i> the largest weakly connected component rather
   * than across the whole scope. Doing it the other way round allows the largest strongly connected component to
   * be found in a different weak component than the one it is compared against, which yields a meaningless
   * trapped count.
   * </p>
   *
   * @param <V> type of directed vertex
   * @param vertices scope to assess
   * @param eligibleSegment condition an edge segment must meet to be traversable
   * @return the assessment
   */
  public static <V extends DirectedVertex> Result<V> assess(
      final Iterable<? extends V> vertices,
      final Predicate<? super EdgeSegment> eligibleSegment) {

    /* an edge participates when at least one of its segments does, which is what makes the undirected view the
     * same infrastructure seen without regard for direction */
    final Predicate<? super Edge> eligibleEdge = edge -> {
      if (!(edge instanceof org.goplanit.utils.graph.directed.DirectedEdge)) {
        return false;
      }
      var directedEdge = (org.goplanit.utils.graph.directed.DirectedEdge) edge;
      return (directedEdge.hasEdgeSegmentAb() && eligibleSegment.test(directedEdge.getEdgeSegmentAb()))
          || (directedEdge.hasEdgeSegmentBa() && eligibleSegment.test(directedEdge.getEdgeSegmentBa()));
    };

    final List<V> largestWeak = ConnectedComponents.largest(vertices, eligibleEdge);
    if (largestWeak.isEmpty()) {
      return new Result<>(List.of(), List.of(), Set.of(), Set.of(), Set.of(), Set.of(), 0);
    }

    final var scc = StronglyConnectedComponents.execute(largestWeak, eligibleSegment);
    final List<V> main = scc.getLargest();
    final Set<V> mainSet = new HashSet<>(main);

    /* everything that can reach the main component, and everything the main component can reach */
    final Set<V> canReachMain = traverse(mainSet, eligibleSegment, true, largestWeak);
    final Set<V> reachableFromMain = traverse(mainSet, eligibleSegment, false, largestWeak);

    final Set<V> exitOnly = new HashSet<>();
    final Set<V> entryOnly = new HashSet<>();
    final Set<V> severed = new HashSet<>();
    final Set<V> partial = new HashSet<>();

    for (var vertex : largestWeak) {
      if (mainSet.contains(vertex)) {
        continue;
      }
      boolean canLeave = canReachMain.contains(vertex);
      boolean canEnter = reachableFromMain.contains(vertex);
      if (canLeave && !canEnter) {
        exitOnly.add(vertex);
      } else if (canEnter && !canLeave) {
        entryOnly.add(vertex);
      } else if (canLeave) {
        partial.add(vertex);
      } else {
        severed.add(vertex);
      }
    }

    return new Result<>(largestWeak, main, exitOnly, entryOnly, severed, partial, scc.size() - 1);
  }

  /**
   * Collect everything reachable from the seeds, restricted to the given scope.
   *
   * @param seeds to start from
   * @param eligibleSegment condition an edge segment must meet
   * @param invertDirection when true traverse upstream, yielding what can reach the seeds
   * @param scope traversal never leaves this set
   * @return reachable vertices, including the seeds
   */
  private static <V extends DirectedVertex> Set<V> traverse(
      final Set<V> seeds,
      final Predicate<? super EdgeSegment> eligibleSegment,
      final boolean invertDirection,
      final Iterable<? extends V> scope) {

    final Set<V> inScope = new HashSet<>();
    scope.forEach(inScope::add);

    final var nextSegments = DirectedVertex.getEdgeSegmentsForVertexLambda(invertDirection);
    final var nextVertex = EdgeSegment.getVertexForEdgeSegmentLambda(invertDirection);

    final Set<V> seen = new HashSet<>(seeds);
    final Deque<V> pending = new ArrayDeque<>(seeds);
    while (!pending.isEmpty()) {
      var current = pending.pop();
      for (var segment : nextSegments.apply(current)) {
        if (!eligibleSegment.test(segment)) {
          continue;
        }
        @SuppressWarnings("unchecked")
        var candidate = (V) nextVertex.apply(segment);
        if (candidate == null || !inScope.contains(candidate) || seen.contains(candidate)) {
          continue;
        }
        seen.add(candidate);
        pending.push(candidate);
      }
    }
    return seen;
  }

  /**
   * Outcome of a connectivity assessment, in memory rather than logged so that callers can act on it as well as
   * report it.
   *
   * @param <V> type of directed vertex
   */
  public static class Result<V extends DirectedVertex> {

    private final List<V> largestWeak;
    private final List<V> largestStrong;
    private final Set<V> exitOnly;
    private final Set<V> entryOnly;
    private final Set<V> severed;
    private final Set<V> partial;
    private final int pocketCount;

    Result(List<V> largestWeak, List<V> largestStrong, Set<V> exitOnly, Set<V> entryOnly,
           Set<V> severed, Set<V> partial, int pocketCount) {
      this.largestWeak = largestWeak;
      this.largestStrong = largestStrong;
      this.exitOnly = exitOnly;
      this.entryOnly = entryOnly;
      this.severed = severed;
      this.partial = partial;
      this.pocketCount = pocketCount;
    }

    /**
     * The largest weakly connected component, i.e. what an undirected check regards as the network
     *
     * @return vertices
     */
    public List<V> getLargestWeaklyConnected() {
      return Collections.unmodifiableList(largestWeak);
    }

    /**
     * The largest strongly connected component within the largest weak one, i.e. the part that is actually
     * usable for routing in both directions
     *
     * @return vertices
     */
    public List<V> getLargestStronglyConnected() {
      return Collections.unmodifiableList(largestStrong);
    }

    /**
     * Vertices that can leave the main network but cannot be reached from it, so unusable as a destination
     *
     * @return vertices
     */
    public Set<V> getExitOnly() {
      return Collections.unmodifiableSet(exitOnly);
    }

    /**
     * Vertices reachable from the main network that cannot get back, so unusable as an origin
     *
     * @return vertices
     */
    public Set<V> getEntryOnly() {
      return Collections.unmodifiableSet(entryOnly);
    }

    /**
     * Vertices cut off from the main network in both directions
     *
     * @return vertices
     */
    public Set<V> getSevered() {
      return Collections.unmodifiableSet(severed);
    }

    /**
     * Vertices that can both reach and be reached by the main network while not being strongly connected to it.
     * <p>
     * Empirically empty on every network measured so far. A non-empty result here is not an error but is worth a
     * caller's attention, since it means traps are not simply one directional.
     * </p>
     *
     * @return vertices
     */
    public Set<V> getPartiallyConnected() {
      return Collections.unmodifiableSet(partial);
    }

    /**
     * All vertices inside the largest weak component that are not strongly connected to its main component
     *
     * @return number of trapped vertices
     */
    public int getNumberOfTrapped() {
      return exitOnly.size() + entryOnly.size() + severed.size() + partial.size();
    }

    /**
     * Number of separate pockets the trapped vertices form
     *
     * @return number of pockets
     */
    public int getNumberOfPockets() {
      return pocketCount;
    }

    /**
     * Proportion of the largest weak component that is trapped, as a percentage
     *
     * @return percentage, 0 when there is nothing to assess
     */
    public double getTrappedPercentage() {
      return largestWeak.isEmpty() ? 0.0 : (100.0 * getNumberOfTrapped()) / largestWeak.size();
    }

    /**
     * Single line summary suitable for logging, provided so that callers report consistently without this class
     * deciding whether or where anything is logged
     *
     * @return summary
     */
    @Override
    public String toString() {
      return String.format(
          "Largest weakly connected %d, strongly connected %d, trapped %d (%.2f%%) in %d pockets "
              + "[no destination %d, no origin %d, severed %d, partial %d]",
          largestWeak.size(), largestStrong.size(), getNumberOfTrapped(), getTrappedPercentage(),
          pocketCount, exitOnly.size(), entryOnly.size(), severed.size(), partial.size());
    }
  }
}
