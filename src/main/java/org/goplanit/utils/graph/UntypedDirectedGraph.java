package org.goplanit.utils.graph;

import org.goplanit.utils.graph.directed.DirectedEdge;
import org.goplanit.utils.graph.directed.DirectedGraphUtils;
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
 * Interface for a directed graph
 * 
 * @author markr
 */
public interface UntypedDirectedGraph<V extends DirectedVertex, E extends DirectedEdge, ES extends EdgeSegment> extends UntypedGraph<V,E> {


  /** Collect edges segments of graph
   * @return edges segments
   */
  public abstract GraphEntities<ES> getEdgeSegments();
    
  /** Verify if empty, empty when no nodes, edges, edge segments exist
   * 
   * @return true when no nodes, edges, edge segments,  false otherwise
   */
  @Override
  public default boolean isEmpty() {
    return UntypedGraph.super.isEmpty() && (getEdgeSegments()==null || getEdgeSegments().isEmpty());
  }  

  /**
   * {@inheritDoc}
   */
  @Override
  default boolean validate() {
    boolean isValid = UntypedGraph.super.validate();
    for(ES edgeSegment : getEdgeSegments()) {
      isValid = isValid && edgeSegment.validate();
    }
    return isValid;
  }

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
   * @param vertexSegmentInclusionCondition predicate for general condition on edge segments to consider
   * @param vertexSegmentTerminationCondition predicate for termination condition for successful search completion
   * @return found vertex (if any) absed on termination and the back links for all processed vertices
   */
  public default Pair<V, Map<V, ES>> breadthFirstSearch(
      V startVertex,
      boolean invertDirection,
      Predicate<ES> initialVertexSegmentInclusionCondition,
      BiPredicate<ES, ES> vertexSegmentInclusionCondition,
      BiPredicate<V,ES> vertexSegmentTerminationCondition){

    // delegate
    return DirectedGraphUtils.breadthFirstSearch(
        startVertex,
        invertDirection,
        initialVertexSegmentInclusionCondition,
        vertexSegmentInclusionCondition,
        vertexSegmentTerminationCondition);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract UntypedDirectedGraph<V, E, ES> shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract UntypedDirectedGraph<V, E, ES> deepClone();
}
