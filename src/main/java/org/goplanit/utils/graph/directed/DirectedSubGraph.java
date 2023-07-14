package org.goplanit.utils.graph.directed;

import org.goplanit.utils.id.IdAble;

/**
 * A directed subgraph interface for a given parent graph by registering edge segments on it (and therefore vertices and edges)
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
