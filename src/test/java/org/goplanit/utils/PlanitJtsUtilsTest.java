package org.goplanit.utils;

import org.geotools.geometry.Position2D;
import org.goplanit.utils.exceptions.PlanItException;
import org.goplanit.utils.exceptions.PlanItRunTimeException;
import org.goplanit.utils.geo.PlanitJtsCrsUtils;
import org.goplanit.utils.geo.PlanitJtsUtils;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.linearref.LinearLocation;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests of {@link PlanitJtsUtils}: transformations, creating coordinates, points, lines and polygons, closing rings,
 * copying, combining and splitting line strings, and the measures and conversions
 *
 * @author markr
 */
public class PlanitJtsUtilsTest {

  /** A multi point holds the given points in the given order */
  @Test
  public void multiPointHoldsGivenPointsInOrder() {
    MultiPoint multiPoint = PlanitJtsUtils.createMultiPoint(
        PlanitJtsUtils.createPoint(1, 2), PlanitJtsUtils.createPoint(3, 4), PlanitJtsUtils.createPoint(5, 6));

    assertEquals(3, multiPoint.getNumGeometries());
    assertEquals(new Coordinate(1, 2), multiPoint.getGeometryN(0).getCoordinate());
    assertEquals(new Coordinate(3, 4), multiPoint.getGeometryN(1).getCoordinate());
    assertEquals(new Coordinate(5, 6), multiPoint.getGeometryN(2).getCoordinate());
  }

  /** A multi point of a single point holds that point, and one of no points is empty */
  @Test
  public void multiPointOfOneOrNoPoints() {
    MultiPoint single = PlanitJtsUtils.createMultiPoint(PlanitJtsUtils.createPoint(7, 8));
    assertEquals(1, single.getNumGeometries());
    assertEquals(new Coordinate(7, 8), single.getCoordinate());

    assertTrue(PlanitJtsUtils.createMultiPoint().isEmpty());
  }

  // TRANSFORMATIONS

  /** A transform from WGS84 to Web Mercator gives the Web Mercator formula's result, longitude first */
  @Test
  public void mathTransformFromGeographicToWebMercator() {
    var transform = PlanitJtsUtils.findMathTransform(
        PlanitJtsCrsUtils.DEFAULT_GEOGRAPHIC_CRS, PlanitJtsCrsUtils.DEFAULT_PROJECTED_CRS_EPSG_3857);
    var projected = PlanitJtsUtils.transformGeometry(PlanitJtsUtils.createPoint(151.2, -33.87), transform);

    double earthRadius = 6378137;
    assertEquals(earthRadius * Math.toRadians(151.2), projected.getCoordinate().x, 0.01);
    assertEquals(earthRadius * Math.log(Math.tan(Math.PI / 4 + Math.toRadians(-33.87) / 2)),
        projected.getCoordinate().y, 0.01);
  }

  /** A transform needs both coordinate reference systems */
  @Test
  public void mathTransformRejectsMissingCrs() {
    assertThrows(PlanItRunTimeException.class,
        () -> PlanitJtsUtils.findMathTransform(null, PlanitJtsCrsUtils.DEFAULT_PROJECTED_CRS_EPSG_3857));
    assertThrows(PlanItRunTimeException.class,
        () -> PlanitJtsUtils.findMathTransform(PlanitJtsCrsUtils.DEFAULT_GEOGRAPHIC_CRS, null));
  }

  /** Transforming and transforming back gives the original geometry */
  @Test
  public void transformGeometryAndBack() throws Exception {
    var transform = PlanitJtsUtils.findMathTransform(
        PlanitJtsCrsUtils.DEFAULT_GEOGRAPHIC_CRS, PlanitJtsCrsUtils.DEFAULT_PROJECTED_CRS_EPSG_3857);
    var line = PlanitJtsUtils.createLineString(new Coordinate(151.2, -33.87), new Coordinate(151.21, -33.86));

    var back = PlanitJtsUtils.transformGeometry(PlanitJtsUtils.transformGeometry(line, transform), transform.inverse());
    assertTrue(back.equalsExact(line, 1e-9));
  }

  /** The safe transform returns the geometry itself without a transform, and null without a geometry */
  @Test
  public void transformGeometrySafeWithoutTransformOrGeometry() {
    var point = PlanitJtsUtils.createPoint(1, 2);
    assertSame(point, PlanitJtsUtils.transformGeometrySafe(point, null));
    assertNull(PlanitJtsUtils.transformGeometrySafe(null, null));
  }

