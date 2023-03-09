package org.goplanit.utils.network.layer.service;

import org.goplanit.utils.graph.ManagedGraphEntities;
import org.goplanit.utils.network.layer.physical.Node;
import org.goplanit.utils.network.layer.physical.Nodes;
import org.goplanit.utils.network.layer.physical.UntypedPhysicalLayer;

import java.util.function.BiConsumer;

/** Container around service legs with factory access 
 * 
 * @author markr
 */
public interface ServiceLegs extends ManagedGraphEntities<ServiceLeg> {
  
  /* do not derive from Links since we require to override the factory method return type. This is only
   * allowed when the return type directly derives from the original return type. ServiceLegFactory cannot
   * derive from LinkFactory since the signature of the factory methods differs. Hence, we must derive from
   * the base interface instead which has an empty dummy factory return type which one can always overwrite and
   * the ServiceLegFactory is derived from */
  
  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ServiceLegFactory getFactory();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ServiceLegs shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ServiceLegs deepClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ServiceLegs deepCloneWithMapping(BiConsumer<ServiceLeg, ServiceLeg> mapper);

  /**
   * verify if service leg is present
   * 
   * @param id to check
   * @return true when present false otherwise
   */
  public default boolean hasServiceLeg(long id) {
    return containsKey(id);
  }

}
