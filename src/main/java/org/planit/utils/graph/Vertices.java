package org.planit.utils.graph;

/**
 * Container and factory class for vertices in a graph, also to be used to create and register vertices of any
 * (derived) type
 * 
 * @author markr
 */
public interface Vertices<V extends Vertex> extends Iterable<V> {
  
  /** Remove
   * 
   * @param entity to remove
   */
  public abstract V remove(V entity);
  
  /** Remove by id
   * 
   * @param id to remove vertex for
   */  
  public abstract V remove(long id);  

  
  /** Create a new vertex (without registering on this class)
   * 
   * @return created vertex
   */
  public abstract V createNew();
  
  /**
   * Add to the container
   *
   * @param entity to be registered in this network
   * @return entity, in case it overrides an existing entry, the removed entryis returned
   */
  public abstract V register(V entity);  

  /**
   * Create and register new entity
   *
   * @return new node created
   */
  public abstract V registerNew();

  /**
   * Return number of registered entity
   *
   * @return number of registered entity
   */
  public abstract int size();
  
  /**
   * Find a entity by its d
   *
   * @param id Id of entity
   * @return retrieved entity
   */
  public abstract V get(final long id);  
  
  /** When there are no vertices the nodes are considered empty
   * 
   * @return true when no vertices exist yet, false otherwise
   */
  default public boolean isEmpty() {
    return !(size() > 0);
  }
  
}
