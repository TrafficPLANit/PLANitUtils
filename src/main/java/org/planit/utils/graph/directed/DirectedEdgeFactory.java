package org.planit.utils.graph.directed;

import org.planit.utils.exceptions.PlanItException;
import org.planit.utils.graph.GraphEntityFactory;

/** Factory interface for creating directed edges.
 * 
 * @author markr
 *
 * @param <E> type of directed edges
 */
public interface DirectedEdgeFactory extends GraphEntityFactory<DirectedEdge> {

  /**
   * Create and register new directed edge to graph identified via its id, (not registered on vertices)
   *
   * @param vertexA the first vertex of this edge
   * @param vertexB the second vertex of this edge
   * @return the created edge
   * @throws PlanItException thrown if there is an error
   */
  public default DirectedEdge registerNew(final DirectedVertex vertexA, final DirectedVertex vertexB) throws PlanItException{
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
  public abstract DirectedEdge registerNew(final DirectedVertex vertexA, final DirectedVertex DirectedVertex, boolean registerOnVertices) throws PlanItException; 

  
}
