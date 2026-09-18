package org.goplanit.utils.zoning.zonetozone;

import java.util.Iterator;

import org.goplanit.utils.zoning.Zone;

/**
 * Defines methods for an iterator which moves through a matrix of origin-destination cells
 * 
 * @author gman6028
 *
 * @param <T> the type of data stored for each origin-destination cell
 */
public interface ZoneToZoneDataIterator<T> extends Iterator<T> {

  /**
   * Returns the origin zone object for the current cell
   * 
   * @return the origin zone object at the current cell
   */
  public Zone getCurrentFromZone();

  /**
   * Returns the destination zone object for the current cell
   * 
   * @return the destination zone object for the current cell
   */
  public Zone getCurrentToZone();

  /**
   * Returns the value at the current cell
   * 
   * @return the value at the current cell
   */
  public T getCurrentValue();
}
