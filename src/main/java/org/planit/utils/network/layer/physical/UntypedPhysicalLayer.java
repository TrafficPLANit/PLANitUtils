package org.planit.utils.network.layer.physical;

import org.planit.utils.graph.GraphEntities;
import org.planit.utils.network.layer.UntypedDirectedGraphLayer;
import org.planit.utils.network.layer.physical.UntypedPhysicalLayer;

/**
 * Physical topological Network consisting of nodes, links and link segments 
 *
 * @author markr
 */
public interface UntypedPhysicalLayer<N extends Node, NE extends GraphEntities<N>, L extends Link, LE extends GraphEntities<L>, LS extends LinkSegment, LSE extends GraphEntities<LS>> extends UntypedDirectedGraphLayer<N, NE, L, LE, LS, LSE> {

  /**
   * Collect the links
   * 
   * @return the links
   */
  public abstract LE getLinks();

  /**
   * Collect the link segments
   * 
   * @return the linkSegments
   */
  public abstract LSE getLinkSegments();

  /**
   * Collect the nodes
   * 
   * @return the nodes
   */
  public abstract NE getNodes();

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
