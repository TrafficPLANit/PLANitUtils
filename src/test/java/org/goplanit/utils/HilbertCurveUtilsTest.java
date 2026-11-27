package org.goplanit.utils;

import org.goplanit.utils.geo.HilbertCurveUtils;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests of {@link HilbertCurveUtils}
 */
public class HilbertCurveUtilsTest {

  /**
   * The 16 cells of a 4 by 4 grid come in the known Hilbert order, the one of the d2xy algorithm, starting in the
   * corner at the origin and ending in the corner at the end of the x axis:
   * <pre>
   *   y=3    5  6  9 10
   *   y=2    4  7  8 11
   *   y=1    3  2 13 12
   *   y=0    0  1 14 15
   *        x=0  1  2  3
   * </pre>
   */
  @Test
  public void testFourByFourGridInKnownOrder() {
    int[][] cellsInOrder = {
        {0, 0}, {1, 0}, {1, 1}, {0, 1}, {0, 2}, {0, 3}, {1, 3}, {1, 2},
        {2, 2}, {2, 3}, {3, 3}, {3, 2}, {3, 1}, {2, 1}, {2, 0}, {3, 0}};
    for (int index = 0; index < cellsInOrder.length; ++index) {
      assertEquals(index, HilbertCurveUtils.createIndex(cellsInOrder[index][0], cellsInOrder[index][1], 2));
    }
  }

  /** On a 64 by 64 grid every cell has its own index, and any two cells whose indices follow each other share a side */
  @Test
  public void testCellsFollowingEachOtherShareASide() {
    final int order = 6;
    final int side = 1 << order;
    int[][] cellOfIndex = new int[side * side][];
    for (int x = 0; x < side; ++x) {
      for (int y = 0; y < side; ++y) {
        int index = (int) HilbertCurveUtils.createIndex(x, y, order);
        assertTrue(index >= 0 && index < side * side);
        assertEquals(null, cellOfIndex[index]);
        cellOfIndex[index] = new int[]{x, y};
      }
    }
    for (int index = 1; index < cellOfIndex.length; ++index) {
      int dx = Math.abs(cellOfIndex[index][0] - cellOfIndex[index - 1][0]);
      int dy = Math.abs(cellOfIndex[index][1] - cellOfIndex[index - 1][1]);
      assertEquals(1, dx + dy);
    }
  }

  /** A point on the envelope's edge gets the index of the cell at that edge, the origin's corner index 0 */
  @Test
  public void testPointOnEnvelopeEdgeGetsAnIndex() {
    var envelope = new Envelope(0, 100, 0, 100);
    final int lastCell = (1 << HilbertCurveUtils.DEFAULT_ORDER) - 1;
    assertEquals(0, HilbertCurveUtils.createIndex(new Coordinate(0, 0), envelope));
    assertEquals(HilbertCurveUtils.createIndex(lastCell, lastCell, HilbertCurveUtils.DEFAULT_ORDER),
        HilbertCurveUtils.createIndex(new Coordinate(100, 100), envelope));
  }

  /** A point outside the envelope is refused */
  @Test
  public void testPointOutsideEnvelopeRefused() {
    var envelope = new Envelope(0, 100, 0, 100);
    assertThrows(IllegalArgumentException.class,
        () -> HilbertCurveUtils.createIndex(new Coordinate(101, 50), envelope));
  }

  /**
   * The grid over an envelope twice as wide as high has square cells, its side the width: the top right corner falls
   * in the last column and in the middle row
   */
  @Test
  public void testWideEnvelopeHasSquareCells() {
    var envelope = new Envelope(0, 200, 0, 100);
    final int cellsPerSide = 1 << HilbertCurveUtils.DEFAULT_ORDER;
    assertEquals(HilbertCurveUtils.createIndex(cellsPerSide - 1, cellsPerSide / 2, HilbertCurveUtils.DEFAULT_ORDER),
        HilbertCurveUtils.createIndex(new Coordinate(200, 100), envelope));
  }

  /**
   * Points in the four quadrants of a square, given shuffled, come out in the order the curve visits the quadrants:
   * <pre>
   *   top left (2)     top right (3)
   *   bottom left (1)  bottom right (4)
   * </pre>
   */
  @Test
  public void testSortAlongCurveVisitsQuadrantsInOrder() {
    var bottomLeft = new Coordinate(25, 25);
    var topLeft = new Coordinate(25, 75);
    var topRight = new Coordinate(75, 75);
    var bottomRight = new Coordinate(75, 25);
    var sorted = HilbertCurveUtils.sortAlongCurve(
        List.of(topRight, bottomRight, bottomLeft, topLeft), coordinate -> coordinate, new Envelope(0, 100, 0, 100));
    assertEquals(List.of(bottomLeft, topLeft, topRight, bottomRight), sorted);
  }

  /** Two items at the same position keep the order they are given in, either way round */
  @Test
  public void testItemsAtTheSamePositionKeepTheirOrder() {
    var spot = new Coordinate(10, 10);
    var corner = new Coordinate(0, 0);
    var far = new Coordinate(100, 100);
    assertEquals(List.of("a", "b"),
        HilbertCurveUtils.sortAlongCurve(List.of("a", "b"), name -> spot, new Envelope(corner, far)));
    assertEquals(List.of("b", "a"),
        HilbertCurveUtils.sortAlongCurve(List.of("b", "a"), name -> spot, new Envelope(corner, far)));
  }

  /** An item without a position is refused */
  @Test
  public void testItemWithoutPositionRefused() {
    assertThrows(IllegalArgumentException.class,
        () -> HilbertCurveUtils.sortAlongCurve(List.of("a"), name -> null, new Envelope(0, 100, 0, 100)));
  }
}
