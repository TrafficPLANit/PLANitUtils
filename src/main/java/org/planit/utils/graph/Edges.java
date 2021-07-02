package org.planit.utils.graph;

import java.util.logging.Logger;

/**
 * Container and factory class for edges in a graph, also to be used to create and register edges of any
 * (derived) type
 * 
 * @author markr
 */
public interface Edges extends GraphEntities<Edge>{       
  
  /**
   * Collect the edge factory to use for creating instances
   * 
   * @return edgeFactory to create edges for this container
   */
  @Override
  public default EdgeFactory getFactory(){
    /** override to change return type signature on interface, implementation must still
     * implement this method to provide access to an actual instance */
    Logger.getLogger(Edges.class.getCanonicalName()).warning("getFactory not implemented yet for edges implementation");
    return null;
  }
  
  /**
   * clone edges
   */
  @Override
  public abstract Edges clone();

}
