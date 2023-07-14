package org.goplanit.utils.od;

import java.util.logging.Logger;

import org.goplanit.utils.id.IdAble;
import org.goplanit.utils.id.IdGroupingToken;
import org.goplanit.utils.zoning.OdZones;

/**
 * This class contains common methods for handling origin-demand matrices.
 * 
 * @author gman6028, markr
 *
 */
public abstract class OdMatrixImpl<T, U> extends OdDataImpl<T> implements OdMatrix<T, U> {

  /** the logger */
  @SuppressWarnings("unused")
  private static final Logger LOGGER = Logger.getLogger(OdMatrixImpl.class.getCanonicalName());

  /**
   * matrix of data values
   */
  protected U matrixContents;

  /**
   * Constructor for Od matrix containing primitives, i.e. number based
   * 
   * @param idTokenClass   to use for id generation
   * @param idToken        to use for the matrix id
   * @param zones          holder for zones considered in the matrix
   * @param matrixContents container for the matrix contents
   */
  public OdMatrixImpl(Class<? extends IdAble> idTokenClass, IdGroupingToken idToken, OdZones zones, U matrixContents) {
    super(idTokenClass, idToken, zones);
    this.matrixContents = matrixContents;
  }

  /**
   * Copy Constructor (shallow copy of matrix contents)
   * 
   * @param other to copy
   */
  public OdMatrixImpl(final OdMatrixImpl<T, U> other) {
    super(other);
    this.matrixContents = other.matrixContents;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract OdMatrixIterator<T, U> iterator();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract OdMatrixImpl<T, U> shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract OdMatrixImpl<T, U> deepClone();

}
