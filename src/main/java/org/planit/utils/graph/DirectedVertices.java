package org.planit.utils.graph;

import java.util.logging.Logger;

/**
 * Container class for directed vertices and creating instances within this container via factory.
 * 
 * @author markr
 */
public interface DirectedVertices extends GraphEntities<DirectedVertex> {
      
  /**
   * Collect the vertex factory to use for creating instances
   * 
   * @return vertexFactory to create edges for this container
   */
  @Override
  public default DirectedVertexFactory getFactory(){
    /** override to change return type signature on interface, implementation must still
     * implement this method to provide access to an actual instance */
    Logger.getLogger(DirectedVertices.class.getCanonicalName()).warning("getFactory not implemented yet for directed vertices implementation");
    return null;
  }  
  
  /**
   * clone vertices
   */
  @Override
  public abstract DirectedVertices clone();  
  
}
