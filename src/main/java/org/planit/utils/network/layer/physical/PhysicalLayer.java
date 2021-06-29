package org.planit.utils.network.layer.physical;

import org.planit.utils.network.layer.TopologicalLayer;
import org.planit.utils.network.layer.physical.Link;
import org.planit.utils.network.layer.physical.LinkSegment;
import org.planit.utils.network.layer.physical.LinkSegments;
import org.planit.utils.network.layer.physical.Links;
import org.planit.utils.network.layer.physical.Node;
import org.planit.utils.network.layer.physical.Nodes;
import org.planit.utils.network.layer.physical.PhysicalLayer;

/**
 * Physical topological Network consisting of nodes, links and link segments.
 *
 * @author markr
 */
public interface PhysicalLayer<N extends Node, L extends Link, LS extends LinkSegment> extends TopologicalLayer {

  /**
   * Collect the links
   * 
   * @return the links
   */
  public abstract Links<L> getLinks();

  /**
   * Collect the link segments
   * 
   * @return the linkSegments
   */
  public abstract LinkSegments<LS> getLinkSegments();

  /**
   * Collect the nodes
   * 
   * @return the nodes
   */
  public abstract Nodes<N> getNodes();
  
  /**
   * Number of nodes
   * 
   * @return number of nodes
   */
  public abstract long getNumberOfNodes();

  /**
   * Number of links
   * 
   * @return number of links
   */
  public abstract long getNumberOfLinks();

  /**
   * Number of link segments
   * 
   * @return number of link segments
   */
  public abstract long getNumberOfLinkSegments();  

}