  /** An envelope transforms by its two corners */
  @Test
  public void transformEnvelopeByItsCorners() {
    var transform = PlanitJtsUtils.findMathTransform(
        PlanitJtsCrsUtils.DEFAULT_GEOGRAPHIC_CRS, PlanitJtsCrsUtils.DEFAULT_PROJECTED_CRS_EPSG_3857);
    var envelope = new Envelope(151.2, 151.21, -33.87, -33.86);

    var transformed = PlanitJtsUtils.transformEnvelope(envelope, transform);
    var min = PlanitJtsUtils.transformGeometry(PlanitJtsUtils.createPoint(151.2, -33.87), transform).getCoordinate();
    var max = PlanitJtsUtils.transformGeometry(PlanitJtsUtils.createPoint(151.21, -33.86), transform).getCoordinate();
    assertEquals(new Envelope(min, max), transformed);
  }

  // CREATING COORDINATES, POINTS AND LINES

  /** A coordinate takes the first two ordinates of a position, x then y */
  @Test
  public void coordinatesFromPositions() {
    assertEquals(new Coordinate(3, 4), PlanitJtsUtils.createCoordinate(new Position2D(3, 4)));
    assertArrayEquals(new Coordinate[]{new Coordinate(1, 2), new Coordinate(3, 4)},
        PlanitJtsUtils.directPositionsToCoordinates(List.of(new Position2D(1, 2), new Position2D(3, 4))));
  }

  /** A point is made from a coordinate, or from an x and a y */
  @Test
  public void pointsFromCoordinateOrOrdinates() {
    assertEquals(new Coordinate(1, 2), PlanitJtsUtils.createPoint(new Coordinate(1, 2)).getCoordinate());
    assertEquals(new Coordinate(3, 4), PlanitJtsUtils.createPoint(3, 4.0).getCoordinate());
  }

  /** A line segment runs between the two coordinates given */
  @Test
  public void lineSegmentBetweenCoordinates() {
    var segment = PlanitJtsUtils.createLineSegment(new Coordinate(0, 0), new Coordinate(3, 4));
    assertEquals(new Coordinate(0, 0), segment.p0);
    assertEquals(new Coordinate(3, 4), segment.p1);
    assertEquals(5, segment.getLength(), 1e-9);
  }

  /** A flat list of ordinates gives one coordinate per x, y pair, and an odd number of ordinates is refused */
  @Test
  public void coordinatesFromFlatList() {
    assertArrayEquals(new Coordinate[]{new Coordinate(1, 2), new Coordinate(3, 4)},
        PlanitJtsUtils.createCoordinates(List.of(1.0, 2.0, 3.0, 4.0)));
    assertThrows(PlanItRunTimeException.class, () -> PlanitJtsUtils.createCoordinates(List.of(1.0, 2.0, 3.0)));
  }

  /** A line string is made from coordinates, from a flat list of ordinates, or from text */
  @Test
  public void lineStringsFromCoordinatesListOrText() {
    var expected = PlanitJtsUtils.createLineString(new Coordinate(1, 2), new Coordinate(3, 4));
    assertTrue(expected.equalsExact(PlanitJtsUtils.createLineString(List.of(1.0, 2.0, 3.0, 4.0))));
    assertTrue(expected.equalsExact(PlanitJtsUtils.createLineString("1,2;3,4", ';', ',')));
    assertTrue(expected.equalsExact(PlanitJtsUtils.createLineStringFromCsvString("1 2,3 4", ",", " ")));
  }

  /** Text with a tuple of other than two ordinates, or separators longer than one character, is refused */
  @Test
  public void lineStringFromInvalidText() {
    assertThrows(PlanItRunTimeException.class, () -> PlanitJtsUtils.createLineString("1,2,3;4,5", ';', ','));
    assertThrows(PlanItRunTimeException.class,
        () -> PlanitJtsUtils.createLineStringFromCsvString("1,2;3,4", ";;", ","));
    assertThrows(PlanItRunTimeException.class,
        () -> PlanitJtsUtils.createLineStringFromCsvString("1,2;3,4", ";", ",,"));
  }

