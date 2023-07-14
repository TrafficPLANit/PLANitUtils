package org.goplanit.utils.zoning;

import java.util.function.BiConsumer;

/**
 * Container class for TransferZone instances
 * 
 * @author markr
 *
 */
public interface TransferZones extends Zones<TransferZone>{
  
  /**
   * {@inheritDoc}
   */
  @Override
  public abstract TransferZoneFactory getFactory();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract TransferZones shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract TransferZones deepClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract TransferZones deepCloneWithMapping(BiConsumer<TransferZone, TransferZone> mapper);
}
