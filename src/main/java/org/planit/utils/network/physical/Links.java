package org.planit.utils.network.physical;

import org.planit.utils.graph.Edges;

/**
 * wrapper around edges interface to support Links explicitly rather than edges
 * 
 * @author markr
 *
 * @param <L> link type
 */
public interface Links<L extends Link> extends Edges<L> {

  /**
   * verify if link is present
   * 
   * @param id to check
   * @return true when present false otherwise
   */
  public default boolean hasLink(long id) {
    return hasEdge(id);
  } 

}
