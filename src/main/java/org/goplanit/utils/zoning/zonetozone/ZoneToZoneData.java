package org.goplanit.utils.zoning.zonetozone;

import org.goplanit.utils.id.IdAble;
import org.goplanit.utils.zoning.Zone;

/**
 * Interface defining methods for objects which store data related to zone-to-zone data
 * 
 * @author gman6028
 *
 * @param <T> the type of data to be stored for each zone-to-zone cell
 */
public interface ZoneToZoneData<T> extends IdAble, Iterable<T> {

  /**
   * Access to class of the chosen data type
   *
   * @return clazz
   */
  public Class<T> getDataClass();

  /**
   * Returns the value for a specified origin and destination
   * 
   * @param from      specified origin
   * @param to specified destination
   * @return value at the specified cell
   */
  public T getValue(Zone from, Zone to);

  /**
   * Returns the value for a specified origin and destination by their internal id
   *
   * @param from      specified origin
   * @param to specified destination
   * @return value at the specified cell
   */
  public default boolean hasValue(Zone from, Zone to){
    return hasValue(from.getId(), to.getId());
  }

  /**
   * Returns the value for a specified origin and destination by their internal id
   * 
   * @param from      specified origin
   * @param to specified destination
   * @return value at the specified cell
   */
  public T getValue(long from, long to);

  /**
   * Returns the value for a specified origin and destination by their internal id
   *
   * @param from      specified origin
   * @param to specified destination
   * @return value at the specified cell
   */
  public default boolean hasValue(long from, long to){
    return getValue(from, to) != null;
  }

  /**
   * Sets the value for a specified origin and destination
   * 
   * @param from      specified origin
   * @param to        specified destination
   * @param value     value at the specified cell
   */
  public void setValue(Zone from, Zone to, T value);

  /**
   * Returns the number of zones contained in the object
   * 
   * @return number of zones in the object
   */
  public int getNumberOfZones();

  /**
   * Returns an iterator which can iterate through all the origin-destination cells
   * 
   * @return iterator through all the origin-destination cells
   */
  public ZoneToZoneDataIterator<T> iterator();

  /**
   * Shallow copy
   * @return shallow copy
   */
  public ZoneToZoneData<T> shallowClone();
  
}
