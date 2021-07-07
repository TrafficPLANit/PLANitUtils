package org.planit.utils.network.layer.macroscopic;

import org.planit.utils.network.layer.physical.Links;
import org.planit.utils.network.layer.physical.Nodes;
import org.planit.utils.network.layer.physical.UntypedPhysicalLayer;

/**
 * Macroscopic physical network layer consisting of nodes, links and macroscopic link segments 
 *
 * @author markr
 */
public interface MacroscopicNetworkLayer extends UntypedPhysicalLayer<Nodes, Links, MacroscopicLinkSegments> {

  /**
   * Provide access to registered macroscopic link segment types used across all macroscopic link segments
   * 
   * @return link segment types container class
   */
  public abstract MacroscopicLinkSegmentTypes getLinkSegmentTypes();

}
