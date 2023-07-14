package org.goplanit.utils.graph;

import org.goplanit.utils.exceptions.PlanItException;

/** Factory interface for creating conjugate instances
 * 
 * @author markr
 *
 */
public interface ConjugateEdgeFactory extends GraphEntityFactory<ConjugateEdge> {

  /**
   * Create new edge to graph identified via its id, (not registered on vertices)
   *
   * @param vertexA the first conjugate vertex of this conjugate edge
   * @param vertexB the second conjugate vertex of this conjugate edge
   * @param originalEdge this conjugate represents
   * @return the created edge
   * @throws PlanItException thrown if there is an error
   */
  public default ConjugateEdge registerNew(final ConjugateVertex vertexA, final ConjugateVertex vertexB, final Edge originalEdge) throws PlanItException{
    return registerNew(vertexA, vertexB, originalEdge, false);
  }
  
  /**
   * Create new conjugate edge to network identified via its id, allow to be registered on conjugate vertices if indicated)
   *
   * @param vertexA           the first conjugate vertex in this conjugate edge
   * @param vertexB           the second conjugate vertex in this conjugate edge
   * @param originalEdge this conjugate represents
   * @param registerOnVertices choice to register new conjugate edge on the conjugate vertices or not
   * @return the created edge
   * @throws PlanItException thrown if there is an error
   */
  public abstract ConjugateEdge registerNew(final ConjugateVertex vertexA, final ConjugateVertex vertexB,final Edge originalEdge, boolean registerOnVertices) throws PlanItException; 
  
}
