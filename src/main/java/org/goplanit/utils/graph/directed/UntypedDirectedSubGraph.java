package org.goplanit.utils.graph.directed;

import org.goplanit.utils.graph.Edge;
import org.goplanit.utils.graph.GraphEntities;
import org.goplanit.utils.graph.UntypedGraph;
import org.goplanit.utils.graph.UntypedSubGraph;
import org.goplanit.utils.id.IdAble;
import org.goplanit.utils.misc.Pair;

import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * A directed subgraph interface for a given parent graph by registering edge segments on it
 * 
 * @author markr
 *
 */
public interface UntypedDirectedSubGraph<V extends DirectedVertex, E extends DirectedEdge, ES extends EdgeSegment>
    extends UntypedSubGraph<V,E> {
    
  /** Register an edge segment on the subgraph
   * 
   * @param edgeSegment to add
   */
  public abstract void addEdgeSegment(ES edgeSegment);
  
  /** Remove an edge segment on the subgraph
   * 
   * @param edgeSegment to remove
   */
  public abstract void removeEdgeSegment(ES edgeSegment);
  
  /** Verify if given edge segment is registered on this subgraph
   * 
   * @param edgeSegment to verify
   * @return true when registered, false otherwise
   */
  public abstract boolean containsEdgeSegment(ES edgeSegment);

  /**
   * Verify if the bush contains the given edge segment
   *
   * @param edgeSegmentId to verify
   * @return true when present, false otherwise
   */
  public abstract boolean containsEdgeSegment(long edgeSegmentId);
  

  /** Collect the number of exit or entry edge segments that are present in the subgraph for the given
   * vertex on the parent graph
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
      if(containsEdgeSegment((ES)segment)) {
        ++numSubGraphVertexSegments;
      }
    }
    return numSubGraphVertexSegments;
  }

  /**
   * total number of registered edge segments on subgraph
   * @return total
   */
  public abstract int getNumberOfEdgeSegments();

  /**
   * Construct a new list from all edge segments registered on this subgraph.
   * <p>
   * Implementations tracking membership by id should resolve this by keyed lookup rather than by scanning the
   * parent container, so that the cost is proportional to the subgraph and not to the entire graph. Callers that
   * materialise many subgraphs, e.g. dangling subnetwork removal, otherwise become quadratic in the graph size.
   * </p>
   *
   * @param parentEdgeSegments to collect the registered segments from
   * @return list of edge segments on this subgraph
   */
  @SuppressWarnings("unchecked")
  public default List<ES> getEdgeSegments(GraphEntities<? extends ES> parentEdgeSegments) {
    /* fallback for implementations that do not track membership by id, see note above on cost */
    return parentEdgeSegments.stream().filter(es -> containsEdgeSegment((ES) es))
        .map(es -> (ES) es).collect(java.util.stream.Collectors.toList());
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
   * @param startVertex to start search from
   * @param invertDirection direction for searching, when true invert direction from downstream to upstream
   * @param vertexSegmentTerminationCondition predicate for termination condition for successful search completion
   * @return found vertex (if any) absed on termination and the back links for all processed vertices
   */
  public default Pair<V, Map<V, ES>> breadthFirstSearch(
      V startVertex,
      boolean invertDirection,
      BiPredicate<V,ES> vertexSegmentTerminationCondition){
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
   * @return found vertex (if any) based on termination and the back links for all processed vertices
   */
  public default Pair<V, Map<V, ES>> breadthFirstSearch(
      V startVertex,
      boolean invertDirection,
      Predicate<ES> initialVertexSegmentInclusionCondition,
      BiPredicate<ES,ES> vertexSegmentInclusionCondition,
      BiPredicate<V,ES> vertexSegmentTerminationCondition){

    // supplement with condition it must be in the subgraph
    Predicate<ES> initialInclusionCondition = es ->
        containsEdgeSegment(es) && initialVertexSegmentInclusionCondition.test(es);

    // supplement with condition it must be in the subgraph
    BiPredicate<ES, ES> regularInclusionCondition = (prevEs, es) ->
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
  public abstract UntypedDirectedSubGraph<V,E,ES> shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract UntypedDirectedSubGraph<V,E,ES> deepClone();
  
}
