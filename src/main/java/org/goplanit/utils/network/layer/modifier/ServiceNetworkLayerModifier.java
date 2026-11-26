package org.goplanit.utils.network.layer.modifier;

import org.goplanit.utils.modifier.LoggableModifier;

import org.goplanit.utils.network.layer.service.ServiceLeg;
import org.goplanit.utils.network.layer.service.ServiceLegSegment;
import org.goplanit.utils.network.layer.service.ServiceNode;

/**
 * Modifier with additional functionality related to modifications to service network layers
 *
 * @author markr
 */
public interface ServiceNetworkLayerModifier<
        V extends ServiceNode,
        E extends ServiceLeg,
        S extends ServiceLegSegment> extends UntypedDirectedGraphLayerModifier<V,E,S>, LoggableModifier {

  /**
   * Remove all entities (service nodes, legs, leg segments) without a mapping to the underlying physical network layer
   * the service network is attached to.
   * <p>
   *   This recreates all managed ids across the service network, as removing the unmapped entries leaves gaps
   * </p>
   * <p>
   *   Should fire #RecreatedGraphEntitiesManagedIdsEvent after it has been executed
   * </p>
   */
    public abstract void removeUnmappedServiceNetworkEntities();

}
