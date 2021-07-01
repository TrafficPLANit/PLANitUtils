package org.planit.utils.graph;

import java.util.logging.Logger;

/**
 * Container and managing class for directed edges in a graph.
 * 
 * @author markr
 */
public interface DirectedEdges<E extends DirectedEdge> extends GraphEntities<E> {
  /* do not derive from Edges<E> since we require to override the factory method return type. This is only
   * allowed when the return type directly derives from the original return type. DirectedEdgeFactory cannot
   * derive from EdgeFactory since the signature of the factory methods differs. Hence, we must derive from
   * the base interface instead which has an empty dummy factory return type which one can always overwrite and
   * the DirectedEdgeFactory is derived from */
  
  /**
   * Collect the directed edge factory to use for creating instances
   * 
   * @return directedEdgeFactory to create edges for this container
   */
  @Override
  public default DirectedEdgeFactory<? extends E> getFactory(){
    /** override to change return type signature on interface, implementation must still
     * implement this method to provide access to an actual instance */
    Logger.getLogger(DirectedEdges.class.getCanonicalName()).warning("getFactory not implemented yet for directed edges implementation");
    return null;
  }  
}
