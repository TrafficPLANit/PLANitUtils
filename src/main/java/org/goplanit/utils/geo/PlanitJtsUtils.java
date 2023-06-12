package org.goplanit.utils.geo;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.referencing.CRS;
import org.goplanit.utils.exceptions.PlanItException;
import org.goplanit.utils.exceptions.PlanItRunTimeException;
import org.goplanit.utils.math.Precision;
import org.goplanit.utils.misc.Pair;
import org.locationtech.jts.algorithm.Angle;
import org.locationtech.jts.algorithm.RobustDeterminant;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.linearref.LinearLocation;
import org.locationtech.jts.linearref.LocationIndexedLine;
import org.locationtech.jts.operation.linemerge.LineMerger;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.coordinate.PointArray;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

/**
 * General geographic related utils utilising the JTS API. 
 * 
 * @author markr
 *
 */
public class PlanitJtsUtils {

  /** the logger */
  private static final Logger LOGGER = Logger.getLogger(PlanitJtsUtils.class.getCanonicalName());
  

  /** jts geometry factory, jts geometry differs from opengis implementation by not carrying the crs and being more lightweight */
  protected static final GeometryFactory jtsGeometryFactory = JTSFactoryFinder.getGeometryFactory();
   
  /**
   * Convenience method that wraps the CRS.findMathTransform by catching exceptions and producing a planit excepion only as well as allowing for lenient transformer
   * 
   * @param sourceCRS      the source
   * @param destinationCRS the destination
   * @return transformer
   */
  public static MathTransform findMathTransform(CoordinateReferenceSystem sourceCRS, CoordinateReferenceSystem destinationCRS){
    PlanItRunTimeException.throwIfNull(sourceCRS, "source coordinate reference system null when creating math transform");
    PlanItRunTimeException.throwIfNull(destinationCRS, "destination coordinate reference system null when creating math transform");
    PlanitCrsUtils.silenceHsqlLogging();

    try {
      /* allows for some lenience in transformation due to different datums */
      boolean lenient = true;
      return CRS.findMathTransform(sourceCRS, destinationCRS, lenient);
    } catch (Exception e) {
      throw new PlanItRunTimeException(String.format("error during creation of transformer from CRS %s to CRS %s", sourceCRS.toString(), destinationCRS.toString()), e);
    }

  }

  /**
   * Transform given geometry based on provided transformer, checkedd exceptions are converted to PLANitRunTimeException instead
   *
   * @param geometry to transform
   * @param transformer to apply transformation
   * @return transformed geometry
   */
  public static Geometry transformGeometry(Geometry geometry, MathTransform transformer){
    try {
      return JTS.transform(geometry, transformer);
    }catch(Exception e){
      throw new PlanItRunTimeException("Unable to transform geometry %s",geometry, e);
    }
  }

  /**
   * Transform the spanning coordinates of envelope to difference CRS
   * @param envelope to transform
   * @param crsTransform to apply
   * @return transformed envelope
   */
  public static Envelope transformEnvelope(Envelope envelope, MathTransform crsTransform){
    var minPoint = createPoint(new Coordinate(envelope.getMinX(),envelope.getMinY()));
    var maxPoint = createPoint(new Coordinate(envelope.getMaxX(),envelope.getMaxY()));
    var transformedMinPoint = transformGeometry(minPoint,crsTransform);
    var transformedMaxPoint = transformGeometry(maxPoint,crsTransform);
    return new Envelope(transformedMinPoint.getCoordinate(),transformedMaxPoint.getCoordinate());
  }

  /**
   * create a coordinate by mapping ordinate 0 to x and ordinate 1 to y on the open gis DirecPosition
   * 
   * @param position in opengis format
   * @return JTS coordinate created
   */
  public static Coordinate createCoordinate(DirectPosition position) {
    return new Coordinate(position.getOrdinate(0), position.getOrdinate(1));
  }
  
  /**
   * Create JTS point object from coordinate
   * 
   * @param coordinate to use
   * @return point object representing the location
   */
  public static Point createPoint(Coordinate coordinate) {
    Point newPoint = jtsGeometryFactory.createPoint(coordinate);
    return newPoint;
  }  

  /**
   * Create JTS point object from X- and Y-coordinates
   * 
   * @param xCoordinate X-coordinate (longitude assumed)
   * @param yCoordinate Y-coordinate (latitude assumed)
   * @return point object representing the location
   */
  public static Point createPoint(Number xCoordinate, Number yCoordinate) {
    Coordinate coordinate = new Coordinate(xCoordinate.doubleValue(), yCoordinate.doubleValue());
    return createPoint(coordinate);
  }
  
