package org.goplanit.utils.zoning.zonetozone;

import org.goplanit.utils.id.IdAble;
import org.goplanit.utils.id.IdAbleImpl;
import org.goplanit.utils.id.IdGroupingToken;
import org.goplanit.utils.zoning.Zone;
import org.goplanit.utils.zoning.Zones;

/**
 * Base class containing common methods required by all classes which implement ZoneToZoneData
 * 
 * @author gman6028
 *
 * @param <T> the type of data to be stored for each origin-destination cell
 */
public abstract class ZoneToZoneDataImpl<T> extends IdAbleImpl implements ZoneToZoneData<T> {

  protected Class<T> dataClass;

  /**
   * holder for zones considered in the matrix
   */
  protected Zones<? extends Zone> zones;

  /**
   * Access to underlying zones
   *
   * @return odZones
   */
  public Zones<? extends Zone> getZones() {
    return zones;
  }

  /**
   * Constructor
   * 
   * @param idTokenClass to use for id grouping
   * @param idToken      to use for id generation
   * @param dataClass    class of the entries for each od
   * @param zones        zones considered in the matrix
   */
  public ZoneToZoneDataImpl(
      final Class<? extends IdAble> idTokenClass,
      IdGroupingToken idToken, Class<T> dataClass,
      final Zones<? extends Zone> zones) {

    super(generateId(idToken, idTokenClass));
    this.zones = zones;
    this.dataClass = dataClass;
  }

  /**
   * Copy Constructor
   * 
   * @param other to copy
   */
  public ZoneToZoneDataImpl(ZoneToZoneDataImpl<? extends T> other) {
    super(other);
    this.zones = other.zones;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Class<T> getDataClass() {
    return dataClass;
  }

  /**
   * Returns the number of zones contained in the object
   * 
   * @return number of zones in the object
   */
  public int getNumberOfZones() {
    return zones.size();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ZoneToZoneDataImpl<T> shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ZoneToZoneDataImpl<T> deepClone();
}
