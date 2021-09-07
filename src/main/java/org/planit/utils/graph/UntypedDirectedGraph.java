package org.planit.utils.graph;

import org.planit.utils.graph.directed.DirectedEdge;
import org.planit.utils.graph.directed.DirectedVertex;

/**
 * Interface for a directed graph
 * 
 * @author markr
 */
public interface UntypedDirectedGraph<V extends GraphEntities<? extends DirectedVertex>, E extends GraphEntities<? extends DirectedEdge>, ES extends GraphEntities<? extends EdgeSegment>> extends UntypedGraph<V,E> {


  /** Collect edges segments of graph
   * @return edges segments
   */
  ES getEdgeSegments();
    
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
    for(EdgeSegment edgeSegment : getEdgeSegments()) {
      isValid = isValid && edgeSegment.validate();
    }
    return isValid;
  } 
  
  /**
   * {@inheritDoc}
   */
  @Override
  public default void reset() {
    UntypedGraph.super.reset();
    getEdgeSegments().reset();
  }  

}