  /** create a line segment
   * 
   * @param coordinate1 first coordinate
   * @param coordinate2 second coordinate
   * @return created line segment
   */
  public static LineSegment createLineSegment(Coordinate coordinate1, Coordinate coordinate2) {
    return new LineSegment(coordinate1, coordinate2);
  }  

  /**
   * Create a JTS line string from the doubles passed in (list of doubles containing x1,y1,x2,y2,etc. coordinates
   * 
   * @param coordinateList source
   * @return created line string
   */
  public static LineString createLineString(List<Double> coordinateList){
    PlanItRunTimeException.throwIf(coordinateList.size() % 2 != 0, "coordinate list must contain an even number of entries to correctly identify (x,y) pairs");
    Iterator<Double> iter = coordinateList.iterator();
    Coordinate[] coordinateArray = new Coordinate[coordinateList.size() / 2];
    int index = 0;
    while (iter.hasNext()) {
      coordinateArray[index++] = new Coordinate(iter.next(), iter.next());
    }
    return createLineString(coordinateArray);
  }

  /**
   * Based on the csv string construct a JTS line string
   * 
   * @param value the values containing the x,y coordinates in the crs of this instance
   * @param ts    tuple separating character
   * @param cs    comma separating character
   * @return the LineString created from the String
   */
  public static LineString createLineString(String value, char ts, char cs){
    List<Double> coordinateDoubleList = new ArrayList<>();
    String[] tupleString = value.split("[" + ts + "]");
    for (int index = 0; index < tupleString.length; ++index) {
      String xyCoordinateString = tupleString[index];
      String[] coordinateString = xyCoordinateString.split("[" + cs + "]");
      if (coordinateString.length != 2) {
        throw new PlanItRunTimeException(String.format("invalid coordinate encountered, expected two coordinates in tuple, but found %d", coordinateString.length));
      }
      coordinateDoubleList.add(Double.parseDouble(coordinateString[0]));
      coordinateDoubleList.add(Double.parseDouble(coordinateString[1]));
    }
    return createLineString(coordinateDoubleList);
  }

  /**
   * Create a line string from the passed in coordinates
   * 
   * @param coordinates source
   * @return created line string
   */
  public static LineString createLineString(Coordinate... coordinates){
    return jtsGeometryFactory.createLineString(coordinates);
  }

  /**
   * Based on the csv string construct a line string
   * 
   * @param value the values containing the x,y coordinates in the crs of this instance
   * @param ts    tuple separating string (which must be a a character)
   * @param cs    comma separating string (which must be a a character)
   * @return the LineString created from the String
   */
  public static LineString createLineStringFromCsvString(String value, String ts, String cs){
    if (ts.length() > 1 || cs.length() > 1) {
      PlanItRunTimeException.throwIf(ts.length() > 1, String.format("tuple separating string to create LineString is not a single character but %s", ts));
      PlanItRunTimeException.throwIf(cs.length() > 1, String.format("comma separating string to create LineString is not a single character but %s", cs));
    }
    return createLineString(value, ts.charAt(0), cs.charAt(0));
  }

  /**
   * Based on the line string construct a csv string
   * 
   * @param coordinates the values containing the x,y coordinates in the crs of this instance
   * @param ts       tuple separating string to use
   * @param cs       comma separating string to use
   * @param df       decinal formatter to format the decimals of the coordinates
   * @return the LineString created from the String
   */
  public static String createCsvStringFromCoordinates(Coordinate[] coordinates, Character ts, Character cs, DecimalFormat df) {
    StringBuilder csvStringBuilder = new StringBuilder();
    for (int index = 0, lastIndex = coordinates.length - 1; index < coordinates.length; ++index) {
      Coordinate coordinate = coordinates[index];
      csvStringBuilder.append(df.format(coordinate.x)).append(cs).append(df.format(coordinate.y));
      if (index == lastIndex) {
        break;
      }
      csvStringBuilder.append(ts);
    }
    return csvStringBuilder.toString();
  }

  /**
   * Create a multi line string from the passed in line strings
   *
   * @param lineStrings source
   * @return created multi line string
   */
  public static MultiLineString createMultiLineString(LineString... lineStrings){
    return jtsGeometryFactory.createMultiLineString(lineStrings);
  }

