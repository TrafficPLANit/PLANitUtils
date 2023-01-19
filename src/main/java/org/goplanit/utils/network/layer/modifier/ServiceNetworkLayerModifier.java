package org.goplanit.utils.network.layer.modifier;

import org.goplanit.utils.network.layer.service.ServiceLeg;
import org.goplanit.utils.network.layer.service.ServiceLegSegment;
import org.goplanit.utils.network.layer.service.ServiceNode;

/**
 * Modifier with additional functionality related to modifications to service network layers
 *
 * @author markr
 */
public interface ServiceNetworkLayerModifier<V extends ServiceNode, E extends ServiceLeg, S extends ServiceLegSegment> extends UntypedDirectedGraphLayerModifier<V,E,S> {

  /**
   * Method that will remove all entities (service nodes, legs, leg segments) that have no mapping present to the underlying physical network layer
   * the service network is attached to
   *
   */
  public abstract void removeUnmappedServiceNetworkEntities();

}
