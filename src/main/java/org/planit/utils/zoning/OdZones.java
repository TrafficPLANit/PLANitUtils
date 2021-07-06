package org.planit.utils.zoning;

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
  public abstract OdZones clone();
}
