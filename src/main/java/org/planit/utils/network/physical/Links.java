package org.planit.utils.network.physical;

import java.util.Collection;

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

  /** Collect all links based on a matching external id. links are not indexed by external id so this is
   *  a very inefficient linear search through all registered links.
   *  
   * @param externalId to match
   * @return found matching links
   */
  public default Collection<L> getByExternalId(String externalId){
    return (Collection<L>) Edges.super.getByExternalId(externalId);
  }

}