  /** Coordinates written as text use the separators and decimal format given, with no separator at the end */
  @Test
  public void csvStringFromCoordinates() {
    var format = new DecimalFormat("0.0", DecimalFormatSymbols.getInstance(Locale.ROOT));
    assertEquals("1.0,2.0;3.5,4.0", PlanitJtsUtils.createCsvStringFromCoordinates(
        new Coordinate[]{new Coordinate(1, 2), new Coordinate(3.5, 4)}, ';', ',', format));
  }

  /** A multi line string holds the line strings given */
  @Test
  public void multiLineStringHoldsLineStrings() {
    var first = PlanitJtsUtils.createLineString(new Coordinate(0, 0), new Coordinate(1, 0));
    var second = PlanitJtsUtils.createLineString(new Coordinate(5, 5), new Coordinate(6, 5));
    var multi = PlanitJtsUtils.createMultiLineString(first, second);
    assertEquals(2, multi.getNumGeometries());
    assertTrue(second.equalsExact(multi.getGeometryN(1)));
  }

  /** Coordinates from a list of values that read as numbers, a trailing unpaired value left out */
  @Test
  public void listTo2DCoordinates() {
    assertArrayEquals(new Coordinate[]{new Coordinate(1, 2), new Coordinate(3, 4)},
        PlanitJtsUtils.listTo2DCoordinates(List.of("1", "2", 3.0, 4)));
    assertArrayEquals(new Coordinate[]{new Coordinate(1, 2)}, PlanitJtsUtils.listTo2DCoordinates(List.of(1, 2, 3)));
  }

  // POLYGONS

  /** Polygons from coordinates, a flat list of ordinates, an envelope, or with holes; and an empty one */
  @Test
  public void polygonsOfAllKinds() {
    var square = new Coordinate[]{
        new Coordinate(0, 0), new Coordinate(0, 2), new Coordinate(2, 2), new Coordinate(2, 0), new Coordinate(0, 0)};
    assertEquals(4, PlanitJtsUtils.createPolygon(square).getArea(), 1e-9);
    assertEquals(4, PlanitJtsUtils.create2DPolygon(List.of(0.0, 0.0, 0.0, 2.0, 2.0, 2.0, 2.0, 0.0, 0.0, 0.0)).getArea(),
        1e-9);
    assertEquals(6, PlanitJtsUtils.create2DPolygon(new Envelope(1, 4, 1, 3)).getArea(), 1e-9);
    assertTrue(PlanitJtsUtils.createPolygon().isEmpty());

    var outer = PlanitJtsUtils.create2DPolygon(new Envelope(0, 10, 0, 10)).getExteriorRing();
    var hole = PlanitJtsUtils.create2DPolygon(new Envelope(2, 4, 2, 4)).getExteriorRing();
    var withHole = PlanitJtsUtils.createPolygonWithHoles(outer, new ArrayList<>(List.of(hole)));
    assertEquals(1, withHole.getNumInteriorRing());
    assertEquals(96, withHole.getArea(), 1e-9);
  }

  /** A multi polygon holds the polygons given, and is empty without any */
  @Test
  public void multiPolygonHoldsPolygons() {
    var first = PlanitJtsUtils.create2DPolygon(new Envelope(0, 1, 0, 1));
    var second = PlanitJtsUtils.create2DPolygon(new Envelope(5, 6, 5, 6));
    assertEquals(2, PlanitJtsUtils.createMultiPolygon(new Polygon[]{first, second}).getNumGeometries());
    assertTrue(PlanitJtsUtils.createMultiPolygon(null).isEmpty());
    assertTrue(PlanitJtsUtils.createMultiPolygon(new Polygon[0]).isEmpty());
  }

  /** The centre of a polygon is its centroid, of a multi polygon the centroid of its largest polygon, and anything
   * else has none */
  @Test
  public void polygonCentre() {
    var small = PlanitJtsUtils.create2DPolygon(new Envelope(0, 1, 0, 1));
    var large = PlanitJtsUtils.create2DPolygon(new Envelope(10, 13, 10, 13));
    assertEquals(new Coordinate(0.5, 0.5), PlanitJtsUtils.extractPolygonCentre(small).getCoordinate());
    assertEquals(new Coordinate(11.5, 11.5), PlanitJtsUtils.extractPolygonCentre(
        PlanitJtsUtils.createMultiPolygon(new Polygon[]{small, large})).getCoordinate());

    assertNull(PlanitJtsUtils.extractPolygonCentre(
        PlanitJtsUtils.createLineString(new Coordinate(0, 0), new Coordinate(1, 1))));
    assertNull(PlanitJtsUtils.extractPolygonCentre(null));
    assertNull(PlanitJtsUtils.extractPolygonCentre(PlanitJtsUtils.createPolygon()));
  }

