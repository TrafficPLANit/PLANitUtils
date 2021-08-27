package org.planit.utils.zoning;

import java.util.function.BiConsumer;

/**
 * Container class for OdZones
 * 
 * @author markr
 *
 */
public interface OdZones extends Zones<OdZone>{

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract OdZoneFactory getFactory();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract OdZones clone();
  
  /** Loop over all origin destination combinations possible given the registed zones and apply the 
   * provided consumer
   * 
   * @param consumer
   */
  public default void forEachOriginDestination(final BiConsumer<OdZone, OdZone> consumer) {
    /* origin */
    for (OdZone origin : this) {
      /* destination */
      for (OdZone destination : this) {
        consumer.accept(origin, destination);
      }
    }
  }
}
