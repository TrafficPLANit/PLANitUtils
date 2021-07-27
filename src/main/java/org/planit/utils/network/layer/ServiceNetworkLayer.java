package org.planit.utils.network.layer;

import org.planit.utils.network.layer.service.ServiceLeg;
import org.planit.utils.network.layer.service.ServiceLegSegment;
import org.planit.utils.network.layer.service.ServiceLegSegments;
import org.planit.utils.network.layer.service.ServiceLegs;
import org.planit.utils.network.layer.service.ServiceNode;
import org.planit.utils.network.layer.service.ServiceNodes;

/**
 * Service network layer consisting of service nodes, legs and leg segments on top of a parent network layer with physical entities. Service network layers
 * are used to define service legs between nodes offered by one or more routed services as a separate network layer. 
 *
 * @author markr
 */
public interface ServiceNetworkLayer extends UntypedDirectedGraphLayer<ServiceNode, ServiceNodes, ServiceLeg, ServiceLegs, ServiceLegSegment, ServiceLegSegments> {
  
  /**
   * Collect the parent layer of this service layer
   * 
   * @return the parent layer
   */
  public abstract MacroscopicNetworkLayer getParentNetworkLayer();  
  
  /**
   * Collect the service legs
   * 
   * @return the legs
   */
  public abstract ServiceLegs getLegs();

  /**
   * Collect the service leg segments
   * 
   * @return the legSegments
   */
  public abstract ServiceLegSegments getLegSegments();

  /**
   * Collect the service nodes
   * 
   * @return the nodes
   */
  public abstract ServiceNodes getServiceNodes();

}
