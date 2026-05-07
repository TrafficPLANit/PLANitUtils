package org.goplanit.utils.zoning.zonetozone;

import java.util.logging.Logger;

import org.goplanit.utils.id.IdAble;
import org.goplanit.utils.id.IdGroupingToken;
import org.goplanit.utils.zoning.OdZones;
import org.goplanit.utils.zoning.Zone;
import org.goplanit.utils.zoning.Zones;

/**
 * This class contains common methods for handling zone-to-zone matrices.
 * 
 * @author gman6028, markr
 *
 */
public abstract class ZoneToZoneMatrixImpl<T, U> extends ZoneToZoneDataImpl<T> implements ZoneToZoneMatrix<T, U> {

  /** the logger */
  @SuppressWarnings("unused")
  private static final Logger LOGGER = Logger.getLogger(ZoneToZoneMatrixImpl.class.getCanonicalName());

  /**
   * matrix of data values
   */
  protected U matrixContainer;

  /**
   * Constructor for zone-to-zone matrix containing primitives, i.e. number based
   * 
   * @param idTokenClass   to use for id generation
   * @param idToken        to use for the matrix id
   * @param zones          holder for zones considered in the matrix
   * @param matrixContainer container for the matrix contents
   * @param valueClass class of the values within matrix
   */
  public ZoneToZoneMatrixImpl(
      Class<? extends IdAble> idTokenClass,
      IdGroupingToken idToken,
      Class<T> valueClass,
      Zones<? extends Zone> zones,
      U matrixContainer) {
    super(idTokenClass, idToken, valueClass, zones);
    this.matrixContainer = matrixContainer;
  }

  /**
   * Copy Constructor (shallow copy of matrix contents)
   * 
   * @param other to copy
   */
  public ZoneToZoneMatrixImpl(final ZoneToZoneMatrixImpl<T, U> other) {
    super(other);
    this.matrixContainer = other.matrixContainer;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ZoneToZoneMatrixIterator<T, U> iterator();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ZoneToZoneMatrixImpl<T, U> shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ZoneToZoneMatrixImpl<T, U> deepClone();

}