  // CLOSED RINGS

  /** A copy without null entries keeps the order of the others, and is null when all entries are null */
  @Test
  public void copyWithoutNullEntries() {
    var a = new Coordinate(1, 1);
    var b = new Coordinate(2, 2);
    assertArrayEquals(
        new Coordinate[]{a, b}, PlanitJtsUtils.copyWithoutNullEntries(new Coordinate[]{null, a, null, b}));
    assertNull(PlanitJtsUtils.copyWithoutNullEntries(new Coordinate[]{null, null}));
  }

  /** Coordinates are closed when there are more than two and the last equals the first in 2D */
  @Test
  public void isClosed2D() {
    var a = new Coordinate(0, 0);
    var b = new Coordinate(1, 0);
    var c = new Coordinate(1, 1);
    assertTrue(PlanitJtsUtils.isClosed2D(new Coordinate[]{a, b, c, new Coordinate(0, 0, 5)}));
    assertFalse(PlanitJtsUtils.isClosed2D(new Coordinate[]{a, b, c}));
    assertFalse(PlanitJtsUtils.isClosed2D(new Coordinate[]{a, a}));
    assertFalse(PlanitJtsUtils.isClosed2D((Coordinate[]) null));

    assertTrue(PlanitJtsUtils.isClosed2D(List.of(0.0, 0.0, 1.0, 0.0, 0.0, 0.0)));
    assertFalse(PlanitJtsUtils.isClosed2D(List.of(0.0, 0.0, 1.0, 0.0, 1.0, 1.0)));
    assertFalse(PlanitJtsUtils.isClosed2D(List.of(0.0, 0.0, 0.0, 0.0)));

    assertTrue(PlanitJtsUtils.isClosed2D(new LinkedList<>(List.of(a, b, c, a))));
    assertFalse(PlanitJtsUtils.isClosed2D(new LinkedList<>(List.of(a, b, c))));

    assertTrue(PlanitJtsUtils.isClosed2D(PlanitJtsUtils.create2DPolygon(new Envelope(0, 1, 0, 1))));
  }

  /** Coordinates close by appending the first, are returned as they are when closed, and two are needed */
  @Test
  public void makeClosed2DCoordinates() {
    var a = new Coordinate(0, 0);
    var b = new Coordinate(1, 0);
    var c = new Coordinate(1, 1);
    assertArrayEquals(new Coordinate[]{a, b, c, a}, PlanitJtsUtils.makeClosed2D(new Coordinate[]{a, b, c}));
    var closed = new Coordinate[]{a, b, c, a};
    assertSame(closed, PlanitJtsUtils.makeClosed2D(closed));
    assertThrows(PlanItRunTimeException.class, () -> PlanitJtsUtils.makeClosed2D(new Coordinate[]{a}));
  }

  /** A flat list of ordinates closes by appending the first pair, and needs three coordinates */
  @Test
  public void makeClosed2DFlatList() {
    assertEquals(List.of(0.0, 0.0, 1.0, 0.0, 1.0, 1.0, 0.0, 0.0),
        PlanitJtsUtils.makeClosed2D(List.of(0.0, 0.0, 1.0, 0.0, 1.0, 1.0)));
    var closed = List.of(0.0, 0.0, 1.0, 0.0, 0.0, 0.0);
    assertSame(closed, PlanitJtsUtils.makeClosed2D(closed));
    assertThrows(PlanItRunTimeException.class, () -> PlanitJtsUtils.makeClosed2D(List.of(0.0, 0.0, 1.0, 0.0)));
  }

  /** A linked list of coordinates closes by adding, at the end, a coordinate matching the first */
  @Test
  public void makeClosed2DLinkedList() {
    var a = new Coordinate(0, 0);
    var b = new Coordinate(1, 0);
    var c = new Coordinate(1, 1);
    var closed = PlanitJtsUtils.makeClosed2D(new LinkedList<>(List.of(a, b, c)));
    assertEquals(List.of(a, b, c, a), closed);
    assertThrows(PlanItRunTimeException.class, () -> PlanitJtsUtils.makeClosed2D(new LinkedList<>(List.of(a))));
  }

