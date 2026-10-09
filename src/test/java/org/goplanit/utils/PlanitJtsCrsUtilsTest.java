package org.goplanit.utils;

import org.goplanit.utils.exceptions.PlanItRunTimeException;
import org.goplanit.utils.geo.PlanitJtsCrsUtils;
import org.goplanit.utils.geo.PlanitJtsUtils;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.Polygon;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests of the distance, closest location, bounding box, direction and proximity functionality of
 * {@link PlanitJtsCrsUtils}. Most use a projected crs in metres, where the expected values are plain plane geometry;
 * the geodetic path is checked separately on WGS84 against reference values of the WGS84 ellipsoid
 *
 * @author markr
 */
public class PlanitJtsCrsUtilsTest {

  /** tolerance for plane geometry results in metres */
  private static final double PLANE_TOLERANCE = 1e-9;

  /** tolerance for geodetic results in metres */
  private static final double GEODETIC_TOLERANCE = 0.01;

  /** projected crs in metres, so distances are those of the plane */
  private final PlanitJtsCrsUtils projected = new PlanitJtsCrsUtils(PlanitJtsCrsUtils.DEFAULT_PROJECTED_CRS_EPSG_3857);

  /** geographic crs, so distances are measured on the ellipsoid */
  private final PlanitJtsCrsUtils geographic = new PlanitJtsCrsUtils(PlanitJtsCrsUtils.DEFAULT_GEOGRAPHIC_CRS);

  private static Coordinate c(double x, double y) {
    return new Coordinate(x, y);
  }

  /** square of 10 by 10 with its lower left corner at the origin, its ring running (0,0), (0,10), (10,10), (10,0) and
   * back to (0,0) */
  private static Polygon square() {
    return PlanitJtsUtils.create2DPolygon(new Envelope(0, 10, 0, 10));
  }

  // ---------------------------------------------------------------------------------------------------------------
  // construction
  // ---------------------------------------------------------------------------------------------------------------

  /** The default instance works on WGS84, and an instance reports the crs it was created for */
  @Test
  public void instanceReportsItsCoordinateReferenceSystem() {
    assertSame(PlanitJtsCrsUtils.DEFAULT_GEOGRAPHIC_CRS, new PlanitJtsCrsUtils().getCoordinateReferenceSystem());
    assertSame(PlanitJtsCrsUtils.DEFAULT_PROJECTED_CRS_EPSG_3857, projected.getCoordinateReferenceSystem());
  }

  // ---------------------------------------------------------------------------------------------------------------
  // distance between two locations
  // ---------------------------------------------------------------------------------------------------------------

  /** In a projected crs in metres the distance between two locations is the straight line one */
  @Test
  public void distanceBetweenLocationsIsStraightLineInProjectedCrs() {
    assertEquals(5, projected.getDistanceInMetres(c(0, 0), c(3, 4)), PLANE_TOLERANCE);
    assertEquals(5, projected.getDistanceInMetres(
        PlanitJtsUtils.createPoint(0, 0), PlanitJtsUtils.createPoint(3, 4)), PLANE_TOLERANCE);
    assertEquals(0.005, projected.getDistanceInKilometres(
        PlanitJtsUtils.createPoint(0, 0), PlanitJtsUtils.createPoint(3, 4)), PLANE_TOLERANCE);
    assertEquals(0, projected.getDistanceInMetres(c(7, 7), c(7, 7)), PLANE_TOLERANCE);
  }

  /** In a geographic crs the distance follows the ellipsoid: one degree of latitude near Sydney */
  @Test
  public void distanceBetweenLocationsFollowsEllipsoidInGeographicCrs() {
    assertEquals(110_913.399, geographic.getDistanceInMetres(c(151, -33), c(151, -34)), GEODETIC_TOLERANCE);
  }

  /** A distance is within a maximum only when strictly smaller than it */
  @Test
  public void distanceWithinMetresIsStrict() {
    assertTrue(projected.isDistanceWithinMetres(c(0, 0), c(3, 4), 5.1));
    assertFalse(projected.isDistanceWithinMetres(c(0, 0), c(3, 4), 5));
    assertTrue(projected.isDistanceWithinMetres(PlanitJtsUtils.createPoint(0, 0), PlanitJtsUtils.createPoint(3, 4), 6));
  }

