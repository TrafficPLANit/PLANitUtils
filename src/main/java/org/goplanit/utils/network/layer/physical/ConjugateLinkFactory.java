package org.goplanit.utils.network.layer.physical;

import org.goplanit.utils.graph.GraphEntityFactory;

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
   * @param originalLink1   first of two links this conjugate link represents
   * @param originalLink2   second of two links this conjugate link represents
   * @return the created undirected turn, i.e. conjugated directed edge
   */
  public abstract ConjugateLink registerNew(final ConjugateNode a, final ConjugateNode b, boolean registerOnNodes, final Link originalLink1, final Link originalLink2);     
}