  /** A polygon is always closed, so it is returned as it is */
  @Test
  public void makeClosed2DPolygon() {
    var polygon = PlanitJtsUtils.create2DPolygon(new Envelope(0, 1, 0, 1));
    assertSame(polygon, PlanitJtsUtils.makeClosed2D(polygon));
  }

  /** A gap between first and last below the tolerance is closed by setting the last to the first; a closed list is
   * returned as it is, and a larger gap is left */
  @Test
  public void makeClosed2DWithinTolerance() {
    var closed = List.of(0.0, 0.0, 1.0, 0.0, 0.0, 0.0);
    assertSame(closed, PlanitJtsUtils.makeClosed2DWithinTolerance(closed, 1e-6));
    assertEquals(List.of(0.0, 0.0, 1.0, 0.0, 0.0, 0.0),
        PlanitJtsUtils.makeClosed2DWithinTolerance(List.of(0.0, 0.0, 1.0, 0.0, 1e-9, -1e-9), 1e-6));
    var wideGap = List.of(0.0, 0.0, 1.0, 0.0, 0.5, 0.5);
    assertEquals(wideGap, PlanitJtsUtils.makeClosed2DWithinTolerance(wideGap, 1e-6));
  }

  // COPYING PARTS OF LINE STRINGS

  /** line string (0,0) (1,0) (2,0) (3,0) used for the copies */
  private static LineString fourPointLine() {
    return PlanitJtsUtils.createLineString(
        new Coordinate(0, 0), new Coordinate(1, 0), new Coordinate(2, 0), new Coordinate(3, 0));
  }

  /** A copy from a position keeps that position and everything after; a position not on the line, or one leaving a
   * single coordinate, is refused */
  @Test
  public void copyWithoutCoordinatesBeforePosition() {
    var copy = PlanitJtsUtils.createCopyWithoutCoordinatesBefore(PlanitJtsUtils.createPoint(1, 0), fourPointLine());
    assertTrue(copy.equalsExact(PlanitJtsUtils.createLineString(
        new Coordinate(1, 0), new Coordinate(2, 0), new Coordinate(3, 0))));
    assertThrows(PlanItRunTimeException.class,
        () -> PlanitJtsUtils.createCopyWithoutCoordinatesBefore(PlanitJtsUtils.createPoint(9, 9), fourPointLine()));
    assertThrows(PlanItRunTimeException.class,
        () -> PlanitJtsUtils.createCopyWithoutCoordinatesBefore(PlanitJtsUtils.createPoint(3, 0), fourPointLine()));
  }

  /** A copy from an index keeps that index and everything after; an index outside the line is refused */
  @Test
  public void copyWithoutCoordinatesBeforeIndex() {
    var copy = PlanitJtsUtils.createCopyWithoutCoordinatesBefore(2, fourPointLine());
    assertTrue(copy.equalsExact(PlanitJtsUtils.createLineString(new Coordinate(2, 0), new Coordinate(3, 0))));
    assertThrows(PlanItRunTimeException.class,
        () -> PlanitJtsUtils.createCopyWithoutCoordinatesBefore(4, fourPointLine()));
    assertThrows(PlanItRunTimeException.class,
        () -> PlanitJtsUtils.createCopyWithoutCoordinatesBefore(-1, fourPointLine()));
  }

  /** A copy up to a position keeps everything before and that position; a position not on the line is refused */
  @Test
  public void copyWithoutCoordinatesAfterPosition() {
    var copy = PlanitJtsUtils.createCopyWithoutCoordinatesAfter(PlanitJtsUtils.createPoint(1, 0), fourPointLine());
    assertTrue(copy.equalsExact(PlanitJtsUtils.createLineString(new Coordinate(0, 0), new Coordinate(1, 0))));
    assertThrows(PlanItRunTimeException.class,
        () -> PlanitJtsUtils.createCopyWithoutCoordinatesAfter(PlanitJtsUtils.createPoint(9, 9), fourPointLine()));
  }

  /** A copy up to an index keeps everything before and that index; no line gives no copy, and an index outside the
   * line is refused */
  @Test
  public void copyWithoutCoordinatesAfterIndex() {
    var copy = PlanitJtsUtils.createCopyWithoutCoordinatesAfter(1, fourPointLine());
    assertTrue(copy.equalsExact(PlanitJtsUtils.createLineString(new Coordinate(0, 0), new Coordinate(1, 0))));
    assertNull(PlanitJtsUtils.createCopyWithoutCoordinatesAfter(1, null));
    assertThrows(PlanItRunTimeException.class,
        () -> PlanitJtsUtils.createCopyWithoutCoordinatesAfter(4, fourPointLine()));
  }