  /**
   * Create an empty polygon geometry
   * 
   * @return polygon
   */
  public static Polygon createPolygon() {
    Polygon polygon = jtsGeometryFactory.createPolygon();
    return polygon;
  }

  /**
   * create a polygon based on the passed in 2d list of doubles
   * 
   * @param coordinateList2D to use
   * @return created polygon
   */
  public static Polygon create2DPolygon(List<Double> coordinateList2D) {
    return createPolygon(listTo2DCoordinates(coordinateList2D));
  }

  /**
   * create a polygon based on the passed coordinate array
   * 
   * @param coords to use
   * @return created polygon
   */
  public static Polygon createPolygon(Coordinate[] coords) {
    return jtsGeometryFactory.createPolygon(jtsGeometryFactory.createLinearRing(coords));
  }
  
  /**
   * create a polygon based on the bounding box
   * 
   * @param envelope to use
   * @return polygon geometry created
   */
  public static Polygon create2DPolygon(Envelope envelope) {
   Coordinate[] coordinates = 
       new Coordinate[] { 
           new Coordinate(envelope.getMinX(),envelope.getMinY()),
           new Coordinate(envelope.getMinX(),envelope.getMaxY()),
           new Coordinate(envelope.getMaxX(),envelope.getMaxY()),
           new Coordinate(envelope.getMaxX(),envelope.getMinY()),
           new Coordinate(envelope.getMinX(),envelope.getMinY()) /* repeat initial coordinate to close polygon*/
      };
   return createPolygon(coordinates);
  }     

  /**
   * Convert OpenGIS directPosition to JTS coordinates
   * 
   * @param positions List of GeoTools Position objects
   * @return coordinates array of JTS Coordinate objects
   */
  public static Coordinate[] directPositionsToCoordinates(List<DirectPosition> positions) {
    Coordinate[] coordinates = new Coordinate[positions.size()];
    for (int index = 0; index < coordinates.length; ++index) {
      coordinates[index] = createCoordinate(positions.get(index));
    }
    return coordinates;
  }

  /**
   * Create an array of coordinates based on a list of some type that can be interpreted as strings and converted to doubles.
   * 
   * @param posList to extract coordinates from
   * @return coordinates array
   */
  public static Coordinate[] listTo2DCoordinates(List<?> posList) {
    int dimensions = 2;
    /* we parse as is, so we do not check for correct axis order (long/lat or lat/long for example) */
    Coordinate[] coordinates = new Coordinate[posList.size() / dimensions];

    int index = 0;
    while (index + dimensions - 1 < posList.size()) {
      coordinates[index / dimensions] = new Coordinate(Double.parseDouble(posList.get(index).toString()), Double.parseDouble(posList.get(index + 1).toString()));
      index += dimensions;
    }
    return coordinates;
  }

  /** copy the array and remove any null entries
   * 
   * @param coordArray to trim
   * @return copy without null entries
   */
  public static Coordinate[] copyWithoutNullEntries(final Coordinate[] coordArray) {
    Coordinate[] copy = new Coordinate[coordArray.length];
    int copyIndex=0;
    for(int index=0;index<copy.length;++index) {
      Coordinate currCoord = coordArray[index];
      if(currCoord!=null) {
        copy[copyIndex] = currCoord;
        ++copyIndex;        
      }
    }
    if(copyIndex > 0) {
      return Arrays.copyOf(copy, copyIndex);
    }
    return null;
  }  
  
  /** check if coord array is closed, i.e., first coordinate is the same as the last
   *  in 2D
   *  
   * @param coordArray to check
   * @return true when closed, false otherwise
   */
  public static boolean isClosed2D(Coordinate[] coordArray) {
    if(coordArray != null && coordArray.length>2) {
      return coordArray[0].equals2D(coordArray[coordArray.length-1]);
    }
    return false;
  }
  
  /** create a copy of the passed in coord array and close it by adding a new coordinate at the end that matches the first.
   * If the array is already closed, it is returned as is. If not eligible for closing, exception is thrown.
   * 
   * @param coordArray to make closed if possible
   * @return closed array
   * @throws PlanItException thrown if error
   */
  public static Coordinate[] makeClosed2D(final Coordinate[] coordArray) throws PlanItException {
    if(coordArray!= null && coordArray.length >=2) {
      if(!isClosed2D(coordArray)) {
        Coordinate[] closedCoordArray = Arrays.copyOf(coordArray, coordArray.length+1);
        closedCoordArray[coordArray.length] = coordArray[0];
        return closedCoordArray;
      }
      return coordArray;
    }else {
      throw new PlanItException("Cannot make passed in coordinates closed 2D");
    }
  }  

