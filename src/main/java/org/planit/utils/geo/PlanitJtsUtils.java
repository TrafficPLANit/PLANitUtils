package org.planit.utils.geo;

import java.awt.geom.Point2D;
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
import org.geotools.referencing.GeodeticCalculator;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.referencing.factory.epsg.CartesianAuthorityFactory;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineSegment;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.linearref.LinearLocation;
import org.locationtech.jts.linearref.LocationIndexedLine;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.coordinate.PointArray;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.planit.utils.exceptions.PlanItException;
import org.planit.utils.graph.Vertex;

/**
 * General geotools related utils. Uses geodetic distance when possible. In case the CRS is not based on an ellipsoid (2d plane) it will simply compute the distance between
 * coordinates using Pythagoras with the unit distance in meters, consistent with the {@code CartesianAuthorityFactory.GENERIC_2D}
 * 
 * It is assumed that x coordinate refers to latitude and y coordinate refers to longitude
 * 
 * @author markr
 *
 */
public class PlanitJtsUtils {

  /** the logger */
  private static final Logger LOGGER = Logger.getLogger(PlanitJtsUtils.class.getCanonicalName());
  
  /**
   * Default Coordinate Reference System: WGS84
   */
  public static final DefaultGeographicCRS DEFAULT_GEOGRAPHIC_CRS = DefaultGeographicCRS.WGS84;

  /**
   * In absence of a geographic crs we can also use cartesian: GENERIC_2D
   */
  public static final CoordinateReferenceSystem CARTESIANCRS = CartesianAuthorityFactory.GENERIC_2D;  

  /** the crs to use */
  private final CoordinateReferenceSystem crs;
  
  /** geodetic calculator to use in tandem with the used CRS */
  private final GeodeticCalculator geoCalculator;  

  /** jts geometry factory, jts geometry differs from opengis implementation by not carrying the crs and being more lightweight */
  private static final GeometryFactory jtsGeometryFactory = JTSFactoryFinder.getGeometryFactory();
  
  /**
   * Constructor
   * 
   * Uses default coordinate reference system
   */
  public PlanitJtsUtils() {
    this(DEFAULT_GEOGRAPHIC_CRS);
  }

  /**
   * Constructor
   * 
   * @param coordinateReferenceSystem OpenGIS CoordinateReferenceSystem object containing geometry
   */
  public PlanitJtsUtils(CoordinateReferenceSystem coordinateReferenceSystem) {
    this.crs = coordinateReferenceSystem;
    /* viable only if non-cartesian based */
    geoCalculator = (!(coordinateReferenceSystem.equals(CARTESIANCRS))) ? new GeodeticCalculator(getCoordinateReferenceSystem()) : null;
  }
  
  /** find the distance between the closest coordinate on the geometry's coordinates. Note that this is likely NOT
   * the closest point to the geometry as this likely lies on the line connecting the two closest points.
   * 
   * @param point reference
   * @param geometry to check against, explore all its coordinates
   * @return closest coordinate distance
   * @throws PlanItException thrown if error
   */
  public double getClosestExistingCoordinateDistanceInMeters(Point point, Geometry geometry) throws PlanItException {
    double minDistanceMetersToCoordinate = Double.POSITIVE_INFINITY;
    if(geometry !=null ){      
      Coordinate referenceCoordinate = point.getCoordinate();
      Coordinate[] coordinates = geometry.getCoordinates();
      for(int index = 0 ; index < coordinates.length; ++index) {
        Coordinate coordinate = coordinates[index];
        double distanceMeters = getDistanceInMetres(referenceCoordinate, coordinate);
        if(minDistanceMetersToCoordinate > distanceMeters) {
          minDistanceMetersToCoordinate = distanceMeters;
        }
      }
    }
    return minDistanceMetersToCoordinate;
  }
  
