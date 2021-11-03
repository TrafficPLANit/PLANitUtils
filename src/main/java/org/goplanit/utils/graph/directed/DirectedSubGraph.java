package org.goplanit.utils.graph.directed;

import org.goplanit.utils.graph.EdgeSegment;
import org.goplanit.utils.id.IdAble;

/**
 * A directed subgraph interface for a given parent graph by registering edge segments on it (and therefore vertices and edges)
 * 
 * @author markr
 *
 */
public interface DirectedSubGraph extends IdAble {
    
  /** Register an edge segment on the subgraph
   * 
   * @param edgeSegment to add
   */
  public abstract void addEdgeSegment(EdgeSegment edgeSegment);
  
  /** Remove an edge segment on the subgraph
   * 
   * @param edgeSegment to remove
   */
  public abstract void removeEdgeSegment(EdgeSegment edgeSegment);  
  
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
  
  /**
   * {@inheritDoc}
   */
  @Override
  public abstract DirectedSubGraph clone();  
  
}
