package org.goplanit.utils.geo;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Puts points in a spatial order along a Hilbert curve, so that points near each other in the order are near each
 * other on the map.
 * <p>
 * Many tasks need things in an order that keeps neighbours together: processing them in batches that each cover one
 * area, drawing a sample spread evenly over a region, or cutting a region into areas of about equal weight by cutting
 * the ordered list into runs. Sorting by x and then y does not do that, as each row ends far from where the next one
 * begins. A Hilbert curve does. It passes once through every cell of a square grid laid over the region, always
 * moving on to a cell that shares a side with the one before. Each point takes the index of the cell it lies in, and
 * sorting by that index gives the order.
 * </p>
 * <p>
 * The points need not lie on a grid or be spread evenly. The grid is far finer than the points, 65,536 cells a side
 * by default, so nearly every point has a cell of its own, and the curve visits the points in the order it reaches
 * their cells. Points next to each other in the order lie on one unbroken stretch of the curve, which covers one
 * connected and compact area: small where the points are dense, large where they are sparse. The reverse does not
 * always hold. Two points close on the map can be far apart in the order, where the curve's quadrants meet.
 * </p>
 * <p>
 * For example, the zones of a zoning sorted by the centres of their polygons come in an order in which any run of
 * zones next to each other in the list covers one compact area, a few city blocks in the centre and a whole district
 * at the edge.
 * </p>
 * <p>
 * The curve is the one of Hilbert (1891). A cell's index is worked out as in the xy2d routine of the Wikipedia article
 * on the Hilbert curve: from the largest quadrants down, each level adds the quadrant the cell is in and turns the cell
 * so the curve within that quadrant runs the way the whole curve does. What differs: points are first placed on a
 * square grid laid over an envelope, the grid's order is the caller's, and a sort keeps the order of items in one cell.
 * </p>
 *
 * @see <a href="https://doi.org/10.1007/BF01199431">Hilbert, D. (1891). Ueber die stetige Abbildung einer Linie auf
 * ein Flaechenstueck. Mathematische Annalen 38, 459-460</a>
 * @see <a href="https://en.wikipedia.org/wiki/Hilbert_curve">Hilbert curve, Wikipedia: the xy2d routine</a>
 *
 * @author markr
 */
public class HilbertCurveUtils {

  /**
   * The cell a value falls in along one side of the grid, cells being of equal length. A value at the side's far end
   * falls in the last cell, and a side of no length has its first cell only
   *
   * @param value to place
   * @param min start of the side
   * @param length of the side
   * @param lastCell index of the side's last cell
   * @return the cell, from 0 to the last cell
   */
  private static long toCell(double value, double min, double length, long lastCell) {
    if (length <= 0) {
      return 0;
    }
    return (long) Math.min(lastCell, Math.floor((value - min) / length * (lastCell + 1)));
  }

  /** order of the grid laid over a region unless given: 2 to the power 16, so 65,536 cells a side */
  public static final int DEFAULT_ORDER = 16;

  /**
   * The index along the Hilbert curve of a cell of a square grid. The curve starts in the cell at the origin and
   * ends in the last cell along the x axis
   *
   * @param x the cell's column, from 0
   * @param y the cell's row, from 0
   * @param order the grid has 2 to the power order cells a side, from 1 to 31
   * @return the index along the curve, from 0 to the number of cells minus one
   * @throws IllegalArgumentException when the order is out of range or the cell is not on the grid
   */
  public static long createIndex(long x, long y, int order) {
    if (order < 1 || order > 31) {
      throw new IllegalArgumentException(
          String.format("Hilbert curve order %d is out of range, it must be from 1 to 31", order));
    }
    final long side = 1L << order;
    if (x < 0 || y < 0 || x >= side || y >= side) {
      throw new IllegalArgumentException(
          String.format("Cell (%d, %d) is not on a Hilbert curve grid of %d cells a side", x, y, side));
    }
    long cellX = x;
    long cellY = y;
    long index = 0;
    for (long half = side / 2; half > 0; half /= 2) {
      long quadrantX = (cellX & half) > 0 ? 1 : 0;
      long quadrantY = (cellY & half) > 0 ? 1 : 0;
      index += half * half * ((3 * quadrantX) ^ quadrantY);
      /* turn the cell so the curve within its quadrant runs the way the curve over the whole grid does */
      if (quadrantY == 0) {
        if (quadrantX == 1) {
          cellX = side - 1 - cellX;
          cellY = side - 1 - cellY;
        }
        long swap = cellX;
        cellX = cellY;
        cellY = swap;
      }
    }
    return index;
  }

