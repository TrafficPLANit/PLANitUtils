package org.goplanit.utils.zoning.zonetozone;

import org.goplanit.utils.zoning.Zone;
import org.goplanit.utils.zoning.Zones;

/**
 * Base matrix oriented Iterator which runs through rows and columns of some matrix type of container,
 * making the value, row and column of each cell available
 *
 * 
 * @param <T> type of values in matrix
 * @param <U> type of matrix container used
 * 
 * @author gman6028, markr
 *
 */
public abstract class ZoneToZoneMatrixIterator<T, U> implements ZoneToZoneDataIterator<T> {

  /** the contents */
  private U matrixContents;

  /**
   * Id of the origin zone
   */
  protected int fromId;

  /**
   * Id of the destination zone
   */
  protected int toId;

  /**
   * Marker used to store the current position in the OD matrix (used internally, not accessible from other classes)
   */
  protected int currentLocation;

  /**
   * Zones object to store travel analysis zones (from Zoning object)
   */
  protected Zones<? extends Zone> zones;

  /**
   * Increment the location cursor for the next iteration
   */
  protected void updateCurrentLocation() {
    fromId = currentLocation / zones.size();
    toId = currentLocation % zones.size();
    currentLocation++;
  }

  /**
   * Collect contents as type
   * 
   * @return matrix content value
   */
  protected U getMatrixContent() {
    return matrixContents;
  }

  /**
   * Constructor
   * 
   * @param matrixContents matrix object containing the data to be iterated through
   * @param zones          Zones object defining the zones in the network
   */
  public ZoneToZoneMatrixIterator(U matrixContents, Zones<? extends Zone> zones) {
    super();
    this.zones = zones;
    currentLocation = 0;
    this.matrixContents = matrixContents;
  }

  /**
   * Tests whether there are any more cells to iterate through
   * 
   * @return true if there are more cells to iterate through, false otherwise
   */
  @Override
  public boolean hasNext() {
    return currentLocation < (zones.size() * zones.size());
  }

  /**
   * Returns the origin zone object for the current cell
   * 
   * @return the origin zone object at the current cell
   */
  @Override
  public Zone getCurrentFromZone() {
    return zones.get(fromId);
  }

  /**
   * Returns the destination zone object for the current cell
   * 
   * @return the destination zone object for the current cell
   */
  @Override
  public Zone getCurrentToZone() {
    return zones.get(toId);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public T next() {
    updateCurrentLocation();
    return getCurrentValue();
  }

}
