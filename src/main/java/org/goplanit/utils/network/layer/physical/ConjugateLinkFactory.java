package org.goplanit.utils.network.layer.physical;

import org.goplanit.utils.graph.Edge;
import org.goplanit.utils.graph.GraphEntityFactory;
import org.goplanit.utils.graph.directed.ConjugateDirectedVertex;
import org.goplanit.utils.graph.directed.DirectedEdge;

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
   * @param originalEdge1   first of two edges this conjugate link represents (edge because it may attach to virtual network)
   * @param originalEdge2   second of two edges this conjugate link represents (edge because it may attach to virtual network)
   * @return the created undirected turn, i.e. conjugated directed edge
   */
  public abstract ConjugateLink registerNew(
          final ConjugateDirectedVertex a,
          final ConjugateDirectedVertex b,
          boolean registerOnNodes,
          final DirectedEdge originalEdge1, final DirectedEdge originalEdge2);
}
