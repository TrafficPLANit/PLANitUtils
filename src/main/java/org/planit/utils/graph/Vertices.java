package org.planit.utils.graph;

import org.planit.utils.wrapper.LongMapWrapper;

/**
 * Container and factory class for vertices in a graph, also to be used to create and register vertices of any
 * (derived) type
 * 
 * @author markr
 */
public interface Vertices<V extends Vertex> extends LongMapWrapper<V> {
    
  /** Create a new vertex (without registering on this class)
   * 
   * @return created vertex
   */
  public abstract V createNew();
  
  /**
   * Create and register new entity
   *
   * @return new node created
   */
  public abstract V registerNew();
  
}