  /** A missing location cannot be measured from or to */
  @Test
  public void distanceToMissingLocationIsRejected() {
    assertThrows(PlanItRunTimeException.class, () -> projected.getDistanceInMetres(null, c(1, 1)));
    assertThrows(PlanItRunTimeException.class, () -> projected.getDistanceInMetres(c(1, 1), (Coordinate) null));
  }

  // ---------------------------------------------------------------------------------------------------------------
  // length along a line string
  // ---------------------------------------------------------------------------------------------------------------

  /** The length of a line string is the sum of its segments, and a part of it the sum of the segments within */
  @Test
  public void lineStringLengthAddsUpItsSegments() {
    LineString line = PlanitJtsUtils.createLineString(c(0, 0), c(3, 4), c(3, 10));

    assertEquals(0.011, projected.getDistanceInKilometres(line), PLANE_TOLERANCE);
    assertEquals(5, projected.getDistanceInMetres(line, 0, 1), PLANE_TOLERANCE);
    assertEquals(6, projected.getDistanceInMetres(line, 1, 2), PLANE_TOLERANCE);
    assertEquals(11, projected.getDistanceInMetres(line, 2, 0), PLANE_TOLERANCE);
    assertEquals(0, projected.getDistanceInMetres(line, 1, 1), PLANE_TOLERANCE);
  }

  /** The parts of a line string between its coordinates add up to its whole length, in either index order */
  @Test
  public void partsOfLineStringAddUpToItsLength() {
    var line = PlanitJtsUtils.createLineString(
        new Coordinate(151.2089, -33.87), new Coordinate(151.2095, -33.87), new Coordinate(151.2101, -33.8705));

    double whole = geographic.getDistanceInKilometres(line) * 1000;
    double first = geographic.getDistanceInMetres(line, 0, 1);
    double second = geographic.getDistanceInMetres(line, 1, 2);

    assertEquals(whole, first + second, 1e-6);
    assertEquals(whole, geographic.getDistanceInMetres(line, 2, 0), 1e-6);
    assertEquals(0, geographic.getDistanceInMetres(line, 1, 1), 1e-9);
  }

  // ---------------------------------------------------------------------------------------------------------------
  // closest location on a geometry
  // ---------------------------------------------------------------------------------------------------------------

  /** The closest existing coordinate of a geometry is one of its own coordinates, which need not be the closest
   * location on the geometry; projecting finds the latter */
  @Test
  public void closestExistingCoordinateDiffersFromClosestProjectedLocation() {
    LineString line = PlanitJtsUtils.createLineString(c(0, 0), c(10, 0));
    Coordinate reference = c(4, 3);

    assertEquals(c(0, 0), projected.getClosestExistingCoordinateToPoint(reference, line));
    assertEquals(5, projected.getClosestExistingCoordinateDistanceInMeters(reference, line), PLANE_TOLERANCE);

    assertEquals(c(4, 0), projected.getClosestProjectedCoordinateOnLineString(reference, line));
    assertEquals(c(4, 0), projected.getClosestProjectedCoordinateOnGeometry(reference, line));
    assertEquals(3, projected.getClosestProjectedDistanceInMetersToLineString(reference, line), PLANE_TOLERANCE);
  }

  /** Without a geometry there is no closest coordinate, at an infinite distance */
  @Test
  public void closestExistingCoordinateOfMissingGeometryIsAbsent() {
    assertEquals(null, projected.getClosestExistingCoordinateToPoint(c(1, 1), null));
    assertEquals(Double.POSITIVE_INFINITY, projected.getClosestExistingCoordinateDistanceInMeters(c(1, 1), null));
  }

  /** The closest location on a line string as a linear location names the segment and the fraction along it */
  @Test
  public void closestLinearLocationOnLineStringNamesSegmentAndFraction() {
    LineString line = PlanitJtsUtils.createLineString(c(0, 0), c(10, 0), c(10, 10));

    var location = projected.getClosestProjectedLinearLocationOnLineString(c(12, 5), line);
    assertEquals(1, location.getSegmentIndex());
    assertEquals(0.5, location.getSegmentFraction(), PLANE_TOLERANCE);
    assertEquals(c(10, 5), location.getCoordinate(line));

    /* linear locations have no equality of their own, compare them by position */
    assertEquals(0, location.compareTo(projected.getClosestProjectedLinearLocationOnGeometry(c(12, 5), line)));
  }

  /** A single point has no extent to hold a linear location */
  @Test
  public void closestLinearLocationOnPointIsRejected() {
    assertThrows(PlanItRunTimeException.class,
        () -> projected.getClosestProjectedLinearLocationOnGeometry(c(1, 1), PlanitJtsUtils.createPoint(0, 0)));
  }

