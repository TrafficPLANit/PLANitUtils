package org.planit.utils.graph;

import org.planit.utils.graph.Edge;
import org.planit.utils.graph.Edges;
import org.planit.utils.graph.Vertex;
import org.planit.utils.graph.Vertices;

/**
 * 
 * A graph interface consisting of vertices and edges
 * 
 * @author markr
 *
 */
public interface Graph<V extends Vertex, E extends Edge> {

  /**
   * collect the id of this graph
   * 
   * @return graph id
   */
  public long getId();

  /** collect vertices of graph
   * @return vertices
   */
  public Vertices<V> getVertices();
  
  /** collect edges of graph
   * @return edges
   */
  public Edges<E> getEdges();

}
