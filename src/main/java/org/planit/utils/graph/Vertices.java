package org.planit.utils.graph;

/**
 * Container and factory class for vertices in a graph, also to be used to create and register vertices of any
 * (derived) type
 * 
 * @author markr
 */
public interface Vertices<V extends Vertex> extends Iterable<V> {
  
  /** Remove a vertex
   * 
   * @param vertex to remove
   */
  public void remove(V vertex);
  
  /** Remove a vertex by id
   * 
   * @param vertexId to remove vertex for
   */  
  public void remove(long vertexId);  

  
  /** Create a new vertex (without registering on this class)
   * 
   * @return created vertex
   */
  public V createNew();
  
  /**
   * Add vertex to the internal container
   *
   * @param vertex vertex to be registered in this network
   * @return vertex, in case it overrides an existing vertex, the removed vertex is returned
   */
  public V register(V vertex);  

  /**
   * Create and register new vertex
   *
   * @return new node created
   */
  public V registerNew();

  /**
   * Create and register new vertex
   *
   * @param externalId the externalId of the vertex
   * @return new vertex created
   */
  public V registerNew(Object externalId);

  /**
   * Return number of registered vertices
   *
   * @return number of registered vertices
   */
  public int size();
  
  /** When there are no vertices the nodes are considered empty
   * 
   * @return true when no vertices exist yet, false otherwise
   */
  default public boolean isEmpty() {
    return !(size() > 0);
  }

  /**
   * Find a vertex by its d
   *
   * @param id Id of vertex
   * @return retrieved vertex
   */
  public V get(final long id);





  
}
