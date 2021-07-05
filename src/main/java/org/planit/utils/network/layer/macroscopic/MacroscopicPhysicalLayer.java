package org.planit.utils.network.layer.macroscopic;

import org.planit.utils.network.layer.physical.Links;
import org.planit.utils.network.layer.physical.Nodes;
import org.planit.utils.network.layer.physical.UntypedPhysicalLayer;

/**
 * Macroscopic physical network layer consisting of nodes, links and macroscopic link segments 
 *
 * @author markr
 */
public interface MacroscopicPhysicalLayer extends UntypedPhysicalLayer<Nodes, Links, MacroscopicLinkSegments> {


}
