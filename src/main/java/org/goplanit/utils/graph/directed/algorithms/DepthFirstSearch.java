package org.goplanit.utils.graph.directed.algorithms;

import org.goplanit.utils.graph.directed.DirectedVertex;
import org.goplanit.utils.graph.directed.EdgeSegment;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Depth-first traversal over a directed graph, or over any items that each lead on to other items.
 * <p>
 * Unlike {@link BreadthFirstSearch}, which searches outward from one vertex for a target, this visits a whole set and
 * reports the order in which its members <i>finish</i>, i.e. are left behind once everything reachable from them has
 * been explored. {@link StronglyConnectedComponents} needs that order for its first pass.
 * </p>
 * <p>
 * <b>Iterative by necessity.</b> The recursive formulation found in textbooks recurses once per vertex along a path,
 * and these traversals run on road networks with hundreds of thousands of vertices, where a single long chain of
 * links is enough to overflow the stack. The explicit stack below holds an item with an iterator over the items it
 * leads on to, so an item is only finished once its iterator is exhausted. This reproduces the recursive post-order
 * exactly.
 * </p>
 *
 * @author markr
 */
public class DepthFirstSearch {

  /** an item whose next items are still being explored, with an iterator over the next items not looked at yet */
  private static class ItemBeingExplored<T> {
    private final T item;
    private final Iterator<? extends T> remaining;

    private ItemBeingExplored(T item, Iterator<? extends T> remaining) {
      this.item = item;
      this.remaining = remaining;
    }
  }

  /**
   * Traverse every vertex in the given scope and return them in the order they finished.
   * <p>
   * Vertices are only followed within the given scope, so passing a subset restricts the traversal to that
   * subset rather than escaping into the wider graph.
   * </p>
   *
   * @param <V> type of directed vertex
   * @param vertices scope to traverse, every vertex is used as a seed unless already visited
   * @param eligibleSegment condition an edge segment must meet to be traversable
   * @param invertDirection when true follow entry segments upstream instead of exit segments downstream
   * @return vertices in order of finishing, i.e. post-order
   */
  public static <V extends DirectedVertex> List<V> finishOrder(
      final Iterable<? extends V> vertices,
      final Predicate<? super EdgeSegment> eligibleSegment,
      final boolean invertDirection) {
    return finishOrder(
        vertices, DirectedVertex.<V>getAdjacentVerticesForVertexLambda(eligibleSegment, invertDirection));
  }

  /**
   * Traverse every item in the given scope and return them in the order they finished.
   * <p>
   * Items are only followed within the given scope, so passing a subset restricts the traversal to that subset
   * rather than escaping into whatever the items lead on to beyond it.
   * </p>
   *
   * @param <T> type of item
   * @param items scope to traverse, every item is used as a seed unless already visited
   * @param nextItems the items an item leads on to
   * @return items in order of finishing, i.e. post-order
   */
  public static <T> List<T> finishOrder(
      final Iterable<? extends T> items,
      final Function<? super T, ? extends Iterable<? extends T>> nextItems) {

    final Set<T> scope = new HashSet<>();
    items.forEach(scope::add);

    final List<T> finished = new ArrayList<>(scope.size());
    final Set<T> visited = new HashSet<>(scope.size());
    final Deque<ItemBeingExplored<T>> stack = new ArrayDeque<>();

    for (var seed : scope) {
      if (visited.contains(seed)) {
        continue;
      }
      visited.add(seed);
      stack.push(new ItemBeingExplored<>(seed, nextItems.apply(seed).iterator()));

      while (!stack.isEmpty()) {
        var exploring = stack.peek();
        T descendant = null;

        /* advance this item's own iterator rather than re-deriving its next items, so each is considered exactly
         * once no matter how often the item is revisited on the stack */
        while (exploring.remaining.hasNext()) {
          T candidate = exploring.remaining.next();
          if (candidate == null || !scope.contains(candidate) || visited.contains(candidate)) {
            continue;
          }
          descendant = candidate;
          break;
        }

        if (descendant == null) {
          /* nothing left to explore from here, so this item is finished */
          finished.add(exploring.item);
          stack.pop();
        } else {
          visited.add(descendant);
          stack.push(new ItemBeingExplored<>(descendant, nextItems.apply(descendant).iterator()));
        }
      }
    }
    return finished;
  }
}
