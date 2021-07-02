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
public interface DirectedSubGraph<DirectedVertices, DirectedEdges, EdgeSegments> {
  
  /**
   * collect the id of this subgraph
   * 
   * @return sub graph id
   */
  public long getId();

  /** the parent graph
   * @return parent graph
   */
  public UntypedDirectedGraph<DirectedVertices,DirectedEdges,EdgeSegments> getParentGraph();
  
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
