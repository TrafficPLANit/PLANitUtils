package org.planit.utils.graph;

/**
 * A directed subgraph interface for a given parent graph by registering edge segments on it (and therefore vertices and edges)
 * 
 * @author markr
 *
 * @param <V> vertex type
 * @param <E> edge type
 * @param <ES> edge segment type
 */
public interface DirectedSubGraph<V extends DirectedVertices, E extends DirectedEdges, ES extends EdgeSegments> {
  
  /**
   * collect the id of this subgraph
   * 
   * @return sub graph id
   */
  public long getId();

  /** The parent directed graph
   * 
   * @return parent graph
   */
  public DirectedGraph<V,E,ES> getParentGraph();
  
  /** collect the root vertex
   * @return root vertex
   */
  public DirectedVertex getRootVertex();
  
  /** register an edge segment on the subgraph
   * 
   * @param edgeSegment to add
   * @return true when successful, false otherwise (for example when the edge segment is not present on the parent graph)
   */
  public boolean addEdgeSegment(EdgeSegment edgeSegment);
  
}
