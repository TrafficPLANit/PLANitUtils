package org.planit.utils.graph;

import org.planit.utils.exceptions.PlanItException;

/** Factory interface for creating edge instances
 * 
 * @author markr
 *
 * @param <E> type of edge
 */
public interface EdgeFactory<E extends Edge> extends GraphEntityFactory<E> {

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
  
}