  /**
   * Remove all coordinates in the line string up to but not including the first occurrence of the passed in position. In case the position cannot be found, an exception will be
   * thrown
   * 
   * @param position to use
   * @param geometry linestring
   * @return the line string created
   */
  public static LineString createCopyWithoutCoordinatesBefore(Point position, LineString geometry){
    Optional<Integer> offset = findFirstCoordinatePosition(position.getCoordinate(), geometry, Precision.EPSILON_0);

    if (!offset.isPresent()) {
      throw new PlanItRunTimeException(String.format("Point (%s) does not exist on line string (%s), unable to create copy from this location", position.toString(), geometry.toString()));
    }

    Coordinate[] coordinates = copyCoordinatesFrom(offset.get(), geometry);
    if(coordinates.length == 1) {
      throw new PlanItRunTimeException(String.format("Linestring (%s) without coordinates before %s results in single coordinate, unable to create linestring", geometry.toString(), position.toString()));
    }

    return createLineString(coordinates);
  }

  /**
   * Remove all coordinates in the line string up to but not including the passed in index.
   * 
   * @param startIndex start index
   * @param geometry   to apply to
   * @return the line string created
   * @throws PlanItException thrown if error
   */
  public static LineString createCopyWithoutCoordinatesBefore(int startIndex, LineString geometry) throws PlanItException {
    if (startIndex >= geometry.getNumPoints() || startIndex < 0) {
      throw new PlanItException("invalid start index for extracting coordinates from line string geometry");
    }
    return createLineString(copyCoordinatesFrom(startIndex, geometry));
  }

  /**
   * Remove all coordinates in the line string after but not including the passed in position. In case the position cannot be found, an exception will be thrown
   * 
   * @param position first location of this position in geometry is the last entry in the copied geometry
   * @param geometry line string
   * @return copy of the line string without indicated coordinates
   */
  public static LineString createCopyWithoutCoordinatesAfter(Point position, LineString geometry){
    Optional<Integer> offset = findFirstCoordinatePosition(position.getCoordinate(), geometry, Precision.EPSILON_0);

    if (!offset.isPresent()) {
      throw new PlanItRunTimeException(String.format("Point (%s) does not exist on line string %s, unable to create copy from this location", position.toString(), geometry.toString()));
    }

    Coordinate[] coordinates = copyCoordinatesUpToNotIncluding(offset.get() + 1, geometry);
    return createLineString(coordinates);
  }

  /**
   * Remove all coordinates in the line string after but not including the passed in index.
   * 
   * @param endIndex last index to keep, after is removed
   * @param geometry to apply to
   * @return the line string created
   * @throws PlanItException thrown if error
   */
  public static LineString createCopyWithoutCoordinatesAfter(int endIndex, LineString geometry) throws PlanItException {
    if (geometry == null) {
      return null;
    }

    if (endIndex >= geometry.getNumPoints() || endIndex < 0) {
      throw new PlanItException("invalid end index for extracting coordinates from line string geometry");
    }
    return createLineString(copyCoordinatesUpToNotIncluding(endIndex + 1, geometry));
  }

  /**
   * create an identical copy, except that any adjacent duplicate coordinates in the line string are removed
   * 
   * @param geometry to remove duplicate coordinates from
   * @return geometry copy without duplicates
   */
  public static LineString createCopyWithoutAdjacentDuplicateCoordinates(LineString geometry) {
    if (geometry == null) {
      return null;
    }

    ArrayList<Coordinate> coordinateList = new ArrayList<>(geometry.getNumPoints());
    int numCoordinates = geometry.getNumPoints();
    int index = 0;
    int nextIndex = index + 1;
    for (; index < numCoordinates; ++index, ++nextIndex) {
      Coordinate coordinate = geometry.getCoordinateN(index);
      boolean isAdjacentDuplicate = false;
      if (nextIndex < numCoordinates) {
        Coordinate adjacentCoordinate = geometry.getCoordinateN(index + 1);
        isAdjacentDuplicate = coordinate.equals2D(adjacentCoordinate);
      }
      if (!isAdjacentDuplicate) {
        coordinateList.add(coordinate);
      }
    }
    return jtsGeometryFactory.createLineString(coordinateList.stream().toArray(Coordinate[]::new));
  }
  
