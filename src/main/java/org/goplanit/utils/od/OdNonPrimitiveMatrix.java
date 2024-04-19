package org.goplanit.utils.od;

import java.util.logging.Logger;

import org.goplanit.utils.id.IdAble;
import org.goplanit.utils.id.IdGroupingToken;
import org.goplanit.utils.zoning.OdZones;
import org.goplanit.utils.zoning.Zone;

/**
 * This class contains common methods for handling origin-demand matrices where values are non-primitive types, i.e. Objects of some type. Data container used is a raw array of a
 * fixed size based on the Od number of zones. Cells that have no value entry will still take up space in this implementation. If the matrix is dense and contains very little empty
 * cells, this is likely the most computationally efficient implementation, if not, consider other implementations that deal better with sparse matrices in terms of memory usage.
 * TODO: replace internal structure with flat array rather than array of arrays to make it more efficient
 * 
 * @author gman6028m, markr
 *
 */
public abstract class OdNonPrimitiveMatrix<T> extends OdMatrixImpl<T, T[][]> {

  /** the logger */
  @SuppressWarnings("unused")
  private static final Logger LOGGER = Logger.getLogger(OdNonPrimitiveMatrix.class.getCanonicalName());

  /**
   * Constructor for full Od matrix containing non-primitives, i.e. object based
   * 
   * @param idTokenClass   to use for id generation
   * @param idToken        to use for the matrix id
   * @param valueClass class of the values in container
   * @param zones          holder for zones considered in the matrix
   * @param matrixContents container for the matrix contents
   */
  public OdNonPrimitiveMatrix(
          final Class<? extends IdAble> idTokenClass, final IdGroupingToken idToken, Class<T> valueClass, final OdZones zones, final T[][] matrixContents) {
    super(idTokenClass, idToken, valueClass, zones, matrixContents);
  }

  /**
   * Copy constructor for shallow copies only as we do not want to enforce a particular type upon T that supports
   * deeo cloning
   * 
   * @param other to copy from
   */
  public OdNonPrimitiveMatrix(OdNonPrimitiveMatrix<T> other) {
    super(other);
  }

  /**
   * Sets the value for a specified origin and destination
   * 
   * @param origin      specified origin
   * @param destination specified destination
   * @param value       value at the specified cell
   */
  @Override
  public void setValue(Zone origin, Zone destination, T value) {
    matrixContainer[(int) origin.getId()][(int) destination.getId()] = value;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public T getValue(Zone origin, Zone destination) {
    return matrixContainer[(int) origin.getId()][(int) destination.getId()];
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public T getValue(long originId, long destinationId) {
    return matrixContainer[(int) originId][(int) destinationId];
  }

  /**
   * Returns an iterator which can iterate through all the origin-destination cells in the matrix
   * 
   * @return iterator through all the origin-destination cells
   */
  @Override
  public abstract OdMatrixIterator<T, T[][]> iterator();
}
