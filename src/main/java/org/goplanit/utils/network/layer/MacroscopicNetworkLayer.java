package org.goplanit.utils.network.layer;

import org.goplanit.utils.network.layer.macroscopic.MacroscopicLinkSegment;
import org.goplanit.utils.network.layer.macroscopic.MacroscopicLinkSegmentTypes;
import org.goplanit.utils.network.layer.macroscopic.MacroscopicLinkSegments;
import org.goplanit.utils.network.layer.physical.Link;
import org.goplanit.utils.network.layer.physical.Links;
import org.goplanit.utils.network.layer.physical.Node;
import org.goplanit.utils.network.layer.physical.Nodes;
import org.goplanit.utils.network.layer.physical.UntypedPhysicalLayer;

/**
 * Macroscopic physical network layer consisting of nodes, links and macroscopic link segments 
 *
 * @author markr
 */
public interface MacroscopicNetworkLayer extends UntypedPhysicalLayer<Node, Link, MacroscopicLinkSegment> {
  
  /**
   * Collect the links
   * 
   * @return the links
   */
  @Override
  public abstract Links getLinks();

  /**
   * Collect the link segments
   * 
   * @return the linkSegments
   */
  @Override
  public abstract MacroscopicLinkSegments getLinkSegments();

  /**
   * Collect the nodes
   * 
   * @return the nodes
   */
  @Override
  public abstract Nodes getNodes();  
    
  /**
   * Provide access to registered macroscopic link segment types used across all macroscopic link segments
   * 
   * @return link segment types container class
   */
  public abstract MacroscopicLinkSegmentTypes getLinkSegmentTypes();

}
