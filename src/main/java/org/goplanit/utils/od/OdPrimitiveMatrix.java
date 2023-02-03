package org.goplanit.utils.od;

import java.util.logging.Logger;

import org.goplanit.utils.id.IdAble;
import org.goplanit.utils.id.IdGroupingToken;
import org.goplanit.utils.zoning.OdZones;
import org.goplanit.utils.zoning.Zone;
import org.ojalgo.array.Array2D;

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
  public OdPrimitiveMatrix(OdPrimitiveMatrix<T> other, Array2D.Factory<T> contentFactory) {
    super(other);
    this.matrixContents = contentFactory.copy(other.matrixContents); // shallow copy
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
   * {@inheritDoc}
   */
  @Override
  public abstract OdPrimitiveMatrix<T> shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public OdPrimitiveMatrix<T> deepClone(){
    /* for a primitive matrix the deep clone is the same as a shallow copy since contents are immutable */
    return shallowClone();
  }

  /**
   * Returns an iterator which can iterate through all the origin-destination cells in the matrix
   * 
   * @return iterator through all the origin-destination cells
   */
  @Override
  public abstract OdMatrixIterator<T, Array2D<T>> iterator();
  
}
