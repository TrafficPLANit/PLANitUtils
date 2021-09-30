package org.planit.utils.od;

import java.util.logging.Logger;

import org.ojalgo.array.Array2D;
import org.planit.utils.id.IdAble;
import org.planit.utils.id.IdGroupingToken;
import org.planit.utils.zoning.OdZones;
import org.planit.utils.zoning.Zone;

/**
 * This class contains common methods for handling origin-demand matrices.
 * 
 * @author gman6028, markr
 *
 */
public abstract class OdPrimitiveMatrix<T extends Number> extends OdMatrixImpl<T, Array2D<T>> {

  /** the logger */
  @SuppressWarnings("unused")
  private static final Logger LOGGER = Logger.getLogger(OdPrimitiveMatrix.class.getCanonicalName());

  /**
   * Constructor for Od matrix containing primitives, i.e. number based
   * 
   * @param idTokenClass   to use for id generation
   * @param idToken        to use for the matrix id
   * @param zones          holder for zones considered in the matrix
   * @param matrixContents container for the matrix contents
   */
  public OdPrimitiveMatrix(Class<? extends IdAble> idTokenClass, IdGroupingToken idToken, OdZones zones, Array2D<T> matrixContents) {
    super(idTokenClass, idToken, zones, matrixContents);
  }

  /**
   * Copy Constructor
   * 
   * @param other to copy
   */
  public OdPrimitiveMatrix(OdPrimitiveMatrix<T> other) {
    super(other);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setValue(Zone origin, Zone destination, T value) {
    matrixContents.set(origin.getId(), destination.getId(), value);
  }

  /**
   * {@inheritDoc}
   */
  public T getValue(Zone origin, Zone destination) {
    return matrixContents.get(origin.getId(), destination.getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public T getValue(long originId, long destinationId) {
    return matrixContents.get(originId, destinationId);
  }

  /**
   * {@inheritDoc
   */
  @Override
  public abstract OdPrimitiveMatrix<T> clone();

  /**
   * Returns an iterator which can iterate through all the origin-destination cells in the matrix
   * 
   * @return iterator through all the origin-destination cells
   */
  @Override
  public abstract OdMatrixIterator<T, Array2D<T>> iterator();
}
