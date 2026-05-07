package org.goplanit.utils.zoning.zonetozone;

import java.util.logging.Logger;

import org.goplanit.utils.id.IdAble;
import org.goplanit.utils.id.IdGroupingToken;
import org.goplanit.utils.zoning.OdZones;
import org.goplanit.utils.zoning.Zone;
import org.goplanit.utils.zoning.Zones;
import org.ojalgo.array.Array2D;

/**
 * This class contains common methods for handling origin-demand matrices.
 * 
 * @author gman6028, markr
 *
 */
public abstract class ZoneToZonePrimitiveMatrix<T extends Number> extends ZoneToZoneMatrixImpl<T, Array2D<T>> {

  /** the logger */
  @SuppressWarnings("unused")
  private static final Logger LOGGER = Logger.getLogger(ZoneToZonePrimitiveMatrix.class.getCanonicalName());

  /**
   * Constructor for Od matrix containing primitives, i.e. number based
   * 
   * @param idTokenClass   to use for id generation
   * @param idToken        to use for the matrix id
   * @param valueClass class of the values in container
   * @param zones          holder for zones considered in the matrix
   * @param matrixContents container for the matrix contents
   */
  public ZoneToZonePrimitiveMatrix(
          Class<? extends IdAble> idTokenClass,
          IdGroupingToken idToken,
          Class<T> valueClass,
          Zones<? extends Zone> zones,
          Array2D<T> matrixContents) {
    super(idTokenClass, idToken, valueClass, zones, matrixContents);
  }

  /**
   * Copy Constructor
   *
   * @param other to copy
   * @param contentFactory to use
   */
  public ZoneToZonePrimitiveMatrix(ZoneToZonePrimitiveMatrix<T> other, Array2D.Factory<T> contentFactory) {
    super(other);
    this.matrixContainer = contentFactory.copy(other.matrixContainer); // shallow copy
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setValue(Zone from, Zone to, T value) {
    matrixContainer.set(from.getId(), to.getId(), value);
  }

  /**
   * {@inheritDoc}
   */
  public T getValue(Zone from, Zone to) {
    return matrixContainer.get(from.getId(), to.getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public T getValue(long from, long to) {
    return matrixContainer.get(from, to);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ZoneToZonePrimitiveMatrix<T> shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public ZoneToZonePrimitiveMatrix<T> deepClone(){
    /* for a primitive matrix the deep clone is the same as a shallow copy since contents are immutable */
    return shallowClone();
  }

  /**
   * Returns an iterator which can iterate through all the origin-destination cells in the matrix
   * 
   * @return iterator through all the origin-destination cells
   */
  @Override
  public abstract ZoneToZoneMatrixIterator<T, Array2D<T>> iterator();
  
}
