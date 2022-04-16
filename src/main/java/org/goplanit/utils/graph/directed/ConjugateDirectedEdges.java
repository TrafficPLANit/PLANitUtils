package org.goplanit.utils.graph.directed;

import java.util.logging.Logger;

import org.goplanit.utils.graph.GraphEntities;

/**
 * Container and managing class for conjugate directed edges in a conjugate graph.
 * 
 * @author markr
 */
public interface ConjugateDirectedEdges extends GraphEntities<ConjugateDirectedEdge> {
  /* do not derive from ConjugateEdges<E> since we require to override the factory method return type. This is only
   * allowed when the return type directly derives from the original return type. DirectedEdgeFactory cannot
   * derive from EdgeFactory since the signature of the factory methods differs. Hence, we must derive from
   * the base interface instead which has an empty dummy factory return type which one can always overwrite and
   * the DirectedEdgeFactory is derived from */
  
  /**
   * Collect the conjugate directed edge factory to use for creating instances
   * 
   * @return conjugate directed edge factory to create conjugate edges for this container
   */
  @Override
  public default ConjugateDirectedEdgeFactory getFactory(){
    /** override to change return type signature on interface, implementation must still
     * implement this method to provide access to an actual instance */
    Logger.getLogger(ConjugateDirectedEdges.class.getCanonicalName()).warning("getFactory not implemented yet for conjugate directed edges implementation");
    return null;
  }  
}
