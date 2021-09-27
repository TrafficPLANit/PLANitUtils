package org.planit.utils.graph.directed;

import org.planit.utils.graph.EdgeSegment;

/**
 * A directed subgraph interface for a given parent graph by registering edge segments on it (and therefore vertices and edges)
 * 
 * @author markr
 *
 * @param <V> vertex type
 * @param <E> edge type
 * @param <ES> edge segment type
 */
public interface DirectedSubGraph<V extends DirectedVertex, E extends DirectedEdge, ES extends EdgeSegment> {
  
  /**
   * collect the id of this subgraph
   * 
   * @return sub graph id
   */
  public abstract long getId(); 
  
  /** register an edge segment on the subgraph
   * 
   * @param edgeSegment to add
   */
  public abstract void addEdgeSegment(EdgeSegment edgeSegment);
  
  /** Verify if given edge segment is registered on this subgraph
   * 
   * @param edgeSegment to verify
   * @return true when registered, false otherwise
   */
  public abstract boolean containsEdgeSegment(EdgeSegment edgeSegment);
  
  /**
   * Based on the registered edge segments, the number of vertices is automatically determined. This method provides the number of vertices corresponding to these registered edge
   * segments
   * 
   * @return
   */
  public abstract long getNumberOfVertices();  
  
}
