package org.goplanit.utils.zoning;

/**
 * an OdZone is a zone where trips terminate and or commence
 * 
 * @author markr
 *
 */
public interface OdZone extends Zone {
  
  /** the class to use for the additional od zone id generation */
  public static final Class<OdZone> OD_ZONE_ID_CLASS = OdZone.class;    

  /** In addition to a zone id across all zones of any derived type, each Od zone also has a unique id
   * across the Od zones specifically
   * 
   * @return od zone specific id
   */
  public abstract long getOdZoneId();
  
  /**
   * @return od zone id class for id generation
   */
  public default Class<OdZone> getOdZoneIdClass() {
    return OD_ZONE_ID_CLASS;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract OdZone shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract OdZone deepClone();
}
