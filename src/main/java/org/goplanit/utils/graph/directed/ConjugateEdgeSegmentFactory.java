package org.goplanit.utils.graph.directed;

import org.goplanit.utils.graph.GraphEntityFactory;

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
  public abstract ConjugateEdgeSegment registerNew(final ConjugateDirectedEdge parentEdge, final boolean directionAb, final boolean registerOnVertexAndEdge);
    
}
