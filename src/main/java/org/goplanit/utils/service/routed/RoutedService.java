package org.goplanit.utils.service.routed;

import org.goplanit.utils.id.ExternalIdAble;
import org.goplanit.utils.id.ManagedId;
import org.goplanit.utils.misc.StringUtils;
import org.goplanit.utils.mode.Mode;

/**
 * Interface to reflect a routed service. A routed service reflects a route on a service network comprising of one or more legs. A leg comprises one or more physical links on an
 * underlying physical network.
 * 
 * @author markr
 *
 */
public interface RoutedService extends ManagedId, ExternalIdAble {

  /** id class for generating ids */
  public static final Class<RoutedService> ROUTED_SERVICE_ID_CLASS = RoutedService.class;

  /**
   * {@inheritDoc}
   */
  @Override
  public default Class<RoutedService> getIdClass() {
    return ROUTED_SERVICE_ID_CLASS;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract RoutedService shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract RoutedService deepClone();

  /**
   * Name of the service, can be a number. Shortest visual (user) identifier for the service
   * 
   * @return name
   */
  public abstract String getName();

  /**
   * Verify if routed service has a name
   * @return true when present and non-empty, false otherwise
   */
  public default boolean hasName(){
    return !StringUtils.isNullOrBlank(getName());
  }

  /**
   * Name of the service, can be a number. Shortest visual (user) identifier for the service
   * 
   * @param name to use
   */
  public abstract void setName(String name);

  /**
   * Description of the name of the service, usually a short elaboration in addition to the name (number), for example for a bus the name could be 370 and the nameDescription
   * "Balmain to Coogee"
   * 
   * @return nameDescription
   */
  public abstract String getNameDescription();

  /**
   * Verify if routed service has a name description
   * @return true when present and non-empty, false otherwise
   */
  public default boolean hasNameDescription(){
    return !StringUtils.isNullOrBlank(getNameDescription());
  }

  /**
   * Description of the name of the service, usually a short elaboration in addition to the name (number), for example for a bus the name could be 370 and the nameDescription
   * "Balmain to Coogee"
   * 
   * @param nameDescription to use
   */
  public abstract void setNameDescription(String nameDescription);

  /**
   * Description of the service, longer (if at all) and contextual not meant for end user in reality
   * 
   * @return serviceDescription
   */
  public abstract String getServiceDescription();

  /**
   * Verify if routed service has a name description
   * @return true when present and non-empty, false otherwise
   */
  public default boolean hasServiceDescription(){
    return StringUtils.isNullOrBlank(getServiceDescription());
  }

  /**
   * Description of the service, longer (if at all) and contextual not meant for end user in reality
   * 
   * @param serviceDescription to use
   */
  public abstract void setServiceDescription(String serviceDescription);

  /**
   * Access to the trips available for this service
   * 
   * @return known routed service trips
   */
  public abstract RoutedServiceTripInfo getTripInfo();

  /**
   * Access to the mode this routed service utilises
   *
   * @return mode for this routed service
   */
  public abstract Mode getMode();

}
