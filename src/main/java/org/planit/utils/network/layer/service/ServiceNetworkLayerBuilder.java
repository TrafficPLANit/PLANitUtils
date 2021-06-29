package org.planit.utils.network.layer.service;

import org.planit.utils.graph.DirectedGraphBuilder;

/**
 * Builder for service networks of given parameterised types. 
 * @author markr
 *
 */
public interface ServiceNetworkLayerBuilder extends DirectedGraphBuilder<ServiceNode, ServiceLeg, ServiceLegSegment>  {
  
}
