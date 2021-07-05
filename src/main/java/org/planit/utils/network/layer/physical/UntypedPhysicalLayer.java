package org.planit.utils.network.layer.physical;

import org.planit.utils.graph.GraphEntities;
import org.planit.utils.network.layer.TopologicalLayer;
import org.planit.utils.network.layer.physical.UntypedPhysicalLayer;

/**
 * Physical topological Network consisting of containers with derived classes of Node, Link and LinkSegment. By using 
 * graphEntities instead of typed container classes such as Nodes, Links, LinkSegments, it can be used by other interfaces
 * without being restricted to using derived classes of typed containers. 
 *
 * @author markr
 */
public interface UntypedPhysicalLayer<N extends GraphEntities<? extends Node>, L extends GraphEntities<? extends Link>, LS extends GraphEntities<? extends LinkSegment> > extends TopologicalLayer {

  /**
   * Collect the links
   * 
   * @return the links
   */
  public abstract L getLinks();

  /**
   * Collect the link segments
   * 
   * @return the linkSegments
   */
  public abstract LS getLinkSegments();

  /**
   * Collect the nodes
   * 
   * @return the nodes
   */
  public abstract N getNodes();
  
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
