package org.goplanit.utils.graph.directed.algorithms;

import org.goplanit.utils.graph.directed.DirectedVertex;
import org.goplanit.utils.graph.directed.EdgeSegment;
import org.goplanit.utils.misc.Pair;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * Breadth-first search over a directed graph.
 * <p>
 * Single source and terminating, i.e. it explores outward from one vertex until a termination condition is met,
 * returning the back links that allow the path to be reconstructed. It answers "can I get there, and how", which
 * is a different question from the whole graph partitioning performed by
 * {@link StronglyConnectedComponents}.
 * </p>
 * <p>
 * Direction is a parameter rather than a property of the graph, so the same call searches downstream or upstream
 * without any transposed copy of the graph existing.
 * </p>
 * <p>
 * Deliberately iterative. These searches run on networks with hundreds of thousands of vertices, where a
 * recursive formulation overflows the stack.
 * </p>
 *
 * @author markr
 */
public class BreadthFirstSearch {

  /**
   * Perform a breadth-first search from a starting vertex in a given direction with conditions
   * on:
   * <ul>
   *   <li>what initial edge segments from the start vertex to consider</li>
   *   <li>what edge segments from any other vertex to consider</li>
   *   <li>what constitutes a successful search by defining a termination condition</li>
   * </ul>
   *
   * @param <ES> edge segment type
   * @param <V> directed vertex type
   * @param startVertex to start search from
   * @param invertDirection direction for searching, when true invert direction from downstream to upstream
   * @param initialVertexSegmentInclusionCondition predicate for initial condition on edge segments to consider
   * @param vertexSegmentInclusionCondition predicate for general condition on edge segments to consider (prevEdgeSegment, currEdgeSegment)
   * @param vertexSegmentTerminationCondition predicate for termination condition for successful search completion
   * @return found vertex (if any) based on termination and the back links for all processed vertices
   */
  @SuppressWarnings("unchecked")
  public static <V extends DirectedVertex,ES extends EdgeSegment> Pair<V, Map<V, ES>> execute(
      V startVertex,
      boolean invertDirection,
      Predicate<ES> initialVertexSegmentInclusionCondition,
      BiPredicate<ES, ES> vertexSegmentInclusionCondition,
      BiPredicate<V,ES> vertexSegmentTerminationCondition){


    Deque<Pair<V, ES>> openVertexQueue = new ArrayDeque<>(30);
    Map<V, ES> processedVertices = new TreeMap<>();

    /* Search in desired direction */
    final var getNextEdgeSegments =
        DirectedVertex.getEdgeSegmentsForVertexLambda(invertDirection);
    final var getNextVertex = EdgeSegment.getVertexForEdgeSegmentLambda(invertDirection);

    /* start with eligible edge segments of reference vertex except alternative labelled segment */
    processedVertices.put(startVertex, null);
    var nextEdgeSegments = getNextEdgeSegments.apply(startVertex);
    for (var nextSegment : nextEdgeSegments) {
      if(initialVertexSegmentInclusionCondition.test((ES) nextSegment)){
        openVertexQueue.add(Pair.of((V)getNextVertex.apply(nextSegment), (ES) nextSegment));
      }
    }

    while (!openVertexQueue.isEmpty()) {
      Pair<V, ES> current = openVertexQueue.pop();
      var currentVertex = current.first();
      if (processedVertices.containsKey(currentVertex)) {
        continue;
      }

      if (vertexSegmentTerminationCondition.test(currentVertex, current.second())) {
        processedVertices.put(currentVertex, current.second());
        // success
        return Pair.of((V) current.first(), processedVertices);
      }

      /* breadth-first loop for unprocessed vertices */
      nextEdgeSegments = getNextEdgeSegments.apply(currentVertex);
      for (var nextSegment : nextEdgeSegments) {
        if (vertexSegmentInclusionCondition.test(current.second(), (ES) nextSegment)) {
          var nextVertex = (V) getNextVertex.apply(nextSegment);
          if (!processedVertices.containsKey(nextVertex)) {
            openVertexQueue.add(Pair.of(nextVertex, (ES) nextSegment));
          }
        }
      }

      processedVertices.put(currentVertex, current.second());
    }

    // no success
    return Pair.of(null,processedVertices);
  }
}
