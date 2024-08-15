package org.goplanit.utils.od;

/**
 * This class contains common methods for handling origin-demand matrices of a certain type where the entire matrix is modelled even if some combinations of ODs do not contain any
 * data.
 * 
 * @author gman6028, markr
 *
 */
public interface OdMatrix<T, U> extends OdData<T> {

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
   * Returns an iterator which can iterate through all the origin-destination cells in the matrix
   * 
   * @return iterator through all the origin-destination cells
   */
  @Override
  public abstract OdMatrixIterator<T, U> iterator();
}
