package org.goplanit.utils.zoning.zonetozone;

import org.goplanit.utils.zoning.Zone;
import org.goplanit.utils.zoning.Zones;

/**
 * Iterator of OdMatrix with objects stored as raw two-dimensional array
 * 
 * @author markr
 *
 * @param <T> type of values in od matrix
 */
public class ZoneToZoneNonPrimitiveMatrixIterator<T> extends ZoneToZoneMatrixIterator<T, T[][]> {

  /**
   * Constructor
   * 
   * @param matrixContents in preferred container format for values
   * @param zones          zones
   */
  public ZoneToZoneNonPrimitiveMatrixIterator(T[][] matrixContents, Zones<? extends Zone> zones) {
    super(matrixContents, zones);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public T getCurrentValue() {
    return getMatrixContent()[fromId][toId];
  }

}
