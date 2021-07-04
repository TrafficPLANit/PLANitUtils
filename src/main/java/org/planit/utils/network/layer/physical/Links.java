package org.planit.utils.network.layer.physical;

import java.util.logging.Logger;

import org.planit.utils.graph.GraphEntities;

/**
 * Container class for links with access to factory capable of creating new links and registering them on the container
 * directly
 * 
 * @author markr
 *
 */
public interface Links extends GraphEntities<Link> {
  /* do not derive from DirectedEdges<E> since we require to override the factory method return type. This is only
   * allowed when the return type directly derives from the original return type. LinkFactory cannot
   * derive from DirectedEdgeFactory since the signature of the factory methods differs. Hence, we must derive from
   * the base interface instead which has an empty dummy factory return type which one can always overwrite and
   * the LinkFactory is derived from */
  
  /**
   * {@inheritDoc}
   */
  @Override
  public default LinkFactory getFactory() {
    /** override to change return type signature on interface, implementation must still
     * implement this method to provide access to an actual instance */
    Logger.getLogger(Links.class.getCanonicalName()).warning("getFactory not implemented yet for links implementation");
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract Links clone();

  /**
   * verify if link is present
   * 
   * @param id to check
   * @return true when present false otherwise
   */
  public default boolean hasLink(long id) {
    return contains(id);
  }

}
