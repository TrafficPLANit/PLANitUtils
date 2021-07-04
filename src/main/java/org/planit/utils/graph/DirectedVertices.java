package org.planit.utils.graph;

import java.util.logging.Logger;

/**
 * Container class for directed vertices and creating instances within this container via factory.
 * 
 * @author markr
 */
public interface DirectedVertices extends GraphEntities<DirectedVertex> {
  /* do not derive from Vertices<V> since we require to override the factory method return type. This is only
   * allowed when the return type directly derives from the original return type. DirectedVertexFactory cannot
   * derive from VertexFactory since the signature of the factory methods differs. Hence, we must derive from
   * the base interface instead which has an empty dummy factory return type which one can always overwrite and
   * the DirectedVertexFactory is derived from */
  
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
