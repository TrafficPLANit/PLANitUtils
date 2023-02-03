package org.goplanit.utils.network.layer.physical;

import org.goplanit.utils.graph.GraphEntities;
import org.goplanit.utils.mode.Mode;
import org.goplanit.utils.network.layer.UntypedDirectedGraphLayer;
import org.goplanit.utils.network.layer.physical.UntypedPhysicalLayer;

import java.util.Collection;

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
   * {@inheritDoc}
   */
  @Override
  public abstract UntypedPhysicalLayer shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract UntypedPhysicalLayer deepClone();

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
