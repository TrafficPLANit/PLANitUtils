package org.goplanit.utils.zoning.zonetozone;

import org.goplanit.utils.zoning.Zone;
import org.goplanit.utils.zoning.Zones;
import org.ojalgo.array.Array2D;

/**
 * Iterator of OdMatrix with primitives stored as Array2D
 * 
 * @author markr
 *
 * @param <T> type of primitive used which must be a Number derived type
 */
public class ZoneToZonePrimitiveMatrixIterator<T extends Number> extends ZoneToZoneMatrixIterator<T, Array2D<T>> {

  /**
   * Constructor
   * 
   * @param matrixContents in preferred container format for primitives
   * @param zones          zones
   */
  public ZoneToZonePrimitiveMatrixIterator(Array2D<T> matrixContents, Zones<? extends Zone> zones) {
    super(matrixContents, zones);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public T getCurrentValue() {
    return getMatrixContent().get(fromId, toId);
  }

}
