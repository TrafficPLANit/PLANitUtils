package org.goplanit.utils.graph.directed;

import org.goplanit.utils.exceptions.PlanItException;
import org.goplanit.utils.graph.GraphEntityFactory;

/** Factory interface for creating conjugate directed edges.
 * 
 * @author markr
 *
 */
public interface ConjugateDirectedEdgeFactory extends GraphEntityFactory<ConjugateDirectedEdge> {

  /**
   * Create and register new conjugate directed edge to graph identified via its id, (not registered on vertices)
   *
   * @param vertexA the first vertex of this edge
   * @param vertexB the second vertex of this edge
   * @param originalEdge1 first of adjacent edges representing this conjugate
   * @param originalEdge2 second of adjacent edges representing this conjugate
   * @return the created edge
   * @throws PlanItException thrown if there is an error
   */
  public default ConjugateDirectedEdge registerNew(final ConjugateDirectedVertex vertexA, final ConjugateDirectedVertex vertexB, final DirectedEdge originalEdge1, final DirectedEdge originalEdge2) throws PlanItException{
    return registerNew(vertexA, vertexB, originalEdge1, originalEdge2, false);
  }
  
  /**
   * Create new edge to network identified via its id, allow to be registered on vertices if indicated)
   *
   * @param vertexA           the first vertex in this edge
   * @param vertexB           the second vertex in this edge
   * @param originalEdge1     first of adjacent edges representing this conjugate
   * @param originalEdge2     second of adjacent edges representing this conjugate
   * @param registerOnVertices choice to register new edge on the vertices or not
   * @return the created edge
   * @throws PlanItException thrown if there is an error
   */
  public abstract ConjugateDirectedEdge registerNew(final ConjugateDirectedVertex vertexA, final ConjugateDirectedVertex vertexB, final DirectedEdge originalEdge1, final DirectedEdge originalEdge2, boolean registerOnVertices) throws PlanItException; 

  
}