  /**
   * The index along the Hilbert curve of a point. The grid is square, its side the envelope's longer one, laid from
   * the envelope's lower left corner, so its cells are square whatever the envelope's shape. A point on the
   * envelope's edge lies in the cell at that edge
   *
   * @param coordinate of the point
   * @param envelope the grid is laid over, holding the point
   * @param order the grid has 2 to the power order cells a side, from 1 to 31
   * @return the index along the curve
   * @throws IllegalArgumentException when the point lies outside the envelope, or the order is out of range
   */
  public static long createIndex(Coordinate coordinate, Envelope envelope, int order) {
    if (!envelope.covers(coordinate)) {
      throw new IllegalArgumentException(String.format(
          "Point %s lies outside the envelope %s, so it has no index on the Hilbert curve over it", coordinate,
          envelope));
    }
    final long lastCell = (1L << order) - 1;
    final double side = Math.max(envelope.getWidth(), envelope.getHeight());
    return createIndex(
        toCell(coordinate.x, envelope.getMinX(), side, lastCell),
        toCell(coordinate.y, envelope.getMinY(), side, lastCell), order);
  }

  /**
   * The index along the Hilbert curve of a point, on a grid of {@link #DEFAULT_ORDER}, see
   * {@link #createIndex(Coordinate, Envelope, int)}
   *
   * @param coordinate of the point
   * @param envelope the grid is laid over, holding the point
   * @return the index along the curve
   * @throws IllegalArgumentException when the point lies outside the envelope
   */
  public static long createIndex(Coordinate coordinate, Envelope envelope) {
    return createIndex(coordinate, envelope, DEFAULT_ORDER);
  }

  /**
   * Sort items along the Hilbert curve by their positions, on a grid of {@link #DEFAULT_ORDER} laid over the given
   * envelope. Items in the same cell keep the order they are given in, so items given in a fixed order always come
   * out in the same order
   *
   * @param <T> type of the items
   * @param items to sort
   * @param positionOf the position of an item
   * @param envelope the grid is laid over, holding every position
   * @return the items along the curve, in a new list
   * @throws IllegalArgumentException when an item has no position or its position lies outside the envelope
   */
  public static <T> List<T> sortAlongCurve(Iterable<T> items, Function<T, Coordinate> positionOf, Envelope envelope) {
    var itemList = new ArrayList<T>();
    items.forEach(itemList::add);
    long[] indices = new long[itemList.size()];
    for (int position = 0; position < itemList.size(); ++position) {
      var coordinate = positionOf.apply(itemList.get(position));
      if (coordinate == null) {
        throw new IllegalArgumentException(String.format(
            "Item %s has no position, so it has no place along the Hilbert curve", itemList.get(position)));
      }
      indices[position] = createIndex(coordinate, envelope);
    }
    /* sorting objects is stable, so items in the same cell keep their order */
    Integer[] sorted = IntStream.range(0, itemList.size()).boxed().toArray(Integer[]::new);
    Arrays.sort(sorted, Comparator.comparingLong(position -> indices[position]));
    return Arrays.stream(sorted).map(itemList::get).collect(Collectors.toList());
  }

  /**
   * Sort items along the Hilbert curve by their positions, over the envelope of those positions, see
   * {@link #sortAlongCurve(Iterable, Function, Envelope)}
   *
   * @param <T> type of the items
   * @param items to sort
   * @param positionOf the position of an item
   * @return the items along the curve, in a new list
   * @throws IllegalArgumentException when an item has no position
   */
  public static <T> List<T> sortAlongCurve(Iterable<T> items, Function<T, Coordinate> positionOf) {
    var envelope = new Envelope();
    for (var item : items) {
      var coordinate = positionOf.apply(item);
      if (coordinate != null) {
        envelope.expandToInclude(coordinate);
      }
    }
    return sortAlongCurve(items, positionOf, envelope);
  }
}
