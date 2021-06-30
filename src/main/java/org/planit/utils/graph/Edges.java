package org.planit.utils.graph;

import java.util.ArrayList;
import java.util.Collection;

import org.planit.utils.exceptions.PlanItException;
import org.planit.utils.wrapper.LongMapWrapper;

/**
 * Container and factory class for edges in a graph, also to be used to create and register edges of any
 * (derived) type
 * 
 * @author markr
 */
public interface Edges<E extends Edge> extends LongMapWrapper<E> {
  
  //TODO REPLACE factory methods by a method to collect the factory or have a separated factory class with the builder??
  
  /**
   * Create new edge to graph identified via its id, (not registered on vertices)
   *
   * @param vertexA the first vertex of this edge
   * @param vertexB the second vertex of this edge
   * @return the created edge
   * @throws PlanItException thrown if there is an error
   */
  public default E registerNew(final Vertex vertexA, final Vertex vertexB) throws PlanItException{
    return registerNew(vertexA, vertexB, false);
  }
  
  /**
   * Create new edge to network identified via its id, allow to be registered on vertices if indicated)
   *
   * @param vertexA           the first vertex in this edge
   * @param vertexB           the second vertex in this edge
   * @param registerOnVertices choice to register new edge on the vertices or not
   * @return the created edge
   * @throws PlanItException thrown if there is an error
   */
  public abstract E registerNew(final Vertex vertexA, final Vertex vertexB, boolean registerOnVertices) throws PlanItException; 
  
  /** copy the passed in edge and register it
   * 
   * @param edgeToCopy as is except for its ids which will be updated to make it uniquely identifiable
   * @return copy of edge now registered
   */
  public abstract E registerUniqueCopyOf(final E edgeToCopy);

  /** Verify if edge is present
   * 
   * @param id to verify
   * @return true when present false otherwise
   */
  public default boolean hasEdge(long id) {
    return get(id) != null;
  }

  /** Collect all edges based on a matching external id. edges are not indexed by external id so this is
   *  a very inefficient linear search through all registered links.
   *  
   * @param externalId to match
   * @return found matching links
   */  
  public default Collection<? extends E> getByExternalId(String externalId) {
    ArrayList<E> matches = new ArrayList<E>(1);  
    for(E edge : this) {
      if(edge.getExternalId().equals(externalId)) {
        matches.add(edge);      }
    }
    return matches;
  }

}
