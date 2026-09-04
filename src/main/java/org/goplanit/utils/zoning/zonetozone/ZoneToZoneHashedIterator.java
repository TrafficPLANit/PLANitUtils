package org.goplanit.utils.zoning.zonetozone;

import org.goplanit.utils.zoning.Zone;
import org.goplanit.utils.zoning.Zones;

/**
 * Base Hash key oriented Iterator which runs through available ods that have non-zero values
 *
 * 
 * @param <T> type of values
 * 
 * @author markr
 *
 */
public class ZoneToZoneHashedIterator<T> implements ZoneToZoneDataIterator<T> {

  private final ZoneToZoneHashed<T> container;

  /**
   * Id of the origin zone
   */
  protected int originId;

  /**
   * Id of the destination zone
   */
  protected int destinationId;

  /**
   * Marker used to store the current position in the OD matrix (used internally, not accessible from other classes)
   */
  protected int currentLocation;

  /**
   * Zones object to store travel analysis zones (from Zoning object)
   */
  protected Zones<? extends Zone> zones;

  /**
   * Increment the location cursor until we reach a non-empty entry
   */
  protected void updateCurrentLocation() {
    do {
      originId = currentLocation / container.getNumberOfZones();
      destinationId = currentLocation % container.getNumberOfZones();
      currentLocation++;
    } while (getCurrentValue() == null && hasNext());
  }

  /**
   * Constructor
   * 
   * @param container object containing the data to be iterated through
   * @param zones to use
   */
  public ZoneToZoneHashedIterator(final ZoneToZoneHashed<T> container, Zones<? extends Zone> zones) {
    super();
    currentLocation = 0;
    this.container = container;
    this.zones = zones;
  }

  /**
   * Tests whether there are any more cells to iterate through
   * 
   * @return true if there are more cells to iterate through, false otherwise
   */
  @Override
  public boolean hasNext() {
    return currentLocation < (container.getNumberOfZones() * container.getNumberOfZones());
  }

  /**
   * Returns the origin zone object for the current cell
   * 
   * @return the origin zone object at the current cell
   */
  @Override
  public Zone getCurrentFromZone() {
    return zones.get(originId);
  }

  /**
   * Returns the destination zone object for the current cell
   * 
   * @return the destination zone object for the current cell
   */
  @Override
  public Zone getCurrentToZone() {
    return zones.get(destinationId);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public T next() {
    updateCurrentLocation();
    return getCurrentValue();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public T getCurrentValue() {
    return container.getValue(originId, destinationId);
  }
}
