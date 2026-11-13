package org.goplanit.utils.graph.algorithms;

import org.goplanit.utils.graph.Edge;
import org.goplanit.utils.graph.Vertex;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Partition a graph into connected components, treating every edge as traversable in both directions.
 * <p>
 * Typed on {@link Vertex} and {@link Edge} rather than on their directed counterparts, which is deliberate: the
 * <i>weak</i> components of a directed graph are exactly the connected components of its underlying undirected
 * graph, and directed vertices are vertices. A directed caller therefore uses this one, and no directed variant
 * needs to exist.
 * </p>
 * <p>
 * Note what this establishes and what it does not. Membership of the same component means a path exists when
 * direction is ignored. It does <b>not</b> mean the vertices can reach each other in practice, which is what
 * {@code StronglyConnectedComponents} answers. Pruning built only on this reports a one way trap as healthy.
 * </p>
 * <p>
 * Iterative, since the networks involved are far too large for recursion.
 * </p>
 *
 * @author markr
 */
public class ConnectedComponents {

  /**
   * Determine the connected components within the given scope.
   *
   * @param <V> type of vertex
   * @param vertices scope to partition; traversal never leaves this set
   * @param eligibleEdge condition an edge must meet to be traversable
   * @return components, largest first
   */
  public static <V extends Vertex> List<List<V>> execute(
      final Iterable<? extends V> vertices,
      final Predicate<? super Edge> eligibleEdge) {

    final Set<V> scope = new HashSet<>();
    vertices.forEach(scope::add);

    final List<List<V>> components = new ArrayList<>();
    final Set<V> visited = new HashSet<>(scope.size());

    for (var seed : scope) {
      if (visited.contains(seed)) {
        continue;
      }
      final List<V> component = new ArrayList<>();
      final Deque<V> pending = new ArrayDeque<>();
      visited.add(seed);
      pending.push(seed);

      while (!pending.isEmpty()) {
        final V current = pending.pop();
        component.add(current);
        for (Edge edge : current.getEdges()) {
          if (!eligibleEdge.test(edge)) {
            continue;
          }
          /* both extremities are considered, since the edge is undirected here and the vertex we arrived from
           * is simply already visited */
          addIfEligible(edge.getVertexA(), scope, visited, pending);
          addIfEligible(edge.getVertexB(), scope, visited, pending);
        }
      }
      components.add(component);
    }

    components.sort((a, b) -> Integer.compare(b.size(), a.size()));
    return components;
  }

  /**
   * The largest connected component within the given scope, empty when there are no vertices
   *
   * @param <V> type of vertex
   * @param vertices scope to partition
   * @param eligibleEdge condition an edge must meet to be traversable
   * @return largest component
   */
  public static <V extends Vertex> List<V> largest(
      final Iterable<? extends V> vertices, final Predicate<? super Edge> eligibleEdge) {
    var components = execute(vertices, eligibleEdge);
    return components.isEmpty() ? List.of() : Collections.unmodifiableList(components.get(0));
  }

  /** queue an extremity when it is in scope and not yet seen */
  @SuppressWarnings("unchecked")
  private static <V extends Vertex> void addIfEligible(
      final Vertex candidate,
      final Set<V> scope,
      final Set<V> visited,
      final Deque<V> pending) {
    if (candidate == null || !scope.contains(candidate) || visited.contains(candidate)) {
      return;
    }
    visited.add((V) candidate);
    pending.push((V) candidate);
  }
}