  /** find the closest projected coordinate from the reference point to the geometry. Here we project onto the geometry, so we find the location with the actual closest distance 
   * and create a coordinate at this location regardless if this coordinate is part of the geometry as a predefined coordinate at an extreme point. It is therefore more accurate than
   * {@link getClosestExistingCoordinateDistanceInMeters}
   * 
   * @param referencePoint the reference point
   * @param geometry to find closest distance to point to
   * @return distance found in meters
   * @throws PlanItException thrown if error
   */  
  public Coordinate getClosestProjectedCoordinateTo(Point referencePoint, LineString geometry) {
    Coordinate referenceCoordinate = referencePoint.getCoordinate();
    LocationIndexedLine locIndexedLine = new LocationIndexedLine(geometry);
    LinearLocation projectedLocation = locIndexedLine.project(referenceCoordinate);
    return projectedLocation.getCoordinate(geometry); 
  }   
    
  /** find the closest distance in meters from the point to the geometry.Here we project onto the geometry, so we find the actual closest distance instead of merely finding the closest
   * modelled coordinated within the geometry.
   * 
   * @param referencePoint the reference point
   * @param geometry to find closest distance to point to
   * @return distance found in meters
   * @throws PlanItException thrown if error
   */
  public double getClosestProjectedDistanceInMetersToLineString(Point referencePoint, LineString geometry) throws PlanItException {
    return getDistanceInMetres(referencePoint.getCoordinate(), getClosestProjectedCoordinateTo(referencePoint, geometry));      
  }  
      
  
  /** find the closest distance in meters from the point to the geometry.Here we project onto the geometry, so we find the actual closest distance instead of merely finding the closest
   * modelled coordinated within the geometry.
   * 
   * @param referencePoint the reference point
   * @param geometry to find closest distance to point to
   * @return distance found in meters
   * @throws PlanItException thrown if error
   */  
  public double getClosestDistanceInMetersMultiLineString(Point referencePoint, MultiLineString geometry) throws PlanItException {
    double minDistanceInMetersForLineSegment = Double.POSITIVE_INFINITY;
    for(int index=0;index<geometry.getNumGeometries();++index) {       
      LineString currLineString = (LineString)geometry.getGeometryN(index);
      minDistanceInMetersForLineSegment = Math.min(minDistanceInMetersForLineSegment,getClosestProjectedDistanceInMetersToLineString(referencePoint, currLineString));
    }      
    return minDistanceInMetersForLineSegment;
  }    

  /** find the closest distance in meters from the point to the geometry.Here we project onto the geometry, so we find the actual closest distance instead of merely finding the closest
   * modelled coordinated within the geometry.
   * 
   * @param referencePoint the reference point
   * @param geometry to find closest distance to point to
   * @return distance found in meters
   * @throws PlanItException thrown if error
   */
  public double getClosestDistanceInMetersToPolygon(Point referencePoint, Polygon geometry) throws PlanItException {
    double minDistanceInMetersForLineSegment = Double.POSITIVE_INFINITY;
    /* explore line segments and project location to find closest distance for each segment */
    Coordinate[] coords = ((Polygon)geometry).getCoordinates();
    if(coords != null) {
      Coordinate prevCoord = coords[0];
      for(int index=1;index<coords.length;++index) {
        Coordinate currCoord = coords[index];
        LineString lineSegment = createLineString(new Coordinate[] {prevCoord,currCoord});
        minDistanceInMetersForLineSegment = Math.min(minDistanceInMetersForLineSegment,getClosestDistanceInMeters(referencePoint, lineSegment));
      }
    }
    return minDistanceInMetersForLineSegment;
  }  

  /** find the closest distance in meters from the point to the geometry. Here we project onto the geometry, so we find the actual closest distance instead of merely finding the closest
   * modelled coordinated within the geometry.
   * 
   * @param referencePoint the reference point
   * @param geometry to find closest distance to point to
   * @return distance found in meters
   * @throws PlanItException thrown if error
   */
  public double getClosestDistanceInMeters(Point referencePoint, Geometry geometry) throws PlanItException {
    if(geometry instanceof Point ) {
      return getClosestExistingCoordinateDistanceInMeters(referencePoint, geometry);
    }else if(geometry instanceof LineString) {
      return getClosestProjectedDistanceInMetersToLineString(referencePoint, (LineString)geometry);
    }else if(geometry instanceof MultiLineString) {
      return getClosestDistanceInMetersMultiLineString(referencePoint, (MultiLineString)geometry);
    }else if(geometry instanceof Polygon){
      return getClosestDistanceInMetersToPolygon(referencePoint, (Polygon)geometry);
    }else {
      throw new PlanItException("unsupported geometry provided for fiding closest distance to point");
    }              
  }

