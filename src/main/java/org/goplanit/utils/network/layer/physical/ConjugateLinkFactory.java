package org.goplanit.utils.network.layer.physical;

import org.goplanit.utils.graph.Edge;
import org.goplanit.utils.graph.GraphEntityFactory;
import org.goplanit.utils.graph.directed.ConjugateDirectedVertex;
import org.goplanit.utils.graph.directed.DirectedEdge;
import org.goplanit.utils.graph.directed.EdgeSegment;

/** Factory interface for creating undirected turns, i.e., conjugate links
 * 
 * @author markr
 *
 */
public interface ConjugateLinkFactory extends GraphEntityFactory<ConjugateLink>{
 
  /**
   * Create new conjugate link on conjugate links container, allow to be registered on conjugate nodes if indicated)
   *
   * @param a               the first conjugate node on this undirected turn  (conjugate directed edge)
   * @param b               the second conjugate node on this undirected turn (conjugate directed edge)
   * @param registerOnNodes choice to register new edge on the conjugate nodes or not
   * @param original1   first of two originals this conjugate link represents
   * @param original2   second of two originals this conjugate link represents
   * @return the created undirected turn, i.e. conjugated directed edge
   */
  public abstract ConjugateLink registerNew(
          final ConjugateDirectedVertex a,
          final ConjugateDirectedVertex b,
          boolean registerOnNodes,
          final EdgeSegment original1,
          final EdgeSegment original2);

  /**
   * Same as {@link #registerNew(ConjugateDirectedVertex, ConjugateDirectedVertex, boolean, EdgeSegment, EdgeSegment)}
   * only now we also populate its XMLId directly based on configuration
   *
   * @param a               the first conjugate node on this undirected turn  (conjugate directed edge)
   * @param b               the second conjugate node on this undirected turn (conjugate directed edge)
   * @param registerOnNodes choice to register new edge on the conjugate nodes or not
   * @param original1   first of two originals this conjugate link represents
   * @param original2   second of two originals this conjugate link represents
   * @param deriveXmlIdFromOriginals when true use original XML ids, otherwise use internal id of conjugates
   * @param xmlIdPostFix to apply
   * @return the created undirected turn, i.e. conjugated directed edge
   */
  public abstract ConjugateLink registerNew(
          final ConjugateDirectedVertex a,
          final ConjugateDirectedVertex b,
          boolean registerOnNodes,
          final EdgeSegment original1,
          final EdgeSegment original2,
          boolean deriveXmlIdFromOriginals,
          String xmlIdPostFix);
}
