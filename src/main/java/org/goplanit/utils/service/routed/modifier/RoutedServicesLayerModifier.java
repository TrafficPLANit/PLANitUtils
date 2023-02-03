package org.goplanit.utils.service.routed.modifier;

import org.goplanit.utils.network.layer.modifier.ServiceNetworkLayerModifier;
import org.goplanit.utils.zoning.modifier.event.ZoningModifierEventProducer;

/**
 * Modification utilities for a routed services layer that require complex changes to the underlying containers
 */
public interface RoutedServicesLayerModifier extends RoutedServicesModifierEventProducer {

  /**
   * Method that will truncate all routed entities to available service network entities in its parent in case the parent service network has been altered for some reason,
   * i.e., the service network entities might still exist as references on the routed services but no longer exist in the related service layer.
   * To be used - for example - in tandem with {@link ServiceNetworkLayerModifier#removeUnmappedServiceNetworkEntities()} which removes service network entities
   * that are not attached to underlying physical network entities. this type of modification is useful for example when a parsed (routed) service network covers a
   * larger area than the underlying physical network chosen, in which case some or many of the service routes are mapped to a service network that in turn could
   * not be mapped to the physical network available. In those cases this method truncates all routes for which its service network elements are missing from its
   * parent service layer (because they were fully or partially detached from a physical network and were removed because of it)
   * <p>
   *   Note that invoking this method will recreate all managed ids across the routed services due to gaps occurring after removal of unmapped entries
   * </p>
   *
   */
  public abstract void truncateToServiceNetwork();

  /**
   * This method will recreate all ids of the routed services' components, but only when the containers used for them are the primary ManagedIdEntities containers, i.e., when the routed services layer
   * is responsible of uniquely tracking all entities by their managed id. If not, it will not recreate the ids.
   * <p>
   * The reasoning is that if we would recreate ids of the container while the container does not contain all = let's say - routes, their managedId is no longer guaranteed to be unique which can lead to issues
   * <p>
   * Method can be used in conjunctions with the removal of parts of the routed services and the result is required to have unique contiguous ids
   */
  public abstract void recreateManagedEntitiesIds();

  /**
   * Recreate the ids of the routed services layer routedModeServices across all modes
   */
  public abstract void recreateRoutedServicesIds();

  /**
   * Recreate the ids of the routed services layer's routed trip departure ids across all scheduled trips across all modes
   */
  void recreateRoutedTripScheduleDepartureIds();

  /**
   * Recreate the ids of the routed services layer's routed trips (scheduled and frequency based) across all modes
   */
  void recreateRoutedTripsIds();
}
