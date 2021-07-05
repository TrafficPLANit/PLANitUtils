package org.planit.utils.network.layer.physical;

import org.planit.utils.network.layer.physical.LinkSegments;
import org.planit.utils.network.layer.physical.Links;
import org.planit.utils.network.layer.physical.Nodes;
import org.planit.utils.network.layer.physical.PhysicalLayer;

/**
 * Physical topological Network consisting of nodes, links and link segments 
 *
 * @author markr
 */
public interface PhysicalLayer extends UntypedPhysicalLayer<Nodes, Links, LinkSegments> {


}
