package org.planit.utils.graph;

import org.planit.utils.exceptions.PlanItException;

/** Factory interface for creating edge instances
 * 
 * @author markr
 *
 */
public interface EdgeFactory extends GraphEntityFactory<Edge> {

  /**
   * Create new edge to graph identified via its id, (not registered on vertices)
   *
   * @param vertexA the first vertex of this edge
   * @param vertexB the second vertex of this edge
   * @return the created edge
   * @throws PlanItException thrown if there is an error
   */
  public default Edge registerNew(final Vertex vertexA, final Vertex vertexB) throws PlanItException{
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
  public abstract Edge registerNew(final Vertex vertexA, final Vertex vertexB, boolean registerOnVertices) throws PlanItException; 
  
}