  /** A copy without adjacent duplicates keeps one of each run of equal coordinates; no line gives no copy */
  @Test
  public void copyWithoutAdjacentDuplicateCoordinates() {
    var line = PlanitJtsUtils.createLineString(new Coordinate(0, 0), new Coordinate(0, 0), new Coordinate(1, 0),
        new Coordinate(1, 0), new Coordinate(1, 0), new Coordinate(2, 0));
    assertTrue(PlanitJtsUtils.createCopyWithoutAdjacentDuplicateCoordinates(line).equalsExact(
        PlanitJtsUtils.createLineString(new Coordinate(0, 0), new Coordinate(1, 0), new Coordinate(2, 0))));
    assertNull(PlanitJtsUtils.createCopyWithoutAdjacentDuplicateCoordinates(null));
  }

  /** The first position of a coordinate, from an offset and within a tolerance; empty when not found or nothing is
   * given */
  @Test
  public void findFirstCoordinatePosition() {
    var line = PlanitJtsUtils.createLineString(
        new Coordinate(0, 0), new Coordinate(1, 0), new Coordinate(0, 0), new Coordinate(2, 0));
    assertEquals(Optional.of(0), PlanitJtsUtils.findFirstCoordinatePosition(new Coordinate(0, 0), line, 0));
    assertEquals(Optional.of(2), PlanitJtsUtils.findFirstCoordinatePosition(new Coordinate(0, 0), 1, line, 0));
    assertEquals(Optional.of(1), PlanitJtsUtils.findFirstCoordinatePosition(new Coordinate(1.001, 0), line, 0.01));
    assertEquals(Optional.empty(), PlanitJtsUtils.findFirstCoordinatePosition(new Coordinate(1.001, 0), line, 0));
    assertEquals(Optional.empty(), PlanitJtsUtils.findFirstCoordinatePosition(null, line, 0));
    assertEquals(Optional.empty(), PlanitJtsUtils.findFirstCoordinatePosition(new Coordinate(0, 0), null, 0));
  }

  /** Coordinates copied from an offset (included), up to a position (not included), or between the two */
  @Test
  public void copyCoordinatesWithinBounds() {
    assertArrayEquals(new Coordinate[]{new Coordinate(2, 0), new Coordinate(3, 0)},
        PlanitJtsUtils.copyCoordinatesFrom(2, fourPointLine()));
    assertArrayEquals(new Coordinate[]{new Coordinate(0, 0), new Coordinate(1, 0)},
        PlanitJtsUtils.copyCoordinatesUpToNotIncluding(2, fourPointLine()));
    assertArrayEquals(new Coordinate[]{new Coordinate(1, 0), new Coordinate(2, 0)},
        PlanitJtsUtils.copyCoordinatesFromUpToNotIncluding(1, 3, fourPointLine()));
  }

  /** Copying coordinates out of bounds gives an empty array, from an offset, up to a position, or between the two */
  @Test
  public void copyCoordinatesOutOfBounds() {
    assertArrayEquals(new Coordinate[0], PlanitJtsUtils.copyCoordinatesFrom(9, fourPointLine()));
    assertArrayEquals(new Coordinate[0], PlanitJtsUtils.copyCoordinatesUpToNotIncluding(9, fourPointLine()));
    assertArrayEquals(new Coordinate[0], PlanitJtsUtils.copyCoordinatesFromUpToNotIncluding(9, 10, fourPointLine()));
    assertArrayEquals(new Coordinate[0], PlanitJtsUtils.copyCoordinatesFromUpToNotIncluding(-1, 2, fourPointLine()));
  }

  // COMBINING AND SPLITTING LINE STRINGS

  /** Concatenating copies all coordinates in order, so a shared end coordinate appears twice */
  @Test
  public void concatenateCopiesAllCoordinates() {
    var first = PlanitJtsUtils.createLineString(new Coordinate(0, 0), new Coordinate(1, 0));
    var second = PlanitJtsUtils.createLineString(new Coordinate(1, 0), new Coordinate(2, 0));
    assertArrayEquals(new Coordinate[]{
            new Coordinate(0, 0), new Coordinate(1, 0), new Coordinate(1, 0), new Coordinate(2, 0)},
        PlanitJtsUtils.concatenate(first, second).getCoordinates());
  }