  /** Of the coordinates of a reference geometry the one closest to the line string decides the location on it */
  @Test
  public void closestLinearLocationFromReferenceGeometryUsesItsClosestCoordinate() {
    LineString line = PlanitJtsUtils.createLineString(c(0, 0), c(10, 0));
    LineString reference = PlanitJtsUtils.createLineString(c(2, 5), c(4, 1));

    var location = projected.getClosestGeometryExistingCoordinateToProjectedLinearLocationOnLineString(reference, line);
    assertEquals(c(4, 0), location.getCoordinate(line));
  }

  /** Of the coordinates of a line string the one closest to a reference geometry is found, optionally within a range
   * of its coordinates */
  @Test
  public void closestLineStringCoordinateToGeometryRespectsIndexRange() {
    LineString line = PlanitJtsUtils.createLineString(c(0, 5), c(5, 1), c(10, 3));
    LineString reference = PlanitJtsUtils.createLineString(c(0, 0), c(10, 0));

    assertEquals(c(5, 1), projected.getClosestExistingLineStringCoordinateToGeometry(reference, line));
    assertEquals(c(10, 3), projected.getClosestExistingLineStringCoordinateToGeometry(reference, line, 2, 2));
    assertEquals(c(0, 5), projected.getClosestExistingLineStringCoordinateToGeometry(reference, line, 0, 0));
  }

  /** The closest distance to a polygon is to its exterior ring, from outside as well as inside */
  @Test
  public void closestDistanceToPolygonIsToItsRing() {
    Polygon square = square();

    assertEquals(2, projected.getClosestDistanceInMetersToPolygon(c(5, -2), square), PLANE_TOLERANCE);
    assertEquals(3, projected.getClosestDistanceInMetersToPolygon(c(13, 5), square), PLANE_TOLERANCE);
    /* inside, the nearest side is the bottom one */
    assertEquals(4, projected.getClosestDistanceInMetersToPolygon(c(5, 4), square), PLANE_TOLERANCE);
  }

  /** The closest location on a polygon is expressed on its exterior ring, naming the edge it lies on: below the square
   * that is the closing edge from (10,0) back to (0,0), the ring's last */
  @Test
  public void closestLocationOnPolygonNamesTheRingEdgeItLiesOn() {
    Polygon square = square();

    var location = projected.getClosestProjectedLinearLocationOnPolygon(c(5, -2), square);
    assertEquals(3, location.getSegmentIndex());
    assertEquals(0.5, location.getSegmentFraction(), PLANE_TOLERANCE);
    assertEquals(c(5, 0), projected.getClosestPojectedCoordinateOnPolygon(c(5, -2), square));
  }

  /** Beyond a corner the closest location is that corner, reached at the end of the edge leading to it */
  @Test
  public void closestLocationOnPolygonBeyondCornerIsThatCorner() {
    Polygon square = square();

    assertEquals(c(10, 10), projected.getClosestPojectedCoordinateOnPolygon(c(11, 12), square));
    assertEquals(c(0, 0), projected.getClosestPojectedCoordinateOnPolygon(c(-1, -2), square));
  }

  /** Every edge of the exterior ring is considered, including those not touching its first coordinate: right of the
   * square the closest location lies on the edge from (10,10) to (10,0), not on a line through the square */
  @Test
  public void closestLocationOnPolygonConsidersEveryRingEdge() {
    Polygon square = square();

    var location = projected.getClosestProjectedLinearLocationOnPolygon(c(13, 5), square);
    assertEquals(2, location.getSegmentIndex());
    assertEquals(0.5, location.getSegmentFraction(), PLANE_TOLERANCE);
    assertEquals(c(10, 5), projected.getClosestPojectedCoordinateOnPolygon(c(13, 5), square));
    assertEquals(c(10, 5), projected.getClosestProjectedCoordinateOnGeometry(c(13, 5), square));
  }

