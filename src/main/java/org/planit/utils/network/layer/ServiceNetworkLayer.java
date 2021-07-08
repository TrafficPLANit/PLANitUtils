package org.planit.utils.network.layer;

import org.planit.utils.network.layer.service.ServiceLeg;
import org.planit.utils.network.layer.service.ServiceLegSegment;
import org.planit.utils.network.layer.service.ServiceLegSegments;
import org.planit.utils.network.layer.service.ServiceLegs;
import org.planit.utils.network.layer.service.ServiceNode;
import org.planit.utils.network.layer.service.ServiceNodes;

/**
 * Physical topological Network consisting of nodes, links and link segments 
 *
 * @author markr
 */
public interface ServiceNetworkLayer extends UntypedDirectedGraphLayer<ServiceNode, ServiceNodes, ServiceLeg, ServiceLegs, ServiceLegSegment, ServiceLegSegments> {
  
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