  /** A multi line string of one line string converts to it, one of more is refused */
  @Test
  public void convertToLineString() throws PlanItException {
    var first = PlanitJtsUtils.createLineString(new Coordinate(0, 0), new Coordinate(1, 0));
    var second = PlanitJtsUtils.createLineString(new Coordinate(5, 5), new Coordinate(6, 5));
    assertTrue(first.equalsExact(PlanitJtsUtils.convertToLineString(PlanitJtsUtils.createMultiLineString(first))));
    assertThrows(PlanItException.class,
        () -> PlanitJtsUtils.convertToLineString(PlanitJtsUtils.createMultiLineString(first, second)));
  }

  /** Splitting gives the part up to and the part from the location, sharing it; at the end there is no second part */
  @Test
  public void splitLineString() {
    var line = PlanitJtsUtils.createLineString(new Coordinate(0, 0), new Coordinate(10, 0));
    var halves = PlanitJtsUtils.splitLineString(line, new LinearLocation(0, 0.5));
    assertTrue(halves.first().equalsExact(PlanitJtsUtils.createLineString(new Coordinate(0, 0), new Coordinate(5, 0))));
    assertTrue(halves.second().equalsExact(
        PlanitJtsUtils.createLineString(new Coordinate(5, 0), new Coordinate(10, 0))));

    var atEnd = PlanitJtsUtils.splitLineString(line, new LinearLocation(0, 1.0));
    assertTrue(atEnd.first().equalsExact(line));
    assertNull(atEnd.second());
  }

  /** Two line strings sharing an end merge into one; with one missing the other is copied */
  @Test
  public void mergeLineStrings() {
    var first = PlanitJtsUtils.createLineString(new Coordinate(0, 0), new Coordinate(1, 0));
    var second = PlanitJtsUtils.createLineString(new Coordinate(1, 0), new Coordinate(2, 0));
    var merged = PlanitJtsUtils.mergeLineStrings(first, second);
    assertEquals(3, merged.getNumPoints());
    assertEquals(Set.of(new Coordinate(0, 0), new Coordinate(2, 0)),
        Set.of(merged.getStartPoint().getCoordinate(), merged.getEndPoint().getCoordinate()));

    var copy = PlanitJtsUtils.mergeLineStrings(null, second);
    assertNotSame(second, copy);
    assertTrue(second.equalsExact(copy));
    assertTrue(first.equalsExact(PlanitJtsUtils.mergeLineStrings(first, null)));
  }

  /** Merging gives null when both line strings are missing, or when they have no point in common */
  @Test
  public void mergeLineStringsWithoutResult() {
    assertNull(PlanitJtsUtils.mergeLineStrings(null, null));
    var first = PlanitJtsUtils.createLineString(new Coordinate(0, 0), new Coordinate(1, 0));
    var apart = PlanitJtsUtils.createLineString(new Coordinate(5, 5), new Coordinate(6, 5));
    assertNull(PlanitJtsUtils.mergeLineStrings(first, apart));
  }

  /** Adding coordinates to a contiguous run at whichever end they share, reversed where needed, the shared coordinate
   * once; coordinates sharing no end are not added */
  @Test
  public void addCoordsContiguous() {
    var a = new Coordinate(0, 0);
    var b = new Coordinate(1, 0);
    var c = new Coordinate(2, 0);
    var x = new Coordinate(3, 0);
    var y = new Coordinate(4, 0);

    Deque<Coordinate> run = new ArrayDeque<>(List.of(a, b, c));
    assertTrue(PlanitJtsUtils.addCoordsContiguous(run, new Coordinate[]{c, x, y}));
    assertEquals(List.of(a, b, c, x, y), new ArrayList<>(run));

    run = new ArrayDeque<>(List.of(a, b, c));
    assertTrue(PlanitJtsUtils.addCoordsContiguous(run, new Coordinate[]{y, x, c}));
    assertEquals(List.of(a, b, c, x, y), new ArrayList<>(run));

    run = new ArrayDeque<>(List.of(a, b, c));
    assertTrue(PlanitJtsUtils.addCoordsContiguous(run, new Coordinate[]{a, x, y}));
    assertEquals(List.of(y, x, a, b, c), new ArrayList<>(run));

    run = new ArrayDeque<>(List.of(a, b, c));
    assertTrue(PlanitJtsUtils.addCoordsContiguous(run, new Coordinate[]{y, x, a}));
    assertEquals(List.of(y, x, a, b, c), new ArrayList<>(run));

    run = new ArrayDeque<>(List.of(a, b, c));
    assertFalse(PlanitJtsUtils.addCoordsContiguous(run, new Coordinate[]{x, y}));
    assertEquals(List.of(a, b, c), new ArrayList<>(run));
  }

