package org.planit.utils.graph;

/**
 * Interface for an undirected graph
 * 
 * @author markr
 */
public interface DirectedGraph<V extends DirectedVertex, E extends DirectedEdge, ES extends EdgeSegment> extends Graph<V,E> {


  /** Collect edges segments of graph
   * @return edges segments
   */
  EdgeSegments<ES> getEdgeSegments();

}
