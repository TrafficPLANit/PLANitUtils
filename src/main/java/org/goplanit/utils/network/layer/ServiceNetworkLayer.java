package org.goplanit.utils.network.layer;

import org.goplanit.utils.network.layer.modifier.ServiceNetworkLayerModifier;
import org.goplanit.utils.network.layer.service.ServiceLeg;
import org.goplanit.utils.network.layer.service.ServiceLegSegment;
import org.goplanit.utils.network.layer.service.ServiceLegSegments;
import org.goplanit.utils.network.layer.service.ServiceLegs;
import org.goplanit.utils.network.layer.service.ServiceNode;
import org.goplanit.utils.network.layer.service.ServiceNodes;

/**
 * Service network layer consisting of service nodes, legs and leg segments on top of a parent network layer with physical entities. Service network layers
 * are used to define service legs between nodes offered by one or more routed services as a separate network layer. 
 *
 * @author markr
 */
public interface ServiceNetworkLayer extends UntypedDirectedGraphLayer<ServiceNode, ServiceLeg, ServiceLegSegment> {

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ServiceNetworkLayer clone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ServiceNetworkLayer deepClone();

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

  /**
   * Service networks have additional modifier options as per the service network layer modifier
   *
   * @return service network layer modifier
   */
  @Override
  public abstract ServiceNetworkLayerModifier<ServiceNode, ServiceLeg, ServiceLegSegment> getLayerModifier();
  
}
