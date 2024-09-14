package org.goplanit.utils.graph.directed;

import org.goplanit.utils.id.IdAble;
import org.goplanit.utils.misc.Pair;

import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * A directed subgraph interface for a given parent graph by registering edge segments on it
 * (and therefore vertices and edges)
 * 
 * @author markr
 *
 */
public interface DirectedSubGraph <V extends DirectedVertex, E extends EdgeSegment> extends IdAble {
    
  /** Register an edge segment on the subgraph
   * 
   * @param edgeSegment to add
   */
  public abstract void addEdgeSegment(E edgeSegment);
  
  /** Remove an edge segment on the subgraph
   * 
   * @param edgeSegment to remove
   */
  public abstract void removeEdgeSegment(E edgeSegment);  
  
  /** Verify if given edge segment is registered on this subgraph
   * 
   * @param edgeSegment to verify
   * @return true when registered, false otherwise
   */
  public abstract boolean containsEdgeSegment(E edgeSegment);

  /**
   * Verify if the bush contains the given edge segment
   *
   * @param edgeSegmentId to verify
   * @return true when present, false otherwise
   */
  public abstract boolean containsEdgeSegment(long edgeSegmentId);
  
  /**
   * The number of registered vertices. This method provides the number of vertices corresponding to these registered edge
   * segments
   * 
   * @return number of vertices
   */
  public abstract long getNumberOfVertices();  
  
  /** collect the number of exit or entry edgesegments that are present in the subgraph for the given vertex on the parent graph
   *  
   * @param vertex to verify
   * @param exitSegments flag, when true check exit segments, when false check entry segments
   * @return number of subgraph entry or exit edge segments
   */
  @SuppressWarnings("unchecked")
  public default int getNumberOfEdgeSegments(V vertex, boolean exitSegments) {
    var segments = exitSegments ? vertex.getExitEdgeSegments() : vertex.getEntryEdgeSegments();
    int numSubGraphVertexSegments = 0;
    for(var segment : segments) {
      if(containsEdgeSegment((E)segment)) {
        ++numSubGraphVertexSegments;
      }
    }
    return numSubGraphVertexSegments;
  }
  
  /** Check if no vertices (and therefore not edge segments are present on this sub graph
   * 
   * @return true when empty, false otherwise
   */
  public default boolean isEmpty() {
    return getNumberOfVertices() <= 0;
  }

  /**
   * identical to {@link #breadthFirstSearch(DirectedVertex, boolean, Predicate, BiPredicate, BiPredicate)}
   * except no conditions are imposed on initial and regular traversal, oly a termination condition exists.
   *
   */
  public default Pair<V, Map<V, E>> breadthFirstSearch(
      V startVertex,
      boolean invertDirection,
      BiPredicate<V,E> vertexSegmentTerminationCondition){
    return breadthFirstSearch(
        startVertex, invertDirection, es -> true, (prevEs,es) -> true, vertexSegmentTerminationCondition);
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
   * @param vertexSegmentInclusionCondition predicate for general condition on edge segments to consider (prevEdgeSegment, currEdgeSegment)
   * @param vertexSegmentTerminationCondition predicate for termination condition for successful search completion
   * @return found vertex (if any) absed on termination and the back links for all processed vertices
   */
  public default Pair<V, Map<V, E>> breadthFirstSearch(
      V startVertex,
      boolean invertDirection,
      Predicate<E> initialVertexSegmentInclusionCondition,
      BiPredicate<E,E> vertexSegmentInclusionCondition,
      BiPredicate<V,E> vertexSegmentTerminationCondition){

    // supplement with condition it must be in the subgraph
    Predicate<E> initialInclusionCondition = es ->
        containsEdgeSegment(es) && initialVertexSegmentInclusionCondition.test(es);

    // supplement with condition it must be in the subgraph
    BiPredicate<E, E> regularInclusionCondition = (prevEs, es) ->
        containsEdgeSegment(es) && vertexSegmentInclusionCondition.test(prevEs, es);

    // delegate
    return DirectedGraphUtils.breadthFirstSearch(
        startVertex,
        invertDirection,
        initialInclusionCondition,
        regularInclusionCondition,
        vertexSegmentTerminationCondition);
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public abstract DirectedSubGraph<V,E> shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract DirectedSubGraph<V,E> deepClone();
  
}