  /** The closest distance to a geometry projects onto it whatever its type */
  @Test
  public void closestDistanceSupportsPointsLinesMultiLinesAndPolygons() {
    MultiLineString lines = new GeometryFactory().createMultiLineString(new LineString[]{
        PlanitJtsUtils.createLineString(c(0, 0), c(10, 0)), PlanitJtsUtils.createLineString(c(0, 8), c(10, 8))});

    assertEquals(5, projected.getClosestDistanceInMeters(c(3, 4), PlanitJtsUtils.createPoint(0, 0)), PLANE_TOLERANCE);
    assertEquals(4, projected.getClosestDistanceInMeters(c(3, 4), lines.getGeometryN(0)), PLANE_TOLERANCE);
    assertEquals(3, projected.getClosestDistanceInMeters(c(3, 5), lines), PLANE_TOLERANCE);
    assertEquals(3, projected.getClosestDistanceInMetersMultiLineString(c(3, 5), lines), PLANE_TOLERANCE);
    assertEquals(2, projected.getClosestDistanceInMeters(c(5, -2), square()), PLANE_TOLERANCE);
  }

  // ---------------------------------------------------------------------------------------------------------------
  // bounding boxes
  // ---------------------------------------------------------------------------------------------------------------

  /** A bounding box around a centre extends the given length in metres to each side of it */
  @Test
  public void boundingBoxAroundCentreExtendsLengthToEachSide() {
    Envelope box = projected.createBoundingBox(10, 20, 5);
    assertEquals(new Envelope(5, 15, 15, 25), box);
  }

  /** A bounding box around an existing one buffers it by the given length on all sides, whichever way round its
   * extreme points are given */
  @Test
  public void boundingBoxAroundEnvelopeBuffersAllSides() {
    Envelope expected = new Envelope(-2, 12, -2, 12);
    assertEquals(expected, projected.createBoundingBox(new Envelope(0, 10, 0, 10), 2));
    assertEquals(expected, projected.createBoundingBox(0, 0, 10, 10, 2));
    assertEquals(expected, projected.createBoundingBox(10, 10, 0, 0, 2));
  }

  /** In a geographic crs the bounding box sides lie the given length from the centre, measured on the ellipsoid */
  @Test
  public void boundingBoxInGeographicCrsHasSidesAtLengthFromCentre() {
    Envelope box = geographic.createBoundingBox(151.2, -33.87, 1000);

    assertEquals(1000, geographic.getDistanceInMetres(c(151.2, -33.87), c(151.2, box.getMaxY())), GEODETIC_TOLERANCE);
    assertEquals(1000, geographic.getDistanceInMetres(c(151.2, -33.87), c(151.2, box.getMinY())), GEODETIC_TOLERANCE);
    assertEquals(1000, geographic.getDistanceInMetres(c(151.2, -33.87), c(box.getMaxX(), -33.87)), 1);
    assertEquals(1000, geographic.getDistanceInMetres(c(151.2, -33.87), c(box.getMinX(), -33.87)), 1);
  }

  // ---------------------------------------------------------------------------------------------------------------
  // extending line segments
  // ---------------------------------------------------------------------------------------------------------------

  /** A line segment extends in its own direction, at its start, its end or both */
  @Test
  public void lineSegmentExtendsInItsOwnDirection() {
    var segment = PlanitJtsUtils.createLineSegment(c(0, 0), c(0, 10));

    var atEnd = projected.createExtendedLineSegment(segment, 5, false, true);
    assertEquals(0, atEnd.p0.distance(c(0, 0)), PLANE_TOLERANCE);
    assertEquals(0, atEnd.p1.distance(c(0, 15)), PLANE_TOLERANCE);

    var atStart = projected.createExtendedLineSegment(segment, 5, true, false);
    assertEquals(0, atStart.p0.distance(c(0, -5)), PLANE_TOLERANCE);
    assertEquals(0, atStart.p1.distance(c(0, 10)), PLANE_TOLERANCE);

    var both = projected.createExtendedLineSegment(segment, 5, true, true);
    assertEquals(20, both.getLength(), PLANE_TOLERANCE);
  }

  /** In a geographic crs the extension is the given length measured on the ellipsoid */
  @Test
  public void lineSegmentExtensionInGeographicCrsHasGivenLength() {
    var segment = PlanitJtsUtils.createLineSegment(c(151.2, -33.87), c(151.2, -33.86));

    var extended = geographic.createExtendedLineSegment(segment, 100, false, true);
    assertEquals(100, geographic.getDistanceInMetres(segment.p1, extended.p1), GEODETIC_TOLERANCE);
    assertTrue(extended.p1.y > segment.p1.y);
  }

  // ---------------------------------------------------------------------------------------------------------------
  // direction
  // ---------------------------------------------------------------------------------------------------------------

