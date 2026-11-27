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
   * @param original1 first of adjacent originals representing this conjugate
   * @param original2 second of adjacent originals representing this conjugate
   * @return the created edge
   */
  public default ConjugateDirectedEdge registerNew(
          final ConjugateDirectedVertex vertexA,
          final ConjugateDirectedVertex vertexB,
          final EdgeSegment original1,
          final EdgeSegment original2){
    return registerNew(vertexA, vertexB, false, original1, original2);
  }
  
  /**
   * Create new edge to network identified via its id, allow to be registered on vertices if indicated)
   *
   * @param vertexA           the first vertex in this edge
   * @param vertexB           the second vertex in this edge
   * @param original1 first of adjacent originals representing this conjugate
   * @param original2 second of adjacent originals representing this conjugate
   * @param registerOnVertices choice to register new edge on the vertices or not
   * @return the created edge
   */
  public abstract ConjugateDirectedEdge registerNew(
          final ConjugateDirectedVertex vertexA,
          final ConjugateDirectedVertex vertexB,
          boolean registerOnVertices,
          final EdgeSegment original1,
          final EdgeSegment original2);

  /**
   * Create new edge to network identified via its id, allow to be registered on vertices if indicated)
   *
   * @param vertexA           the first vertex in this edge
   * @param vertexB           the second vertex in this edge
   * @param registerOnVertices choice to register new edge on the vertices or not
   * @param original1 first of adjacent originals representing this conjugate
   * @param original2 second of adjacent originals representing this conjugate
   * @param deriveXmlIdFromOriginalEdges when true use original edge XML ids, otherwise use internal id of conjugates
   * @param xmlIdPostFix to apply
   * @return the created edge
   */
  public abstract ConjugateDirectedEdge registerNew(
          final ConjugateDirectedVertex vertexA,
          final ConjugateDirectedVertex vertexB,
          boolean registerOnVertices,
          final EdgeSegment original1,
          final EdgeSegment original2,
          boolean deriveXmlIdFromOriginalEdges,
          String xmlIdPostFix);

  
}