  // MEASURES AND CONVERSIONS

  /** A coordinate is left of a line segment when it lies left looking from its start to its end, right when right, and
   * neither when on its line */
  @Test
  public void coordinateLeftOrRightOf() {
    var start = new Coordinate(0, 0);
    var end = new Coordinate(10, 0);
    assertTrue(PlanitJtsUtils.isCoordinateLeftOf(new Coordinate(5, 1), start, end));
    assertFalse(PlanitJtsUtils.isCoordinateRightOf(new Coordinate(5, 1), start, end));
    assertTrue(PlanitJtsUtils.isCoordinateRightOf(new Coordinate(5, -1), start, end));
    assertFalse(PlanitJtsUtils.isCoordinateLeftOf(new Coordinate(5, 0), start, end));
    assertFalse(PlanitJtsUtils.isCoordinateRightOf(new Coordinate(5, 0), start, end));
  }

  /** The index of a coordinate equal in 2D, -1 when not present */
  @Test
  public void coordinateIndexOf() {
    var coordinates = new Coordinate[]{new Coordinate(0, 0), new Coordinate(1, 0), new Coordinate(2, 0)};
    assertEquals(1, PlanitJtsUtils.getCoordinateIndexOf(new Coordinate(1, 0, 7), coordinates));
    assertEquals(-1, PlanitJtsUtils.getCoordinateIndexOf(new Coordinate(9, 9), coordinates));
  }

  /** The smallest turn from one angle to the other, in either direction */
  @Test
  public void minDiffAngleInDegrees() {
    assertEquals(20, PlanitJtsUtils.minDiffAngleInDegrees(10, 350), 1e-9);
    assertEquals(180, PlanitJtsUtils.minDiffAngleInDegrees(0, 180), 1e-9);
    assertEquals(0, PlanitJtsUtils.minDiffAngleInDegrees(90, 90), 1e-9);
    assertEquals(90, PlanitJtsUtils.minDiffAngleInDegrees(-45, 45), 1e-9);
  }

  /** Coordinates as a flat array of ordinates, x then y, or y then x when reversed */
  @Test
  public void convertToOrdinates() {
    var coordinates = new Coordinate[]{new Coordinate(1, 2), new Coordinate(3, 4)};
    assertArrayEquals(new double[]{1, 2, 3, 4}, PlanitJtsUtils.convertToOrdinates(coordinates, false));
    assertArrayEquals(new double[]{2, 1, 4, 3}, PlanitJtsUtils.convertToOrdinates(coordinates, true));
  }

  /**
   * The centre of a geometry: a point is its own centre, an open line's is the point halfway along it, a closed line's
   * is the centroid of the area it encloses, and a polygon's is its centroid
   */
  @Test
  public void centreOfPointOpenLineClosedLineAndPolygon() {
    var point = PlanitJtsUtils.createPoint(3, 4);
    assertTrue(new Coordinate(3, 4).equals2D(PlanitJtsUtils.extractCentre(point).getCoordinate(), 1e-9));

    /* an L shaped line 20 long: halfway along it is its corner, while its centroid lies off the line */
    var line = PlanitJtsUtils.createLineString(new Coordinate(0, 0), new Coordinate(10, 0), new Coordinate(10, 10));
    assertTrue(new Coordinate(10, 0).equals2D(PlanitJtsUtils.extractCentre(line).getCoordinate(), 1e-9));

    /* a closed line around a 10 by 2 rectangle: the rectangle's centroid, not the point halfway along the outline */
    var rectangle = new Coordinate[]{
        new Coordinate(0, 0), new Coordinate(10, 0), new Coordinate(10, 2), new Coordinate(0, 2), new Coordinate(0, 0)};
    var ring = PlanitJtsUtils.createLineString(rectangle);
    assertTrue(new Coordinate(5, 1).equals2D(PlanitJtsUtils.extractCentre(ring).getCoordinate(), 1e-9));

    var polygon = PlanitJtsUtils.createPolygon(rectangle);
    assertTrue(new Coordinate(5, 1).equals2D(PlanitJtsUtils.extractCentre(polygon).getCoordinate(), 1e-9));
  }
}
