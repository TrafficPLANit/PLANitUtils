package org.goplanit.utils.service.routed.modifier;

import org.goplanit.utils.event.EventListener;
import org.goplanit.utils.mode.Mode;
import org.goplanit.utils.modifier.LoggableModifier;
import org.goplanit.utils.network.layer.modifier.ServiceNetworkLayerModifier;
import org.goplanit.utils.zoning.modifier.event.ZoningModifierEventProducer;

import java.util.List;

/**
 * Modification utilities for a routed services layer that require complex changes to the underlying containers
 */
public interface RoutedServicesLayerModifier extends RoutedServicesModifierEventProducer, LoggableModifier {

  /**
   * Remove all RoutedServicesByMode instances from the layer in case they no longer have any services associated with
   * them
   * <p>
   *   When recreateRoutedServicesManagedEntitiesIds is true, event for recreating routed services ids is fired for
   *   which a listener can be attached
   * </p>
   *
   * @param recreateRoutedServicesManagedEntitiesIds when true recreate routed services ids, otherwise do not
   */
  public abstract void removeEmptyRoutedServicesByMode(boolean recreateRoutedServicesManagedEntitiesIds);

  /**
   * Remove all routed services from the layer in case they no longer have any trips associated with them for the given
   * modes
   * <p>
   *   When managed id entities are recreated, it will do so for routed services
   * </p>
   *
   * @param recreateManagedEntitiesIds when true recreate all managed ids on the layer, otherwise do not
   * @param modes to do this for
   */
  public abstract void removeRoutedServicesWithoutTrips(boolean recreateManagedEntitiesIds, Mode... modes);

  /**
   * Remove all scheduled trips from the layer (for the given modes) in case they have no relative leg timings. this
   * might occur when only a single stop has been identified for a trip based on a parsed external data source for
   * example, e.g., GTFS with a time period filter.
   * <p>
   *   When managed id entities are recreated, it will do so for trips and departure ids
   * </p>
   *
   * @param recreateManagedEntitiesIds when true recreate all managed ids on the layer, otherwise do not
   * @param modes to do this for
   */
  public abstract void removeScheduledTripsWithoutLegs(boolean recreateManagedEntitiesIds, Mode... modes);

  /**
   * Remove all duplicate scheduled trip departures from the layer. This might occur when there are duplicates in the
   * exogenous data source for example
   * <p>
   *   When ids are recreated, it will do so for departure ids only
   * </p>
   *
   * @param recreateManagedDepartureIds when true recreate all departure ids on the layer, otherwise do not
   */
  void removeDuplicateTripDepartures(boolean recreateManagedDepartureIds);

  /**
   * Truncate all routed entities to the service network entities available in their parent, for when the parent
   * service network has been altered, i.e., service network entities may still be referenced by the routed services
   * but no longer exist in the related service layer. To be used, for example, together with
   * {@link ServiceNetworkLayerModifier#removeUnmappedServiceNetworkEntities()}, which removes service network entities
   * not attached to underlying physical network entities. That is useful, for example, when a parsed (routed) service
   * network covers a larger area than the physical network chosen, so some or many of the service routes are mapped to
   * a service network that in turn could not be mapped to the physical network. This method then truncates all routes
   * whose service network elements are missing from their parent service layer (because they were fully or partially
   * detached from a physical network and removed for it)
   * <p>
   *   This recreates all managed ids across the routed services, as removing the unmapped entries leaves gaps
   * </p>
   *
   */
  public abstract void truncateToServiceNetwork();

  /**
   * Consolidate all PLANit RoutedTrips with identical relative timing schedules (but different departure times) into a
   * single PLANit Routed trip. Redundant trips are removed. Note that Ids are not updated when these entries are
   * removed from their respective containers. This is left to the user to do manually afterwards if desired.
   *
   * @param mode to do this for
   */
  public abstract void consolidateIdenticallyScheduledTrips(Mode mode);

  /**
   * Recreate all ids of the routed services' components, but only when their containers are the primary
   * ManagedIdEntities containers, i.e., when the routed services layer is responsible for uniquely tracking all
   * entities by their managed id. Otherwise the ids are not recreated.
   * <p>
   * Recreating the ids of a container that does not hold all of, say, the routes would no longer guarantee their
   * managed ids to be unique, which can lead to issues.
   * </p>
   * <p>
   * Use it together with the removal of parts of the routed services when the result must have unique contiguous ids
   * </p>
   */
  public abstract void recreateManagedEntitiesIds();

  /**
   * Recreate the ids of the routed services layer routedModeServices across all modes
   */
  public abstract void recreateRoutedServicesIds();

  /**
   * Recreate the ids of the routed services layer's routed trip departure ids across all scheduled trips across all
   * modes
   */
  public abstract void recreateRoutedTripScheduleDepartureIds();

  /**
   * Recreate the ids of the routed services layer's routed trips (scheduled and frequency based) across all modes
   */
  public abstract void recreateRoutedTripsIds();

}
