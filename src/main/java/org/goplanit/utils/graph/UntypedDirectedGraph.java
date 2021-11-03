package org.goplanit.utils.graph;

import org.goplanit.utils.graph.directed.DirectedEdge;
import org.goplanit.utils.graph.directed.DirectedVertex;

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

}
