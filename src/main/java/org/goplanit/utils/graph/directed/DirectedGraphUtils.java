package org.goplanit.utils.graph.directed;

import org.goplanit.utils.misc.Pair;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * Utilities for directed graphs
 *
 * @author markr
 */
public class DirectedGraphUtils {

  /**
   * Perform a breadth-first search from a starting vertex in a given direction with conditions
   * on:
   * <ul>
   *   <li>what initial edge segments from the start vertex to consider</li>
   *   <li>what edge segments from any other vertex to consider</li>
   *   <li>what constitutes a successful search by defining a termination condition</li>
   * </ul>
   *
   * @param startVertex to start search from
   * @param invertDirection direction for searching, when true invert direction from downstream to upstream
   * @param initialVertexSegmentInclusionCondition predicate for initial condition on edge segments to consider
   * @param vertexSegmentInclusionCondition predicate for general condition on edge segments to consider (prevEdgeSegment, currEdgeSegment)
   * @param vertexSegmentTerminationCondition predicate for termination condition for successful search completion
   * @return found vertex (if any) based on termination and the back links for all processed vertices
   */
  public static <V extends DirectedVertex,ES extends EdgeSegment> Pair<V, Map<V, ES>> breadthFirstSearch(
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
