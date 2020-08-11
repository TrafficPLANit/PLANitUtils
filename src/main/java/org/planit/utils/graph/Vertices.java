package org.planit.utils.graph;

/**
 * Container and factory class for vertices in a graph, also to be used to create and register vertices of any
 * (derived) type
 * 
 * @author markr
 */
public interface Vertices<V extends Vertex> extends Iterable<V> {


  /**
   * Create and register new vertex
   *
   * @return new node created
   */
  public V registerNewVertex();

  /**
   * Create and register new vertex
   *
   * @param externalId the externalId of the vertex
   * @return new vertex created
   */
  public V registerNewVertex(Object externalId);

  /**
   * Return number of registered vertices
   *
   * @return number of registered vertices
   */
  public int getNumberOfVertices();

  /**
   * Find a vertex by its d
   *
   * @param id Id of vertex
   * @return retrieved vertex
   */
  public V getVertexById(final long id);
}
