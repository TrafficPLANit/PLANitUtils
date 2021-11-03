package org.goplanit.utils.zoning;

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
  public abstract TransferZones clone();
}