  /**
   * Compute the distance in metres between two (JTS) points assuming the positions are provided in the same crs as registered on this class instance
   * 
   * @param startPosition location of the start point
   * @param endPosition   location of the end point
   * @return distance in metres between the points
   * @throws PlanItException thrown if there is an error
   */
  public double getDistanceInMetres(Point startPosition, Point endPosition) throws PlanItException {
    return getDistanceInMetres(startPosition.getCoordinate(), endPosition.getCoordinate());
  }

  /**
   * Compute the distance in metres between two (JTS) coordinates assuming the positions are provided in the same crs as registered on this class instance
   * 
   * @param startCoordinate location of the start point
   * @param endCoordinate   location of the end point
   * @return distance in metres between the points
   * @throws PlanItException thrown if there is an error
   */
  public double getDistanceInMetres(Coordinate startCoordinate, Coordinate endCoordinate) throws PlanItException {
    try {
      if (crs.equals(CARTESIANCRS)) {
        // cartesian in meters
        double deltaCoordinate0 = startCoordinate.x - endCoordinate.x;
        double deltaCoordinate1 = startCoordinate.y - endCoordinate.y;
        return Math.sqrt(Math.pow(deltaCoordinate0, 2) + Math.pow(deltaCoordinate1, 2));
      } else {
        return JTS.orthodromicDistance(startCoordinate, endCoordinate, crs);
      }
    } catch (Exception e) {
      LOGGER.severe(e.getMessage());
      throw new PlanItException("Error when computing distance in meters between two Positions in JtsUtils", e);
    }
  }

  /**
   * Compute the distance in kilometres between two positions assuming the positions are provided in the same crs as registered on this class instance
   * 
   * @param startPosition location of the start point
   * @param endPosition   location of the end point
   * @return distance in kilometres between the points
   * @throws PlanItException thrown if there is an error
   */
  public double getDistanceInKilometres(Point startPosition, Point endPosition) throws PlanItException {
    return getDistanceInMetres(startPosition, endPosition) / 1000.0;
  }

