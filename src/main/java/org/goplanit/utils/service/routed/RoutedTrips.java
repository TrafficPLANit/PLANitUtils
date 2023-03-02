package org.goplanit.utils.service.routed;

import org.goplanit.utils.id.ManagedIdEntities;
import org.goplanit.utils.network.layer.service.ServiceNode;

import java.util.HashSet;
import java.util.Set;

/**
 * Base class for routed trips container for some derived type of RoutedTrip (either schedule or frequency based for example).
 * 
 * @author markr
 */
public interface RoutedTrips<T extends RoutedTrip> extends ManagedIdEntities<T>, Iterable<T> {

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract RoutedTripFactory<T> getFactory();

  /**
   * Get all used service nodes along all registered trips as a newly created set
   * @return used service nodes
   */
  public default Set<ServiceNode> determineUsedServiceNodes(){
    Set<ServiceNode> serviceNodes = new HashSet<>();
    forEach(t -> serviceNodes.addAll(t.getUsedServiceNodes()));
    return serviceNodes;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract RoutedTrips shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract RoutedTrips deepClone();

}
