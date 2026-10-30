package org.goplanit.utils.graph.directed;

import org.goplanit.utils.graph.GraphEntityFactory;
import org.goplanit.utils.network.layer.physical.ConjugateLink;
import org.goplanit.utils.network.layer.physical.ConjugateLinkSegment;

/** Factory interface for creating conjugate edge segment instances
 * 
 * @author markr
 *
 */
public interface ConjugateEdgeSegmentFactory extends GraphEntityFactory<ConjugateEdgeSegment> {

  /**
   * Create edge segment
   *
   * @param parentEdge  the parent edge of this edge segment
   * @param directionAB direction of travel
   * @return the created edge segment
   */
  public abstract ConjugateEdgeSegment create(final ConjugateDirectedEdge parentEdge, final boolean directionAB);
  
  /**
   * Create directional edge segment and register it
   *
   * @param parentEdge            the parent edge of this edge segment
   * @param directionAb           direction of travel
   * @param registerOnVertexAndEdge option to register the new edge segment on the underlying edge and its vertices
   * @return the created edge segment
   */
  public abstract ConjugateEdgeSegment registerNew(
          final ConjugateDirectedEdge parentEdge, final boolean directionAb, final boolean registerOnVertexAndEdge);

  /**
   * same as {@link #registerNew(ConjugateDirectedEdge, boolean, boolean)} only now also populate XmlId based
   * on configuration provided.
   *
   * @param parent            the parent of this conjugate segment
   * @param directionAb           direction of travel
   * @param registerOnVertexAndEdge option to register the new conjugate segment on the underlying conjugate edge
   *                              and its conjugate vertices
   * @param deriveXmlIdFromOriginalEdges when true use original edge XML ids, otherwise use internal id of conjugates
   * @param xmlIdPostFix to apply
   * @return the created segment
   */
  public ConjugateEdgeSegment registerNew(
          ConjugateDirectedEdge parent,
          boolean directionAb,
          boolean registerOnVertexAndEdge,
          boolean deriveXmlIdFromOriginalEdges,
          String xmlIdPostFix);
    
}