  /**
   * Compute the distance in kilometres between two vertices assuming the positions are set and based on the same crs as registered on this class instance
   * 
   * @param vertex1 vertex with location
   * @param vertex2 vertex with location
   * @return distance in kilometres between the points
   * @throws PlanItException thrown if there is an error
   */
  public double getDistanceInKilometres(Vertex vertex1, Vertex vertex2) throws PlanItException {
    return getDistanceInKilometres(vertex1.getPosition(), vertex2.getPosition());
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
   * @throws PlanItException thrown if there is an error during processing
   */
  public static Point createPoint(Coordinate coordinate) throws PlanItException {
    Point newPoint = jtsGeometryFactory.createPoint(coordinate);
    return newPoint;
  }  

  /**
   * Create JTS point object from X- and Y-coordinates
   * 
   * @param xCoordinate X-coordinate (longitude assumed)
   * @param yCoordinate Y-coordinate (latitude assumed)
   * @return point object representing the location
   * @throws PlanItException thrown if there is an error during processing
   */
  public static Point createPoint(double xCoordinate, double yCoordinate) throws PlanItException {
    Coordinate coordinate = new Coordinate(xCoordinate, yCoordinate);
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
   * @throws PlanItException thrown if error
   */
  public static LineString createLineString(List<Double> coordinateList) throws PlanItException {
    PlanItException.throwIf(coordinateList.size() % 2 != 0, "coordinate list must contain an even number of entries to correctly identify (x,y) pairs");
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
   * @throws PlanItException thrown if error
   */
  public static LineString createLineString(String value, char ts, char cs) throws PlanItException {
    List<Double> coordinateDoubleList = new ArrayList<Double>();
    String[] tupleString = value.split("[" + ts + "]");
    for (int index = 0; index < tupleString.length; ++index) {
      String xyCoordinateString = tupleString[index];
      String[] coordinateString = xyCoordinateString.split("[" + cs + "]");
      if (coordinateString.length != 2) {
        throw new PlanItException(String.format("invalid coordinate encountered, expected two coordinates in tuple, but found %d", coordinateString.length));
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
   * @throws PlanItException thrown if error
   */
  public static LineString createLineString(Coordinate... coordinates) throws PlanItException {
    return jtsGeometryFactory.createLineString(coordinates);
  }

  /**
   * Based on the csv string construct a line string
   * 
   * @param value the values containing the x,y coordinates in the crs of this instance
   * @param ts    tuple separating string (which must be a a character)
   * @param cs    comma separating string (which must be a a character)
   * @return the LineString created from the String
   * @throws PlanItException thrown if error
   */
  public static LineString createLineStringFromCsvString(String value, String ts, String cs) throws PlanItException {
    if (ts.length() > 1 || cs.length() > 1) {
      PlanItException.throwIf(ts.length() > 1, String.format("tuple separating string to create LineString is not a single character but %s", ts));
      PlanItException.throwIf(cs.length() > 1, String.format("comma separating string to create LineString is not a single character but %s", cs));
    }
    return createLineString(value, ts.charAt(0), cs.charAt(0));
  }

  /**
   * Based on the line string construct a csv string
   * 
   * @param geometry the values containing the x,y coordinates in the crs of this instance
   * @param ts       tuple separating string to use
   * @param cs       comma separating string to use
   * @param df       decinal formatter to format the decimals of the coordinates
   * @return the LineString created from the String
   */
  public static String createCsvStringFromLineString(LineString geometry, Character ts, Character cs, DecimalFormat df) {
    Coordinate[] coordinates = geometry.getCoordinates();
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

  /** create a square bounding box envelope instance based on the passed in reference point and length in meters 
   * of each of the legs, with the point residing in the middle
   * 
   * @param centrePointX x (longitude) coord of centre
   * @param centrePointY y (latitude) coord of centre
   * @param lengthMeters in meters
   * @return envelope with appropriate square bounding box
   */
  public Envelope createBoundingBox(double centrePointX, double centrePointY, double lengthMeters) {
    if(geoCalculator == null) {
      /* cartesian approach (not in meters though )*/
      return new Envelope(centrePointX-lengthMeters, centrePointX+lengthMeters, centrePointY-lengthMeters, centrePointY+lengthMeters);
    }
    
    geoCalculator.setStartingGeographicPoint(centrePointX, centrePointY);
  
    geoCalculator.setDirection( 0, lengthMeters );
    Point2D north = geoCalculator.getDestinationGeographicPoint();
  
    geoCalculator.setDirection( 90, lengthMeters );
    Point2D east = geoCalculator.getDestinationGeographicPoint();
  
    geoCalculator.setDirection( 180, lengthMeters );
    Point2D south = geoCalculator.getDestinationGeographicPoint();
  
    geoCalculator.setDirection( -90, lengthMeters );
    Point2D west = geoCalculator.getDestinationGeographicPoint();   
    
    double y1 = north.getY();
    double y2 = south.getY();
    double x1 = west.getX();
    double x2 = east.getX();
    
    return new Envelope(x1, x2, y2, y1);    
  }

  /** create a square bounding box envelope instance based on the passed in bounding box coordinates and buffer length in meters 
   * resulting in a larger bounding box returned
   * 
   * @param minX x (longitude) coord of minimum extreme point
   * @param minY y (latitude) coord of minimum extreme point
   * @param maxX x (longitude) coord of maximum extreme point
   * @param maxY y (latitude) coord of maximum extreme point
   * 
   * @param lengthMeters in meters
   * @return envelope with appropriate square bounding box
   */
  public Envelope createBoundingBox(double minX, double minY, double maxX, double maxY, double lengthMeters) {
    
    /* buffer for one of the extreme points */
    Envelope localExtremeBoundingBox1 = createBoundingBox(minX, minY, lengthMeters);
    /* buffer for other extreme points */
    Envelope localExtremeBoundingBox2 = createBoundingBox(maxX, maxY, lengthMeters);
    
    /* expand 1 to include 2 */
    localExtremeBoundingBox1.expandToInclude(localExtremeBoundingBox2.getMaxX(), localExtremeBoundingBox2.getMaxY());
    /* adding minimum should not alter anything since it is less extreme than the max values. However, if the user accidentically swapper the min and max inputs
     * to this method,it avoids a wrong result */
    localExtremeBoundingBox1.expandToInclude(localExtremeBoundingBox2.getMinX(), localExtremeBoundingBox2.getMinY());
    return localExtremeBoundingBox1;    
  }

  /**
   * Convert OpenGIS directPosition to JTS coordinates
   * 
   * @param positions List of GeoTools Position objects
   * @return coordinates array of JTS Coordinate objects
   * @throws PlanItException thrown if there is an error
   */
  public static Coordinate[] directPositionsToCoordinates(List<DirectPosition> positions) throws PlanItException {
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

  /**
   * Compute the length of the line string by traversing all nodes and computing the segment by segment distances TODO: find out if a faster way is possible
   * 
   * @param geometry to extract length from
   * @return length in km
   * @throws PlanItException thrown if error
   */
  public double getDistanceInKilometres(LineString geometry) throws PlanItException {
    Coordinate[] coordinates = geometry.getCoordinates();
    int numberOfCoords = coordinates.length;

    if (numberOfCoords > 1) {

      double computedLengthInMetres = 0;
      Coordinate previousCoordinate = coordinates[0];
      for (int index = 1; index < numberOfCoords; ++index) {
        Coordinate currentCoordinate = coordinates[index];
        computedLengthInMetres += getDistanceInMetres(previousCoordinate, currentCoordinate);
        previousCoordinate = currentCoordinate;
      }

      return computedLengthInMetres / 1000.0;
    }
    throw new PlanItException("unable to compute distance for less than two points");
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
   * @throws PlanItException thrown if position could not be located
   */
  public static LineString createCopyWithoutCoordinatesBefore(Point position, LineString geometry) throws PlanItException {
    Optional<Integer> offset = findFirstCoordinatePosition(position.getCoordinate(), geometry);

    if (!offset.isPresent()) {
      throw new PlanItException(String.format("point (%s) does not exist on line string (%s), unable to create copy from this location", position.toString(), geometry.toString()));
    }

    Coordinate[] coordinates = copyCoordinatesFrom(offset.get(), geometry);

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
   * @param geometry linestring
   * @return copy of the line string without indicated coordinates
   * @throws PlanItException thrown if position could not be located
   */
  public static LineString createCopyWithoutCoordinatesAfter(Point position, LineString geometry) throws PlanItException {
    Optional<Integer> offset = findFirstCoordinatePosition(position.getCoordinate(), geometry);

    if (!offset.isPresent()) {
      throw new PlanItException(String.format("point (%s) does not exist on line string %s, unable to create copy from this location", position.toString(), geometry.toString()));
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
  
  /** extend the given line segment in one or two directions with a given distance in meters. One can also extend on both sides at the same time.
   * Note that this is not correcting for the curvature of the earth in case one uses a geodetic crs.
   * 
   * @param source original line segment
   * @param extensionInMeters desired extension in meters
   * @param extendStart when true extend from start coordinate onwards
   * @param extendEnd when true extend further from end coordinate onwards
   * @return extended line segment based on the passed in parameters
   * @throws PlanItException thrown if error
   */
  public LineSegment createExtendedLineSegment(final LineSegment source, double extensionInMeters, boolean extendStart, boolean extendEnd) throws PlanItException {
    /* obtain heading first */        
    DirectPosition newStartPosition = null;
    if(extendStart) {
      newStartPosition = createPositionInDirection(source.p0, getAzimuthInDegrees(source.p1, source.p0),extensionInMeters);
    }
    
    DirectPosition newEndPosition = null;    
    if(extendEnd) {
      newEndPosition = createPositionInDirection(source.p1, getAzimuthInDegrees(source.p0, source.p1),extensionInMeters);
    }    
   
    Coordinate startCoordinate = newStartPosition!=null ? createCoordinate(newStartPosition) : source.p0;
    Coordinate endCoordinate = newEndPosition!=null ? createCoordinate(newEndPosition) : source.p1;
    return createLineSegment( startCoordinate, endCoordinate ); 
  }  


  /** create a new direct position in the given direction starting from the start coordinate for a distance of the given number of meters
   * using the crs
   * 
   * @param start position
   * @param azimuthInDegrees heading
   * @param distanceInMeters distance
   * @return new position in desired location
   * @throws PlanItException thrown if error
   */
  private DirectPosition createPositionInDirection(Coordinate start, double azimuthInDegrees, double distanceInMeters) throws PlanItException {    
    try {
      geoCalculator.setStartingGeographicPoint(start.x, start.y);
      geoCalculator.setDirection(azimuthInDegrees, distanceInMeters);    
      return geoCalculator.getDestinationPosition();
    }catch (Exception e) {
      LOGGER.severe(e.getMessage());
      throw new PlanItException("Unable to create a position in the desired direction", e);
    }
  }

  /**
   * find first position where the coordinate resides on the geometry.
   * 
   * @param coordinateToLocate the one to locate
   * @param offset             start searching from offset position
   * @param geometry           to locate from
   * @return the position if present
   */
  public static Optional<Integer> findFirstCoordinatePosition(Coordinate coordinateToLocate, int offset, LineString geometry) {
    if (geometry == null || coordinateToLocate == null) {
      return Optional.empty();
    }

    int numCoordinates = geometry.getNumPoints();
    for (int index = offset; index < numCoordinates; ++index) {
      Coordinate coordinate = geometry.getCoordinateN(index);
      if (coordinate.equals2D(coordinateToLocate)) {
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
   * @return the position if present
   */
  public static Optional<Integer> findFirstCoordinatePosition(Coordinate coordinateToLocate, LineString geometry) {
    return findFirstCoordinatePosition(coordinateToLocate, 0, geometry);
  }

  /**
   * copy the coordinates in the line string starting at the given offset (included)
   * 
   * @param offset   to start at
   * @param geometry to copy from
   * @return coordinate array, when offset is out of bounds null is returned
   * @throws PlanItException thrown if error
   */
  public static Coordinate[] copyCoordinatesFrom(int offset, LineString geometry) throws PlanItException {
    return copyCoordinatesFromUpToNotIncluding(offset, geometry.getNumPoints(), geometry);
  }

  /**
   * copy the coordinates in the line string until the given location, the location is included
   * 
   * @param untilPoint to stop (not included)
   * @param geometry   to copy from
   * @return coordinate array, when offset is out of bounds null is returned
   * @throws PlanItException thrown if error
   */
  public static Coordinate[] copyCoordinatesUpToNotIncluding(int untilPoint, LineString geometry) throws PlanItException {
    return copyCoordinatesFromUpToNotIncluding(0, untilPoint, geometry);
  }

  /**
   * copy the coordinates in the line string from-to the given locations, the locations are included
   * 
   * @param offset     to start at (included)
   * @param untilPoint to end with (not included)
   * @param geometry   to copy from
   * @return coordinate array, when offset is out of bounds empty coordinate array is returned
   * @throws PlanItException thrown if error
   */
  public static Coordinate[] copyCoordinatesFromUpToNotIncluding(int offset, int untilPoint, LineString geometry) throws PlanItException {
    PlanItException.throwIfNull(geometry, "provided geometry to copy coordinates from is null");

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
   * collect the crs used by this instance of the utils class
   * 
   * @return crs used by instance
   */
  public CoordinateReferenceSystem getCoordinateReferenceSystem() {
    return crs;
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

  /**
   *  collect the azimuth heading between the two coordinates in decimal degrees between -180 and 180, from location 1 to location 2
   *  
   *@param coordinate1 first Coordinate
   *@param coordinate2 second Coordinate
   *@return azimuth in degrees
   */
  public double getAzimuthInDegrees(Coordinate coordinate1, Coordinate coordinate2) {
    geoCalculator.setStartingGeographicPoint(coordinate1.getX(), coordinate1.getY());
    geoCalculator.setDestinationGeographicPoint(coordinate2.getX(), coordinate2.getY());
    return geoCalculator.getAzimuth();
  }


  
}
