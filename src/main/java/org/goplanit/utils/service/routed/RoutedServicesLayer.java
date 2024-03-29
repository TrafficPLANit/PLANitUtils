package org.goplanit.utils.service.routed;

import org.goplanit.utils.id.ExternalIdAble;
import org.goplanit.utils.id.ManagedId;
import org.goplanit.utils.mode.Mode;
import org.goplanit.utils.network.layer.ServiceNetworkLayer;
import org.goplanit.utils.service.routed.modifier.RoutedServicesLayerModifier;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Interface to reflect a routed services layer which in turn is to be managed by a container class that implements the RoutedServicesLayers interface. A RoutedServiceLayer
 * contains routed services for a given parent service network layer such as for example - but not limited to - bus routes, train lines etc.
 * 
 * @author markr
 *
 */
public interface RoutedServicesLayer extends ManagedId, ExternalIdAble, Iterable<RoutedModeServices> {

  /** id class for generating ids */
  public static final Class<RoutedServicesLayer> ROUTED_SERVICES_LAYER_ID_CLASS = RoutedServicesLayer.class;

  /**
   * {@inheritDoc}
   */
  @Override
  public default Class<RoutedServicesLayer> getIdClass() {
    return ROUTED_SERVICES_LAYER_ID_CLASS;
  }

  /**
   * invoked by entities inquiring about general information about the layer to display to users
   * 
   * @param prefix optional prefix to include in each line of logging
   */
  public abstract void logInfo(final String prefix);

  /**
   * Check if the layer is empty of any routed services
   *
   * @return true when empty, false otherwise
   */
  public abstract boolean isEmpty();

  /**
   * The parent service layer of this routed services layer
   * 
   * @return parent layer
   */
  public abstract ServiceNetworkLayer getParentLayer();

  /**
   * Verify if there exist no registered services for a given mode at all  at present
   *
   * @param mode to check
   * @return true when no single routed service by mode exists, false otherwise
   */
  public boolean isServicesByModeEmpty(Mode mode);

  /**
   * The services for a given mode available on this layer. If no services are yet available an empty instance is created and registered.
   * It is expected that each routed service across all modes on the layer has a unique internal id, so internal ids do not restart at zero per mode
   * 
   * @param mode to obtain services for
   * @return services by mode, empty instance if none have been registered yet
   */
  public abstract RoutedModeServices getServicesByMode(final Mode mode);

  /**
   * {@inheritDoc}
   */
  @Override
  public default void resetChildManagedIdEntities() {
    forEach(routedModeServices -> routedModeServices.reset());
  }

  /** Collect the supported modes that potentially have services registered based on the parent layer
   * @return supported modes
   */
  public default Collection<Mode> getSupportedModes(){
    return getParentLayer().getSupportedModes();
  }

  /** Collect the modes for which a RoutedModeService entry is available, i.e., for which we expect services to exist rather than potentially support.
   * this method does not look at the parent layer supported modes, but only at the modes for which a routed services container exists.
   *
   * @return available modes
   */
  public default Collection<Mode> getSupportedModesWithServices(){
    return getParentLayer().getSupportedModes().stream().filter( m -> !this.isServicesByModeEmpty(m)).collect(Collectors.toList());
  }

  /**
   * Routed services have an additional modifier options to provided integrated utilities for changing the routed services in
   * a way that updates all attached managed routed and service network elements involved
   *
   * @return routed services layer modifier
   */
  public abstract RoutedServicesLayerModifier getLayerModifier();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract RoutedServicesLayer shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract RoutedServicesLayer deepClone();
}
