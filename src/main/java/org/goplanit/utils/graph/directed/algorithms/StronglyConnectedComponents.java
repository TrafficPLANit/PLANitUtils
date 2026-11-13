package org.goplanit.utils.graph.directed.algorithms;

import org.goplanit.utils.graph.directed.DirectedVertex;
import org.goplanit.utils.graph.directed.EdgeSegment;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Partition a directed graph into strongly connected components using Kosaraju's algorithm.
 * <p>
 * Two vertices are in the same strongly connected component when each can reach the other following direction.
 * This is the connectivity notion routing needs. It is stricter than the weak connectivity established by
 * undirected traversal, which only asks whether a path exists when direction is ignored, and which therefore
 * reports a one way trap as perfectly healthy.
 * </p>
 * <p>
 * <b>Scope matters, so it is required rather than optional.</b> The useful question is nearly always "what are
 * the components <i>within</i> this part of the graph", most often within a weakly connected component. Asking
 * globally and then comparing against a weak component gives a wrong answer whenever the largest strongly
 * connected component happens to lie in a different weak component, which is easy to do by accident and produces
 * plausible looking nonsense.
 * </p>
 * <p>
 * Eligibility of edge segments is a predicate so that the same algorithm answers per mode, per track type and
 * unrestricted questions. Edge segments themselves carry no notion of mode; anything mode aware belongs on the
 * network layer side and reaches this algorithm only as a predicate.
 * </p>
 * <p>
 * Runs in O(V+E) with two traversals, and needs no transposed copy of the graph: the second pass reuses the same
 * traversal with direction inverted.
 * </p>
 *
 * @author markr
 */
public class StronglyConnectedComponents {

  /**
   * Determine the strongly connected components within the given scope.
   *
   * @param <V> type of directed vertex
   * @param vertices scope to partition; traversal never leaves this set
   * @param eligibleSegment condition an edge segment must meet to be traversable
   * @return result holding the components, largest first
   */
  public static <V extends DirectedVertex> Result<V> execute(
      final Iterable<? extends V> vertices,
      final Predicate<? super EdgeSegment> eligibleSegment) {

    final Set<V> scope = new HashSet<>();
    vertices.forEach(scope::add);

    /* pass one: order vertices by when they finish, following direction */
    final List<V> finishOrder = DepthFirstSearch.finishOrder(scope, eligibleSegment, false);

    /* pass two: assign components against the direction, taking vertices in reverse finishing order. Everything
     * reachable upstream from a vertex that has not yet been assigned forms one component */
    final var upstreamSegments = DirectedVertex.getEdgeSegmentsForVertexLambda(true);
    final var upstreamVertex = EdgeSegment.getVertexForEdgeSegmentLambda(true);

    final List<List<V>> components = new ArrayList<>();
    final Set<V> assigned = new HashSet<>(scope.size());

    for (int i = finishOrder.size() - 1; i >= 0; --i) {
      final V seed = finishOrder.get(i);
      if (assigned.contains(seed)) {
        continue;
      }
      final List<V> component = new ArrayList<>();
      final Deque<V> pending = new ArrayDeque<>();
      assigned.add(seed);
      pending.push(seed);

      while (!pending.isEmpty()) {
        final V current = pending.pop();
        component.add(current);
        for (var segment : upstreamSegments.apply(current)) {
          if (!eligibleSegment.test(segment)) {
            continue;
          }
          @SuppressWarnings("unchecked")
          final V candidate = (V) upstreamVertex.apply(segment);
          if (candidate == null || !scope.contains(candidate) || assigned.contains(candidate)) {
            continue;
          }
          assigned.add(candidate);
          pending.push(candidate);
        }
      }
      components.add(component);
    }

    components.sort((a, b) -> Integer.compare(b.size(), a.size()));
    return new Result<>(components);
  }

  /**
   * Outcome of a strongly connected component partition.
   * <p>
   * A value rather than something logged in passing: callers need the members, not a message. Filtering demand to
   * routable infrastructure needs the largest component as a set, and treating one way traps needs the vertices
   * of the smaller components as objects.
   * </p>
   *
   * @param <V> type of directed vertex
   */
  public static class Result<V extends DirectedVertex> {

    private final List<List<V>> components;
    private Map<V, Integer> indexByVertex;

    Result(List<List<V>> components) {
      this.components = components;
    }

    /**
     * All components, largest first
     *
     * @return components
     */
    public List<List<V>> getComponents() {
      return Collections.unmodifiableList(components);
    }

    /**
     * The largest component, empty when the scope held no vertices.
     * <p>
     * When several components share the largest size, which one is returned is arbitrary. That is rarely a
     * concern on real networks, where the routable component dwarfs everything else, but it means a caller
     * treating "largest" as "the main network" is relying on that disparity rather than on a guarantee.
     * </p>
     *
     * @return largest component
     */
    public List<V> getLargest() {
      return components.isEmpty() ? List.of() : Collections.unmodifiableList(components.get(0));
    }

    /**
     * Number of components
     *
     * @return number of components
     */
    public int size() {
      return components.size();
    }

    /**
     * Index of the component the vertex belongs to, 0 being the largest
     *
     * @param vertex to find
     * @return index, or -1 when the vertex was not in scope
     */
    public int getComponentIndexOf(V vertex) {
      if (indexByVertex == null) {
        /* built on first use, since many callers only ever want the largest component */
        indexByVertex = new HashMap<>();
        for (int i = 0; i < components.size(); ++i) {
          for (var v : components.get(i)) {
            indexByVertex.put(v, i);
          }
        }
      }
      return indexByVertex.getOrDefault(vertex, -1);
    }

    /**
     * Verify both vertices can reach each other following direction
     *
     * @param one first vertex
     * @param other second vertex
     * @return true when mutually reachable, false otherwise
     */
    public boolean areMutuallyReachable(V one, V other) {
      int index = getComponentIndexOf(one);
      return index >= 0 && index == getComponentIndexOf(other);
    }
  }
}
