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
import java.util.function.Predicate;

/**
 * Depth-first traversal over a directed graph.
 * <p>
 * Unlike {@link BreadthFirstSearch}, which searches outward from one vertex for a target, this exists to visit a
 * whole vertex set and report the order in which vertices <i>finish</i>, i.e. the order in which they are left
 * behind once everything reachable from them has been explored. That ordering is what
 * {@link StronglyConnectedComponents} needs for its first pass.
 * </p>
 * <p>
 * <b>Iterative by necessity, not by taste.</b> The recursive formulation found in textbooks recurses once per
 * vertex along a path, and these traversals run on road networks with hundreds of thousands of vertices, where a
 * single long chain of links is enough to overflow the stack. The explicit stack below holds a vertex together
 * with an iterator over its outgoing segments, so a vertex is only finished once its iterator is exhausted, which
 * reproduces the recursive post-order exactly.
 * </p>
 *
 * @author markr
 */
public class DepthFirstSearch {

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

    final Set<V> scope = new HashSet<>();
    vertices.forEach(scope::add);

    final var nextSegments = DirectedVertex.getEdgeSegmentsForVertexLambda(invertDirection);
    final var nextVertex = EdgeSegment.getVertexForEdgeSegmentLambda(invertDirection);

    final List<V> finished = new ArrayList<>(scope.size());
    final Set<V> visited = new HashSet<>(scope.size());
    final Deque<StackFrame<V>> stack = new ArrayDeque<>();

    for (var seed : scope) {
      if (visited.contains(seed)) {
        continue;
      }
      visited.add(seed);
      stack.push(new StackFrame<>(seed, nextSegments.apply(seed).iterator()));

      while (!stack.isEmpty()) {
        var frame = stack.peek();
        V descendant = null;

        /* advance this vertex's own iterator rather than re-deriving its neighbours, so each segment is
         * considered exactly once no matter how often the vertex is revisited on the stack */
        while (frame.remaining.hasNext()) {
          var segment = frame.remaining.next();
          if (!eligibleSegment.test(segment)) {
            continue;
          }
          @SuppressWarnings("unchecked")
          var candidate = (V) nextVertex.apply(segment);
          if (candidate == null || !scope.contains(candidate) || visited.contains(candidate)) {
            continue;
          }
          descendant = candidate;
          break;
        }

        if (descendant == null) {
          /* nothing left to explore from here, so this vertex is finished */
          finished.add(frame.vertex);
          stack.pop();
        } else {
          visited.add(descendant);
          stack.push(new StackFrame<>(descendant, nextSegments.apply(descendant).iterator()));
        }
      }
    }
    return finished;
  }

  /** a vertex together with how far its neighbours have been consumed, the iterative stand-in for a stack frame */
  private static class StackFrame<V extends DirectedVertex> {
    final V vertex;
    final Iterator<? extends EdgeSegment> remaining;

    @SuppressWarnings("unchecked")
    StackFrame(V vertex, Iterator<?> remaining) {
      this.vertex = vertex;
      this.remaining = (Iterator<? extends EdgeSegment>) remaining;
    }
  }
}
