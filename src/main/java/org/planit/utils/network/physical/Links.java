package org.planit.utils.network.physical;

import java.util.Collection;

import org.planit.utils.exceptions.PlanItException;
import org.planit.utils.graph.Edges;
import org.planit.utils.graph.Vertex;

/**
 * wrapper around edges interface to support Links explicitly rather than edges
 * 
 * @author markr
 *
 * @param <L> link type
 */
public interface Links<L extends Link> extends Edges<L> {
  
  /**
   * Create new edge to graph identified via its id, (not registered on vertices)
   *
   * @param vertexA the first vertex of this edge
   * @param vertexB the second vertex of this edge
   * @param lengthKm length of the link in km
   * @return the created edge
   * @throws PlanItException thrown if there is an error
   */
  default public L registerNew(final Vertex vertexA, final Vertex vertexB, double lengthKm) throws PlanItException{
    return registerNew(vertexA, vertexB, lengthKm, false);
  }
  
  /**
   * Create new edge to network identified via its id, allow to be registered on vertices if indicated)
   *
   * @param vertexA           the first vertex in this edge
   * @param vertexB           the second vertex in this edge
   * @param lengthKm length of the link in km
   * @param registerOnVertices choice to register new edge on the vertices or not
   * @return the created edge
   * @throws PlanItException thrown if there is an error
   */
  public abstract L registerNew(final Vertex vertexA, final Vertex vertexB, double lengthKm, boolean registerOnVertices) throws PlanItException;   

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
  @SuppressWarnings("unchecked")
  public default Collection<? extends L> getByExternalId(String externalId){
    return (Collection<L>) Edges.super.getByExternalId(externalId);
  }

}
