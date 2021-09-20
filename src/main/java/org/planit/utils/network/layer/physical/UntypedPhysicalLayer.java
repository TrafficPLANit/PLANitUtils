package org.planit.utils.network.layer.physical;

import org.planit.utils.graph.GraphEntities;
import org.planit.utils.network.layer.UntypedDirectedGraphLayer;
import org.planit.utils.network.layer.physical.UntypedPhysicalLayer;

/**
 * Physical topological Network consisting of nodes, links and link segments 
 *
 * @author markr
 */
public interface UntypedPhysicalLayer<N extends Node, L extends Link, LS extends LinkSegment> extends UntypedDirectedGraphLayer<N, L, LS> {

  /**
   * Collect the links
   * 
   * @return the links
   */
  public abstract GraphEntities<L> getLinks();

  /**
   * Collect the link segments
   * 
   * @return the linkSegments
   */
  public abstract GraphEntities<LS> getLinkSegments();

  /**
   * Collect the nodes
   * 
   * @return the nodes
   */
  public abstract GraphEntities<N> getNodes();

  /**
   * Number of nodes
   * 
   * @return number of nodes
   */
  public default long getNumberOfNodes() {
    return getNodes().size();
  }

  /**
   * Number of links
   * 
   * @return number of links
   */
  public default long getNumberOfLinks() {
    return getLinks().size();
  }

  /**
   * Number of link segments
   * 
   * @return number of link segments
   */
  public default long getNumberOfLinkSegments() {
    return getLinkSegments().size();
  }
}
