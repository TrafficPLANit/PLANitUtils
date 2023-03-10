package org.goplanit.utils.service.routed;

import org.goplanit.utils.id.ManagedIdEntities;
import org.goplanit.utils.mode.Mode;
import org.goplanit.utils.zoning.Zones;

import java.util.function.BiConsumer;

/**
 * Interface for wrapper container class around RoutedModeServices for a particular mode. This container is used to store instances of a routed service for a given mode
 * 
 * @author markr
 *
 */
public interface RoutedModeServices extends ManagedIdEntities<RoutedService> {

  /**
   * The supported mode for the routed services registered
   * 
   * @return supported mode
   */
  public abstract Mode getMode();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract RoutedModeServices shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract RoutedModeServices deepClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract RoutedModeServices deepCloneWithMapping(BiConsumer<RoutedService, RoutedService> mapper);

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract RoutedServiceFactory getFactory();

}
