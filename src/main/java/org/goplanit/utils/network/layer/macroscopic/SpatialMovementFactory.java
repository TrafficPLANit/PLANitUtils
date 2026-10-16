package org.goplanit.utils.network.layer.macroscopic;

import org.goplanit.utils.id.ManagedIdEntityFactory;

/**
 * Factory to create spatial movements and register them on their container. A movement is created without a direct
 * ban; place one with {@link SpatialMovement#banDirectly(java.util.Collection)}
 *
 * @author markr
 */
public interface SpatialMovementFactory extends ManagedIdEntityFactory<SpatialMovement> {

  /**
   * Create a movement, not registered
   *
   * @param from the link segment it starts on
   * @param to the link segment it ends on
   * @param lengthKm from where the from segment ends to where the to segment starts
   * @return created movement
   */
  public abstract SpatialMovement create(MacroscopicLinkSegment from, MacroscopicLinkSegment to, double lengthKm);

  /**
   * Create a movement and register it
   *
   * @param from the link segment it starts on
   * @param to the link segment it ends on
   * @param lengthKm from where the from segment ends to where the to segment starts
   * @return created and registered movement
   */
  public abstract SpatialMovement registerNew(MacroscopicLinkSegment from, MacroscopicLinkSegment to, double lengthKm);
}
