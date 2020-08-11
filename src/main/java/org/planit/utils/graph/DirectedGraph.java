package org.planit.utils.graph;

/**
 * Interface for an undirected graph
 * 
 * @author markr
 */
public interface DirectedGraph {

  Edges getEdges();
  
  EdgeSegments getEdgeSegments();  

  Vertices getVertices();

}
