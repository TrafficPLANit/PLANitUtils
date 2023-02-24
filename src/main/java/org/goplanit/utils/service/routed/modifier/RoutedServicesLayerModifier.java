package org.goplanit.utils.service.routed.modifier;

import org.goplanit.utils.event.EventListener;
import org.goplanit.utils.mode.Mode;
import org.goplanit.utils.network.layer.modifier.ServiceNetworkLayerModifier;
import org.goplanit.utils.zoning.modifier.event.ZoningModifierEventProducer;

import java.util.List;

/**
 * Modification utilities for a routed services layer that require complex changes to the underlying containers
 */
public interface RoutedServicesLayerModifier extends RoutedServicesModifierEventProducer {

  /**
   * Remove all RoutedServicesByMode instances from the layer in case they no longer have any services  associated with them
   *
   * @param recreateManagedEntitiesIds when true recreate all managed ids on the layer (if needed), otherwise do not
   */
  public abstract void removeEmptyRoutedServicesByMode(boolean recreateManagedEntitiesIds);

  /**
   * Remove all routed services from the layer in case they no longer have any trips associated with them for the given modes
   *
   * @param recreateManagedEntitiesIds when true recreate all managed ids on the layer, otherwise do not
   * @param modes to do this for
   */
  public abstract void removeRoutedServicesWithoutTrips(boolean recreateManagedEntitiesIds, Mode... modes);

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
   * Consolidate all PLANit RoutedTrips with identical relative timing schedules (but different departure times) into a single
   *  PLANit Routed trip. Redundant trips are removed. Not that Ids are no updated when these entries are removed from their respective containers. This is left
   *  to the user to do manually afterwards if desired.
   *
   * @param mode to do this for
   */
  public abstract void consolidateIdenticallyScheduledTrips(Mode mode);

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
  public abstract void recreateRoutedTripScheduleDepartureIds();

  /**
   * Recreate the ids of the routed services layer's routed trips (scheduled and frequency based) across all modes
   */
  public abstract void recreateRoutedTripsIds();

}