  /**
   * find first position where the coordinate resides on the geometry.
   * 
   * @param coordinateToLocate the one to locate
   * @param offset             start searching from offset position
   * @param geometry           to locate from
   * @param tolerance the tolerance allowed
   * @return the position if present
   */
  public static Optional<Integer> findFirstCoordinatePosition(Coordinate coordinateToLocate, int offset, LineString geometry, double tolerance) {
    if (geometry == null || coordinateToLocate == null) {
      return Optional.empty();
    }

    int numCoordinates = geometry.getNumPoints();
    for (int index = offset; index < numCoordinates; ++index) {
      Coordinate coordinate = geometry.getCoordinateN(index);
      if (coordinate.equals2D(coordinateToLocate, tolerance)) {
        return Optional.of(index);
      }
    }
    return Optional.empty();
  }

  /**
   * find first position where the coordinate resides on the geometry.
   * 
   * @param coordinateToLocate the one to locate
   * @param geometry           to locate from
   * @param tolerance the tolerance allowed
   * @return the position if present
   */
  public static Optional<Integer> findFirstCoordinatePosition(Coordinate coordinateToLocate, LineString geometry, double tolerance) {
    final int offset = 0;
    return findFirstCoordinatePosition(coordinateToLocate, offset, geometry, tolerance);
  }

  /**
   * copy the coordinates in the line string starting at the given offset (included)
   * 
   * @param offset   to start at
   * @param geometry to copy from
   * @return coordinate array, when offset is out of bounds null is returned
   */
  public static Coordinate[] copyCoordinatesFrom(int offset, LineString geometry){
    return copyCoordinatesFromUpToNotIncluding(offset, geometry.getNumPoints(), geometry);
  }

  /**
   * copy the coordinates in the line string until the given location, the location is included
   * 
   * @param untilPoint to stop (not included)
   * @param geometry   to copy from
   * @return coordinate array, when offset is out of bounds null is returned
   */
  public static Coordinate[] copyCoordinatesUpToNotIncluding(int untilPoint, LineString geometry){
    return copyCoordinatesFromUpToNotIncluding(0, untilPoint, geometry);
  }

  /**
   * copy the coordinates in the line string from-to the given locations, the locations are included
   * 
   * @param offset     to start at (included)
   * @param untilPoint to end with (not included)
   * @param geometry   to copy from
   * @return coordinate array, when offset is out of bounds empty coordinate array is returned
   */
  public static Coordinate[] copyCoordinatesFromUpToNotIncluding(int offset, int untilPoint, LineString geometry){
    PlanItRunTimeException.throwIfNull(geometry, "Provided geometry to copy coordinates from is null");

    int numCoordinates = geometry.getNumPoints();
    if (offset > untilPoint || untilPoint > numCoordinates) {
      LOGGER.severe("unable to extract coordinates from line string, offset is larger than final point, and/or final point exceeds number of coordinates in geometry");
    }

    Coordinate[] coordinates = new Coordinate[untilPoint - offset];
    for (int index = offset; index < untilPoint; ++index) {
      Coordinate coordinate = geometry.getCoordinateN(index);
      coordinates[index - offset] = coordinate;
    }

    return coordinates;
  }

  /**
   * concatenate the passed in gemoetries (lines strings) by simply copying all the coorcinates in order and create a new line string from these points
   * 
   * @param geometries to concatenate
   * @return created concatenated linesString
   */
  public static LineString concatenate(LineString... geometries) {
    MultiLineString theMultiLineString = jtsGeometryFactory.createMultiLineString(geometries);
    return jtsGeometryFactory.createLineString(theMultiLineString.getCoordinates());
  }

  /**
   * Convert an open gis line string object to a JTS Gis LineString instance by copying the internal coordinates
   * 
   * @param openGisLineString to convert
   * @return jtsLineString created
   * @throws PlanItException thrown if there is an error
   */
  public static LineString convertToJtsLineString(org.opengis.geometry.coordinate.LineString openGisLineString) throws PlanItException {
    PointArray samplePoints = openGisLineString.getSamplePoints();
    List<Coordinate> coordinates = samplePoints.stream().map(point -> createCoordinate(point.getDirectPosition())).collect(Collectors.toList());
    return jtsGeometryFactory.createLineString((Coordinate[]) coordinates.toArray());
  }

