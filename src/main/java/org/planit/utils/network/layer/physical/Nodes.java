package org.planit.utils.network.layer.physical;

import java.util.logging.Logger;

import org.planit.utils.graph.GraphEntities;

/**
 * Container class for nodes and access to factory to create and register them on this instance
 * 
 * @author markr
 *
 */
public interface Nodes extends GraphEntities<Node> {
  /* do not derive from DirectedVertices<E> since we require to override the factory method return type. This is only
   * allowed when the return type directly derives from the original return type. NodeFactory cannot
   * derive from DirectedVertexFactory since the signature of the factory methods differs. Hence, we must derive from
   * the base interface instead which has an empty dummy factory return type which one can always overwrite and
   * the NodeFactory is derived from */
  
  /**
   * {@inheritDoc}
   */
  @Override
  public default NodeFactory getFactory() {
    /** override to change return type signature on interface, implementation must still
     * implement this method to provide access to an actual instance */
    Logger.getLogger(Nodes.class.getCanonicalName()).warning("getFactory not implemented yet for nodes implementation");
    return null;
  } 
  
}
