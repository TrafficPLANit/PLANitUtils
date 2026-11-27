package org.goplanit.utils.zoning.zonetozone;

import java.util.logging.Logger;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.goplanit.utils.exceptions.PlanItRunTimeException;
import org.goplanit.utils.id.IdAble;
import org.goplanit.utils.id.IdGroupingToken;
import org.goplanit.utils.unit.Unit;
import org.goplanit.utils.zoning.Zone;
import org.goplanit.utils.zoning.Zones;
import org.ojalgo.array.Array2D;

/**
 * This class contains common methods for handling origin-demand matrices, each holding its values in one unit
 * <p>
 * A value means nothing without its unit: a distance of 3 may be 3 km or 3 m. The matrix holds the unit its values
 * are in, fixed when it is made, so a reader that needs another unit converts on reading rather than assuming one.
 * </p>
 *
 * @author gman6028, markr
 *
 */
public abstract class ZoneToZonePrimitiveMatrix<T extends Number> extends ZoneToZoneMatrixImpl<T, Array2D<T>> {

  /** the logger */
  @SuppressWarnings("unused")
  private static final Logger LOGGER = Logger.getLogger(ZoneToZonePrimitiveMatrix.class.getCanonicalName());

  /** unit of the values */
  private final Unit unit;

  /**
   * Constructor for Od matrix containing primitives, i.e. number based
   *
   * @param idTokenClass   to use for id generation
   * @param idToken        to use for the matrix id
   * @param valueClass class of the values in container
   * @param zones          holder for zones considered in the matrix
   * @param matrixContents container for the matrix contents
   * @param unit           of the values, not null
   */
  public ZoneToZonePrimitiveMatrix(
          Class<? extends IdAble> idTokenClass,
          IdGroupingToken idToken,
          Class<T> valueClass,
          Zones<? extends Zone> zones,
          Array2D<T> matrixContents,
          Unit unit) {
    super(idTokenClass, idToken, valueClass, zones, matrixContents);
    PlanItRunTimeException.throwIfNull(unit, "Unit of the matrix's values is null, not allowed");
    this.unit = unit;
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
    this.unit = other.unit;
  }

  /**
   * The unit the values are in
   *
   * @return the unit
   */
  public Unit getUnit() {
    return unit;
  }

  /**
   * Whether the values are in the given unit, so a reader that checks once can read them as they are, without
   * converting each
   *
   * @param otherUnit to check
   * @return true when the values are in it
   */
  public boolean isInUnit(Unit otherUnit) {
    return otherUnit == unit || unit.equals(otherUnit);
  }

  /**
   * The value of a cell in the given unit, converted when it is not the unit of the matrix
   *
   * @param from origin zone
   * @param to destination zone
   * @param toUnit to read the value in, one the unit of the matrix converts to
   * @return the value in the given unit
   */
  public double getValueConvertedTo(Zone from, Zone to, Unit toUnit) {
    double value = getValue(from, to).doubleValue();
    if (isInUnit(toUnit)) {
      return value;
    }
    PlanItRunTimeException.throwIf(!unit.canConvertTo(toUnit),
        "Values of the matrix are in %s, unable to read them in %s", unit, toUnit);
    return unit.convertTo(toUnit, value);
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
  public abstract @NonNull ZoneToZoneMatrixIterator<T, Array2D<T>> iterator();
  
}