  /**
   * Cast a JTS MultiLineString with a single entry into a JTS LineString instance if valid
   * 
   * @param jtsMultiLineString JTS MultiLineString input object
   * @return jts LineString output object
   * @throws PlanItException thrown if there is an error in casting
   */
  public static LineString convertToLineString(MultiLineString jtsMultiLineString) throws PlanItException {
    PlanItException.throwIf(((MultiLineString) jtsMultiLineString).getNumGeometries() > 1, "MultiLineString contains multiple LineStrings");
    return (LineString) jtsMultiLineString.getGeometryN(0);
  }

  /** split a line string into two line strings at a given location along the original geometry. the resulting linestrings have the split location in common
   * 
   * @param geometry to split
   * @param splitLocation where to split
   * @return line string pair, first from start to split location, second from split location to end
   */
  public static Pair<LineString, LineString> splitLineString(LineString geometry, LinearLocation splitLocation) {
    LocationIndexedLine locIndexedLine = new LocationIndexedLine(geometry);
    LineString geometryStartToLinearLocation= (LineString)locIndexedLine.extractLine(locIndexedLine.getStartIndex(), splitLocation);
    LineString geometryLinearLocationToEnd = null;
    if(!splitLocation.isEndpoint(geometry)) {
      geometryLinearLocationToEnd = (LineString) locIndexedLine.extractLine(splitLocation, locIndexedLine.getEndIndex());
    }
    return Pair.of(geometryStartToLinearLocation, geometryLinearLocationToEnd);
  }

  /**
   * Merge two line strings that are expected to have at least one point in common, in case any input is null or there is no overlap 
   * between the two null is returned 
   * 
   * 
   * @param first line string
   * @param second line string
   * @return merged line string, or null if failed or any input is null
   */
  public static LineString mergeLineStrings(LineString first, LineString second) {
    if(first == null && second!=null) {
      return (LineString) second.copy();
    }else if(first!=null && second==null){
      return (LineString) first.copy();
    }
  
    /* register */
    LineMerger lineMerger = new LineMerger();
    lineMerger.add(first);
    lineMerger.add(second);
    /* merge and collect first result */
    if(lineMerger.getMergedLineStrings()==null || lineMerger.getMergedLineStrings().isEmpty()) {
      return null;
    }
    return (LineString) lineMerger.getMergedLineStrings().iterator().next();
  }

  /** Using the normalised sign of the determinant of line AB and AM we determine if coordM resides left of the line segment AB
   * 
   * @param coordM to check if left of AB
   * @param coordA A coord of AB
   * @param coordB B coord of AB
   * @return true when M is left of AB 
   */
  public static boolean isCoordinateLeftOf(Coordinate coordM, Coordinate coordA, Coordinate coordB) {
    /* use the RobustDeterminant feature of JTS */
    return RobustDeterminant.orientationIndex( coordA, coordB, coordM) == 1;
  }
  
  /** Using the normalised sign of the determinant of line AB and AM we determine if coordM resides right of the line segment AB
   * 
   * @param coordM to check if right of AB
   * @param coordA A coord of AB
   * @param coordB B coord of AB
   * @return true when M is right of AB
   */
  public static boolean isCoordinateRightOf(Coordinate coordM, Coordinate coordA, Coordinate coordB) {
    /* use the RobustDeterminant feature of JTS */
    return RobustDeterminant.orientationIndex( coordA, coordB, coordM) == -1;
  }

  /** collect the index of the given coordinate from the array
   * @param coordinate to collect index for
   * @param coordinates to collect from
   * @return index, -1 if nto found
   */
  public static int getCoordinateIndexOf(Coordinate coordinate, Coordinate[] coordinates) {
    for(int coordinateIndex = 0; coordinateIndex <= coordinates.length-1; ++coordinateIndex) {
      Point internalPoint = createPoint(coordinates[coordinateIndex]);
      if(internalPoint.getCoordinate().equals2D(coordinate)) {
        return coordinateIndex;
      }
    }
    return -1;
  }


  /**
   * Compute the minimum difference t get to one angle to another in either direction between the given two angles (0-360)
   *
   * @param angleDegrees1 first angle in degrees
   * @param angleDegrees2 second angle in degrees
   * @return the found angle
   */
  public static double minDiffAngleInDegrees(double angleDegrees1, double angleDegrees2) {
    return Angle.toDegrees(Angle.diff(Angle.normalize(Angle.toRadians(angleDegrees1)),Angle.normalize(Angle.toRadians(angleDegrees2))));
  }
}