  /** Azimuth is measured clockwise from north, from -180 to 180 or from 0 to 360 */
  @Test
  public void azimuthIsClockwiseFromNorth() {
    assertEquals(0, projected.getAzimuthInDegrees(c(0, 0), c(0, 10), false), PLANE_TOLERANCE);
    assertEquals(90, projected.getAzimuthInDegrees(c(0, 0), c(10, 0), false), PLANE_TOLERANCE);
    assertEquals(180, projected.getAzimuthInDegrees(c(0, 0), c(0, -10), false), PLANE_TOLERANCE);
    assertEquals(-90, projected.getAzimuthInDegrees(c(0, 0), c(-10, 0), false), PLANE_TOLERANCE);
    assertEquals(270, projected.getAzimuthInDegrees(c(0, 0), c(-10, 0), true), PLANE_TOLERANCE);
    assertEquals(45, projected.getAzimuthInDegrees(0, 0, 10, 10, true), PLANE_TOLERANCE);
    assertEquals(90, projected.getAzimuthInDegrees(
        projected.toDirectPosition(c(0, 0)), projected.toDirectPosition(PlanitJtsUtils.createPoint(10, 0)), false),
        PLANE_TOLERANCE);
  }

  /** In a geographic crs azimuth follows the ellipsoid: due north and, on the equator, due east */
  @Test
  public void azimuthInGeographicCrsFollowsEllipsoid() {
    assertEquals(0, geographic.getAzimuthInDegrees(c(151, -33.87), c(151, -33.86), false), 1e-6);
    assertEquals(90, geographic.getAzimuthInDegrees(c(151, 0), c(151.01, 0), false), 1e-6);
  }

  /** A direct position carries the coordinate and the crs of the instance */
  @Test
  public void directPositionCarriesCoordinateAndCrs() {
    var position = projected.toDirectPosition(c(3, 4));
    assertEquals(3, position.getCoordinate()[0], PLANE_TOLERANCE);
    assertEquals(4, position.getCoordinate()[1], PLANE_TOLERANCE);
    assertSame(projected.getCoordinateReferenceSystem(), position.getCoordinateReferenceSystem());
  }

  // ---------------------------------------------------------------------------------------------------------------
  // side and proximity
  // ---------------------------------------------------------------------------------------------------------------

  /** A geometry is left of a line from A to B when, looking from A to B, it lies to the left; a non point geometry
   * is judged by its location closest to B */
  @Test
  public void geometryLeftOfLineLooksFromAToB() {
    Coordinate a = c(0, 0);
    Coordinate b = c(10, 0);

    assertTrue(projected.isGeometryLeftOf(PlanitJtsUtils.createPoint(5, 1), a, b));
    assertFalse(projected.isGeometryLeftOf(PlanitJtsUtils.createPoint(5, -1), a, b));
    assertTrue(projected.isGeometryLeftOf(PlanitJtsUtils.createLineString(c(8, 2), c(12, 3)), a, b));
    assertThrows(PlanItRunTimeException.class, () -> projected.isGeometryLeftOf(null, a, b));
  }

  /** A geometry outside an envelope is near it when within the given distance of its boundary, inclusive */
  @Test
  public void geometryNearEnvelopeIsWithinDistanceOfItsBoundary() {
    Envelope envelope = new Envelope(0, 10, 0, 10);

    assertTrue(projected.isGeometryNearEnvelope(PlanitJtsUtils.createPoint(12, 5), envelope, 2));
    assertFalse(projected.isGeometryNearEnvelope(PlanitJtsUtils.createPoint(12, 5), envelope, 1.5));
    assertTrue(projected.isGeometryNearEnvelope(
        PlanitJtsUtils.createLineString(c(13, 5), c(11, 12)), envelope, 2.5));
    assertFalse(projected.isGeometryNearEnvelope(
        PlanitJtsUtils.createLineString(c(13, 5), c(14, 12)), envelope, 2.5));
  }

  /** A geometry is near another when any of its own coordinates is within the given distance of it, inclusive */
  @Test
  public void geometryNearGeometryNeedsOneCoordinateWithinDistance() {
    LineString line = PlanitJtsUtils.createLineString(c(0, 0), c(10, 0));

    assertTrue(projected.isGeometryNearGeometry(PlanitJtsUtils.createLineString(c(5, 3), c(5, 30)), line, 3));
    assertFalse(projected.isGeometryNearGeometry(PlanitJtsUtils.createLineString(c(5, 4), c(5, 30)), line, 3));
  }
}
