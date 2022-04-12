package org.goplanit.utils.graph.directed;

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
   * @return number of vertices
   */
  public abstract long getNumberOfVertices();  
  
  /** collect the number of exit or entry edgesegments that are present in the subgraph for the given vertex on the parent graph
   *  
   * @param vertex to verify
   * @param exitSegments flag, when true check exit segments, when false check entry segments
   * @return number of subgraph entry or exit edge segments
   */
  public default int getNumberOfEdgeSegments(DirectedVertex vertex, boolean exitSegments) {
    var segments = exitSegments ? vertex.getExitEdgeSegments() : vertex.getEntryEdgeSegments();
    int numSubGraphVertexSegments = 0;
    for(var segment : segments) {
      if(containsEdgeSegment(segment)) {
        ++numSubGraphVertexSegments;
      }
    }
    return numSubGraphVertexSegments;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public abstract DirectedSubGraph clone();  
  
}
