package org.planit.utils.graph;

import org.planit.utils.exceptions.PlanItException;

/**
 * Container and factory class for edges in a graph, also to be used to create and register edges of any
 * (derived) type
 * 
 * @author markr
 */
public interface Edges<E extends Edge> extends Iterable<E> {


  /**
   * Create new edge to graph identified via its id
   *
   * @param vertexA the first vertex of this link
   * @param vertexB the second vertex of this link
   * @param length  the length (in km)
   * @return the created edge
   * @throws PlanItException thrown if there is an error
   */
  public E registerNewEdge(final Vertex vertexA, final Vertex vertexB, final double length) throws PlanItException;

  /**
   * Get edge by id
   *
   * @param id the id of the edge
   * @return the retrieved edge
   */
  public E getEdge(final long id);

  /**
   * Get the number of edges in the graph
   *
   * @return the number of edges in the graph
   */
  public int getNumberOfEdges();
}
