package org.goplanit.utils.zoning.zonetozone;

/**
 * This class contains common methods for handling zone-to-zone matrices of a certain type where the entire
 * matrix is modeled even if some combinations of ODs do not contain any data.
 * 
 * @author gman6028, markr
 *
 */
public interface ZoneToZoneMatrix<T, U> extends ZoneToZoneData<T> {

  /**
   * Count number of non-empty entries by iterating over them
   *
   * @return non-empty entries
   */
  public default long determineNonNullCells() {
    long counter = 0;
    var iter = iterator();
    while(iter.hasNext()){
      if(iter.next() != null){
        ++counter;
      }
    }
    return counter;
  }

  /**
   * Returns an iterator which can iterate through all the cells in the matrix
   * 
   * @return iterator through all the origin-destination cells
   */
  @Override
  public abstract ZoneToZoneMatrixIterator<T, U> iterator();
}
