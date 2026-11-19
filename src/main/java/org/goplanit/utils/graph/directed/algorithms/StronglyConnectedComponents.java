package org.goplanit.utils.graph.directed.algorithms;

import org.goplanit.utils.graph.directed.DirectedVertex;
import org.goplanit.utils.graph.directed.EdgeSegment;
import org.goplanit.utils.graph.directed.EdgeSegmentUtils;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Function;
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
 * The same partition is available for edge segments, given which turns between them are allowed, and for any items,
 * given each item's next and previous items. Edge segments are what to partition when not every turn is allowed: a
 * vertex can then be reachable while an edge segment entering it is not, because every turn onto that edge segment is
 * banned.
 * </p>
 * <p>
 * Runs in O(V+E) with two traversals, and needs no transposed copy of the graph: the second pass follows each item's
 * previous items instead of its next ones. For edge segments, V is the number of edge segments and E the number of
 * allowed turns.
 * </p>
 *
 * @author markr
 */
public class StronglyConnectedComponents {

  /**
   * Outcome of a strongly connected component partition.
   * <p>
   * A value rather than something logged in passing: callers need the members, not a message. Filtering demand to
   * routable infrastructure needs the largest component as a set, and treating one way traps needs the vertices
   * of the smaller components as objects.
   * </p>
   *
   * @param <T> type of item partitioned, such as a directed vertex or an edge segment
   */
  public static class Result<T> {

    private final List<List<T>> components;
    private Map<T, Integer> indexByItem;

    private Result(List<List<T>> components) {
      this.components = components;
    }

    /**
     * All components, largest first
     *
     * @return components
     */
    public List<List<T>> getComponents() {
      return Collections.unmodifiableList(components);
    }

    /**
     * The largest component, empty when the scope held no items.
     * <p>
     * When several components share the largest size, which one is returned is arbitrary. That is rarely a
     * concern on real networks, where the routable component dwarfs everything else, but it means a caller
     * treating "largest" as "the main network" is relying on that disparity rather than on a guarantee.
     * </p>
     *
     * @return largest component
     */
    public List<T> getLargest() {
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
     * Index of the component the item belongs to, 0 being the largest
     *
     * @param item to find
     * @return index, or -1 when the item was not in scope
     */
    public int getComponentIndexOf(T item) {
      if (indexByItem == null) {
        /* built on first use, since many callers only ever want the largest component */
        indexByItem = new HashMap<>();
        for (int i = 0; i < components.size(); ++i) {
          for (var member : components.get(i)) {
            indexByItem.put(member, i);
          }
        }
      }
      return indexByItem.getOrDefault(item, -1);
    }

    /**
     * Verify both items can reach each other
     *
     * @param one first item
     * @param other second item
     * @return true when mutually reachable, false otherwise
     */
    public boolean areMutuallyReachable(T one, T other) {
      int index = getComponentIndexOf(one);
      return index >= 0 && index == getComponentIndexOf(other);
    }
  }

  /**
   * Partition the items of a scope into strongly connected components, largest first: the second pass of Kosaraju's
   * algorithm, taking items in reverse order of finishing and gathering into one component everything that leads to an
   * item and has not been gathered yet
   *
   * @param <T> type of item
   * @param scope items to partition; neither pass leaves it
   * @param nextItems the items an item leads on to
   * @param previousItems the items that lead on to an item
   * @return result holding the components, largest first
   */
  private static <T> Result<T> partition(
      final Set<T> scope,
      final Function<? super T, ? extends Iterable<? extends T>> nextItems,
      final Function<? super T, ? extends Iterable<? extends T>> previousItems) {

    /* pass one: order items by when they finish, following next items */
    final List<T> finishOrder = DepthFirstSearch.finishOrder(scope, nextItems);

    /* pass two: assign components following previous items, taking items in reverse finishing order. Everything that
     * leads to an item that has not yet been assigned forms one component */
    final List<List<T>> components = new ArrayList<>();
    final Set<T> assigned = new HashSet<>(scope.size());

    for (int i = finishOrder.size() - 1; i >= 0; --i) {
      final T seed = finishOrder.get(i);
      if (assigned.contains(seed)) {
        continue;
      }
      final List<T> component = new ArrayList<>();
      final Deque<T> pending = new ArrayDeque<>();
      assigned.add(seed);
      pending.push(seed);

      while (!pending.isEmpty()) {
        final T current = pending.pop();
        component.add(current);
        for (T candidate : previousItems.apply(current)) {
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
   * Determine the strongly connected components within the given scope.
   *
   * @param <V> type of directed vertex
   * @param vertices scope to partition; traversal never leaves this set
   * @param eligibleSegment condition an edge segment must meet to be traversable
   * @return result holding the components, largest first
   */
  public static <V extends DirectedVertex> Result<V> executeOnVertices(
      final Iterable<? extends V> vertices,
      final Predicate<? super EdgeSegment> eligibleSegment) {
    return execute(vertices, DirectedVertex.<V>getAdjacentVerticesForVertexLambda(eligibleSegment, false),
        DirectedVertex.<V>getAdjacentVerticesForVertexLambda(eligibleSegment, true));
  }

  /**
   * Determine the strongly connected components of the given edge segments, through the turns allowed between them.
   * Two edge segments are in the same component when each can be reached from the other by a sequence of allowed turns
   * that stays within the given edge segments. Turning back onto the other direction of the same edge, where the
   * condition allows it, is a turn like any other.
   * <p>
   * This exists because connectivity between vertices cannot see a banned turn. A vertex can be reachable while an edge
   * segment entering it is not, because every turn onto that edge segment is banned, and a vertex based partition then
   * reports the edge segment as part of the network a traveller can use.
   * </p>
   *
   * @param <ES> type of edge segment
   * @param segments scope to partition; traversal never leaves this set
   * @param canTurn true when a traveller may turn from the first edge segment onto the second, which leaves the vertex
   *                the first one ends at; only asked for edge segments of the scope
   * @return result holding the components, largest first
   */
  public static <ES extends EdgeSegment> Result<ES> executeOnEdgeSegments(
      final Iterable<? extends ES> segments,
      final BiPredicate<? super ES, ? super ES> canTurn) {
    final Set<ES> scope = new HashSet<>();
    segments.forEach(scope::add);
    /* the scope is checked first, so the condition is only ever asked about edge segments of the scope */
    final BiPredicate<ES, ES> canTurnWithinScope =
        (from, onto) -> scope.contains(from) && scope.contains(onto) && canTurn.test(from, onto);
    return partition(scope,
        segment -> EdgeSegmentUtils.findEdgeSegmentsOneAllowedTurnAway(segment, false, canTurnWithinScope),
        segment -> EdgeSegmentUtils.findEdgeSegmentsOneAllowedTurnAway(segment, true, canTurnWithinScope));
  }

  /**
   * Determine the strongly connected components of any items, given the items each one leads on to and the items
   * leading on to it. Two items are in the same component when each can be reached from the other by following next
   * items within the given items. The two must agree: an item is among the previous items of another exactly when that
   * other is among its next items
   *
   * @param <T> type of item
   * @param items scope to partition; traversal never leaves this set
   * @param nextItems the items an item leads on to
   * @param previousItems the items that lead on to an item
   * @return result holding the components, largest first
   */
  public static <T> Result<T> execute(
      final Iterable<? extends T> items,
      final Function<? super T, ? extends Iterable<? extends T>> nextItems,
      final Function<? super T, ? extends Iterable<? extends T>> previousItems) {
    final Set<T> scope = new HashSet<>();
    items.forEach(scope::add);
    return partition(scope, nextItems, previousItems);
  }
}
