package org.goplanit.utils.geo;

import org.geotools.api.geometry.Position;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.api.referencing.crs.GeographicCRS;
import org.geotools.api.referencing.crs.ProjectedCRS;
import org.geotools.geometry.Position2D;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.referencing.GeodeticCalculator;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.referencing.factory.epsg.CartesianAuthorityFactory;
import org.goplanit.utils.exceptions.PlanItRunTimeException;
import org.goplanit.utils.graph.Vertex;
import org.goplanit.utils.math.Precision;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.linearref.LinearLocation;
import org.locationtech.jts.linearref.LocationIndexedLine;
import si.uom.SI;
import tech.units.indriya.unit.Units;

import javax.measure.Unit;
import javax.measure.UnitConverter;
import javax.measure.quantity.Length;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.function.Predicate;
import java.util.logging.Logger;

import static org.goplanit.utils.epsg.ProjectedEpsgCodesByCountry.WORLD_PROJECTED_WGS84;

/**
 * General geographic JTS utilities that rely on a known Coordinate Reference system (CRS). 
 * Uses geodetic distance when possible. In case the CRS is not based on an ellipsoid (2d plane) it will simply
 * compute the distance between coordinates using Pythagoras with the unit distance in meters,
 * consistent with the {@code CartesianAuthorityFactory.GENERIC_2D}
 * 
 * It is assumed that x coordinate refers to latitude and y coordinate refers to longitude
 * 
 * @author markr
 *
 */
public class PlanitJtsCrsUtils {

  /** the logger */
  private static final Logger LOGGER = Logger.getLogger(PlanitJtsCrsUtils.class.getCanonicalName());
    
  /*
   * the geotools gt-epsg-hsql dependency tries to take over the logging and the formatting of the logging.
   * It is initialised whenever {@code CRS.decode} is invoked from some of
   * this class' static methods. Therefore, here we programmatically disable this unwanted behaviour
   */
  static {
    PlanitCrsUtils.silenceHsqlLogging();
  }     

  /** the crs to use */
  protected final CoordinateReferenceSystem crs;
  
  /** geodetic calculator to use in tandem with the used CRS (null if not needed)*/
  protected GeodeticCalculator geoCalculator;

  /** for linear projected CRS we may need additional to meter converter, this tracks that converter */
  protected UnitConverter toLinearMeterConverter;

  /** jts geometry factory, jts geometry differs from opengis implementation by not carrying the crs and being more lightweight */
  protected static final GeometryFactory jtsGeometryFactory = JTSFactoryFinder.getGeometryFactory();

  /**
   * Predicate to use in distance calculation to check if CRS is linear in length in metres
   */
  protected static final Predicate<CoordinateReferenceSystem> checkCrsLinearInMetres =
          crs -> PlanitCrsUtils.isLinearCRSWithExactUnit(crs, Units.METRE);

  /**
   * Predicate to use in distance calculation to check if CRS is linear in length in any unit
   */
  protected static final Predicate<CoordinateReferenceSystem> checkCrsLinearInLength =
          PlanitCrsUtils::isLinearCRSWithLengthCompatibleUnit;
  /**
   * Default Coordinate Reference System: WGS84
   */
  public static final DefaultGeographicCRS DEFAULT_GEOGRAPHIC_CRS = DefaultGeographicCRS.WGS84;

  /** WGS 84 / Pseudo‑Mercator in metres, projected version of WGS84 (EPSG:3857) */
  public static final CoordinateReferenceSystem DEFAULT_PROJECTED_CRS_EPSG_3857 =
          PlanitCrsUtils.createCoordinateReferenceSystem(WORLD_PROJECTED_WGS84);

  /**
   * In absence of a geographic crs we can also use cartesian: GENERIC_2D
   */
  public static final CoordinateReferenceSystem CARTESIANCRS = CartesianAuthorityFactory.GENERIC_2D;

  /** EPSG:404000 String identifier for Caretesian CRS */
  public static final String CARTESIANCRS_EPSG_CODE = CARTESIANCRS.getIdentifiers().toArray()[0].toString();
    
  /**
   * Constructor
   * 
   * Uses default coordinate reference system
   */
  public PlanitJtsCrsUtils() {
    this(DEFAULT_GEOGRAPHIC_CRS);
  }

  /**
   * Compute the distance between two (JTS) coordinates assuming the positions are provided in the same crs as
   * registered on this class instance. Check for linearity and possibly exact unit of the CRS to increase performance
   * based on provided predicate
   *
   * @param startCoordinate location of the start point
   * @param endCoordinate   location of the end point
   * @param checkCrsLinearityConstraint the check to determine if we can use cheaper linear calculation instead of
   *         expensive orthodromicDistance
   * @param ifLinearSkipSquareRootCalc when linear this flag indicates whether to skip square root
   * @return distance in metres between the points
   */
  private double getOptimisedDistance(
          final Coordinate startCoordinate,
          final Coordinate endCoordinate,
          final Predicate<CoordinateReferenceSystem> checkCrsLinearityConstraint,
          boolean ifLinearSkipSquareRootCalc){
    if(startCoordinate == null){
      throw new PlanItRunTimeException("Start coordinate is null when computing distance in meters between " +
              "two locations in JtsCrsUtils");
    }
    if(endCoordinate == null){
      throw new PlanItRunTimeException("End coordinate is null when computing distance in meters between two " +
              "locations in JtsCrsUtils");
    }
    try {
      if (checkCrsLinearityConstraint.test(crs)) {
        // compute direct length
        double deltaCoordinate0 = startCoordinate.x - endCoordinate.x;
        double deltaCoordinate1 = startCoordinate.y - endCoordinate.y;
        return ifLinearSkipSquareRootCalc ?
                deltaCoordinate0 * deltaCoordinate0 + deltaCoordinate1*deltaCoordinate1
                : Math.sqrt(deltaCoordinate0 * deltaCoordinate0 + deltaCoordinate1*deltaCoordinate1);
      } else {
        return JTS.orthodromicDistance(startCoordinate, endCoordinate, crs);
      }
    } catch (Exception e) {
      LOGGER.severe(e.getMessage());
      throw new PlanItRunTimeException("Error when computing distance in meters between two Positions in JtsUtils", e);
    }
  }

  /**
   * Constructor
   * 
   * @param coordinateReferenceSystem OpenGIS CoordinateReferenceSystem object containing geometry
   */
  public PlanitJtsCrsUtils(CoordinateReferenceSystem coordinateReferenceSystem) {
    this.crs = coordinateReferenceSystem;
    this.toLinearMeterConverter = null;

    /* viable only if non-cartesian based */
    if (crs instanceof ProjectedCRS) {
      // no need for geodetic distance calcs, use projection direction possibly with meter
      // conversion layered on top
      this.geoCalculator = null;
      this.toLinearMeterConverter =
          SI.METRE.getConverterTo((Unit<Length>) crs.getCoordinateSystem().getAxis(0).getUnit());
    } else if (crs instanceof GeographicCRS) {
      // Safe to assume coordinates are in degrees; use GeodeticCalculator.
      geoCalculator = new GeodeticCalculator(getCoordinateReferenceSystem());
    } else {
      LOGGER.info(String.format("Unsupported CRS encountered %s", coordinateReferenceSystem.toString()));
    }
  }

  /** find the distance between the closest coordinate on the geometry's coordinates. Note that this is likely NOT
   * the closest point to the geometry as this likely lies on the line connecting the two closest points.
   *
   * @param coord reference
   * @param geometry to check against, explore all its coordinates
   * @return closest coordinate distance
   */
  public double getClosestExistingCoordinateDistanceInMeters(Coordinate coord, Geometry geometry){
    if(geometry !=null ){
      return getDistanceInMetres(getClosestExistingCoordinateToPoint(coord, geometry), coord);
    }
    return Double.POSITIVE_INFINITY;
  }
  
  /** find the coordinate on the geometry with the closest distance to the reference point. Note that this is likely NOT
   * the closest point to the geometry as this likely lies on the line connecting the two closest points.
   * 
   * @param coord reference
   * @param geometry to check against, explore all its coordinates
   * @return closest coordinate distance
   */
  public Coordinate getClosestExistingCoordinateToPoint(Coordinate coord, Geometry geometry){
    double minDistanceMetersToCoordinate = Double.POSITIVE_INFINITY;
    Coordinate closestCoordinate = null;
    if(geometry !=null ){      
      Coordinate[] coordinates = geometry.getCoordinates();
      for(int index = 0 ; index < coordinates.length; ++index) {
        Coordinate coordinate = coordinates[index];
        double distanceMeters = getDistanceInMetres(coord, coordinate);
        if(minDistanceMetersToCoordinate > distanceMeters) {
          minDistanceMetersToCoordinate = distanceMeters;
          closestCoordinate = coordinate;
        }
      }
    }
    return closestCoordinate;
  }  
  
  /** Find the coordinate on the line string with the closest distance to the reference referenceGeometry.
   * Note that this is likely NOT the closest point to the geometry as this likely lies on the line
   * connecting the two closest points.
   * 
   * @param <T> type of line string
   * @param referenceGeometry to use
   * @param lineString to verify closest coordinate
   * @return closest existing coordinate on line string to find coordinate on
   */
  public <T extends LineString> Coordinate getClosestExistingLineStringCoordinateToGeometry(
          Geometry referenceGeometry, T lineString){
    return getClosestExistingLineStringCoordinateToGeometry(
            referenceGeometry, lineString, 0, lineString.getNumPoints()-1);
  }

  /** Find the coordinate on the line string with the closest distance to the reference referenceGeometry.
   * Note that this is likely NOT the closest point to the geometry as this likely lies on the line connecting
   * the two closest points.
   *
   * @param <T> type of line string
   * @param referenceGeometry to use
   * @param lineString to verify closest coordinate
   * @param startIndex start index offset to use (inclusive)
   * @param endIndex end index, i.e., last index to consider (inclusive)
   * @return closest existing coordinate on line string to find coordinate on
   */
  public <T extends LineString> Coordinate getClosestExistingLineStringCoordinateToGeometry(
      Geometry referenceGeometry, T lineString, int startIndex, int endIndex){
    double minDistanceMetersToCoordinate = Double.POSITIVE_INFINITY;
    Coordinate closestCoordinate = null;
    for(int index = startIndex; index <= endIndex ; ++index) {
      Coordinate coordinate = lineString.getCoordinateN(index);
      Coordinate closestProjectedReferenceCoordinate =
              getClosestProjectedCoordinateOnGeometry(coordinate, referenceGeometry);
      double distanceMeters = getDistanceInMetres(closestProjectedReferenceCoordinate, coordinate);
      if(minDistanceMetersToCoordinate > distanceMeters) {
        minDistanceMetersToCoordinate = distanceMeters;
        closestCoordinate = coordinate;
      }
    }
    return closestCoordinate;
  }

  /** find the coordinate on the polygon with the closest distance to the reference geometry. Note that this is
   * likely NOT the closest point to the geometry as this likely lies on the line connecting the two closest points.
   * 
   * @param referenceGeometry to use
   * @param polygon to verify closest coordinate
   * @return closest existing coordinate on polygon to find coordinate on
   */  
  private Coordinate getClosestExistingPolygonCoordinateToGeometry(Geometry referenceGeometry, Polygon polygon){
    LinearRing exteriorGeometry = polygon.getExteriorRing();
    return getClosestExistingLineStringCoordinateToGeometry(referenceGeometry, exteriorGeometry);    
  }   
  
  /** find the closest location from the reference point to the geometry expressed as a linear location. Here we
   *  project onto the geometry, so we find the location with the actual closest distance and create a linear
   *  location regardless if this coordinate is part of the geometry as a predefined coordinate at an extreme point.
   * 
   * @param reference the reference location
   * @param geometry to find closest distance to point to
   * @return linearLocation found
   */  
  public LinearLocation getClosestProjectedLinearLocationOnGeometry(Coordinate reference, Geometry geometry){
    if(geometry instanceof Point) {
      throw new PlanItRunTimeException("Cannot create linear Location from a single point");
    }else if(geometry instanceof LineString ) {
        return getClosestProjectedLinearLocationOnLineString(reference, (LineString)geometry);
    }else if(geometry instanceof Polygon) {
      return getClosestProjectedLinearLocationOnPolygon(reference, (Polygon)geometry);
    }else {
      throw new PlanItRunTimeException("Method getClosestLinearLocationOnGeometry not supported for " +
              "provided geometry type %s",geometry.getClass().getName());
    }      
  }   
  
  /** Find the closest location from the reference coordinate to the line string expressed as a linear location.
   * Here we project onto the geometry, so we find the location with the actual closest distance and create a
   * linear location regardless if this coordinate is part of the geometry as a predefined coordinate at an
   * extreme point.
   * 
   * @param referenceCoordinate the reference point
   * @param lineString to find closest distance to point to (must be a linear geometry)
   * @return linearLocation found
     */  
  public LinearLocation getClosestProjectedLinearLocationOnLineString(
          Coordinate referenceCoordinate, LineString lineString){
    LocationIndexedLine locIndexedLine = new LocationIndexedLine(lineString);
    return locIndexedLine.project(referenceCoordinate);
  } 
  
  /** Find the closest location from the reference coordinate to the polygon expressed as a linear location.
   *  Here we project onto the geometry, so we find the location with the actual closest distance and create a
   *  linear location regardless if this coordinate is part of the geometry as a predefined coordinate at
   *  an extreme point.
   * 
   * @param referenceCoordinate the reference point
   * @param polygon to find closest distance to point to (must be a linear geometry)
   * @return linearLocation found
   */  
  public LinearLocation getClosestProjectedLinearLocationOnPolygon(Coordinate referenceCoordinate, Polygon polygon){
    PlanItRunTimeException.throwIfNull(referenceCoordinate,
            "Provided coordinate is null when computing closest location to given coordinate");
    PlanItRunTimeException.throwIfNull(polygon,
            "Provided polygon is null when computing closest location to given coordinate");
    PlanItRunTimeException.throwIfNull(polygon.getNumPoints()<2,
            "Provided polygon has too few coordinates");
    
    double minDistanceMeters = Double.POSITIVE_INFINITY;
    LinearLocation closestLinearLocation = null;
    Coordinate[] polygonCoordinates = polygon.getExteriorRing().getCoordinates();
    /* for each coordinate of the ring determine the distance, from all distances collect the smallest one */
    Coordinate prevCoordinate = polygonCoordinates[0];
    for(int index = 1; index < polygonCoordinates.length ; ++index) {
      Coordinate currCoordinate = polygonCoordinates[index];
      LineString lineString = jtsGeometryFactory.createLineString(new Coordinate[] {prevCoordinate,currCoordinate});
      LinearLocation linearLocation = getClosestProjectedLinearLocationOnLineString(referenceCoordinate, lineString);
      double distanceMeters = getDistanceInMetres(linearLocation.getCoordinate(lineString), referenceCoordinate);
      if(distanceMeters < minDistanceMeters) {
        minDistanceMeters = distanceMeters;
        closestLinearLocation = linearLocation;
      }
    }
    return closestLinearLocation;
  }   
   
  
  /** find the closest location from any existing coordinate of the reference geometry to the line string geometry
   * as a linear location. Here we project onto the geometry, so we find the location with the actual closest distance
   * and extract the linear location regardless if this coordinate is part of the geometry as a predefined
   * coordinate at an extreme point.
   * 
   * @param referenceGeometry the reference geometry
   * @param linearGeometry to find closest distance to point to (must be a linear geometry)
   * @return linearLocation found
   */  
  public LinearLocation getClosestGeometryExistingCoordinateToProjectedLinearLocationOnLineString(
          Geometry referenceGeometry, LineString linearGeometry) {

    double minDistanceMetersToCoordinate = Double.POSITIVE_INFINITY;
    LinearLocation closestLocation = null;
    Coordinate[] referenceGeometryCoordinates = referenceGeometry.getCoordinates();
    /* for each coordinate of the reference geometry find min distance projected coordinate on linearGeometry... */
    for(int index = 0; index < referenceGeometry.getNumPoints() ; ++index) {
      Coordinate referenceCoordinate = referenceGeometryCoordinates[index];
      LinearLocation location = getClosestProjectedLinearLocationOnLineString(referenceCoordinate, linearGeometry);
      double distanceMeters = getDistanceInMetres(location.getCoordinate(linearGeometry), referenceCoordinate);
      
      /* ... select minimum of all found connecting line segments */
      if(minDistanceMetersToCoordinate > distanceMeters) {
        minDistanceMetersToCoordinate = distanceMeters;
        closestLocation = location;
      }      
    }
    return closestLocation;
  }  
  
  /** find the closest projected coordinate from the reference point to the geometry. Here we project onto the
   * geometry, so we find the location with the actual closest distance and create a coordinate at this
   * location regardless if this coordinate is part of the geometry as a predefined coordinate at an extreme point.
   * 
   * @param reference the reference location
   * @param geometry to find closest distance to point to
   * @return distance found in meters
   */  
  public Coordinate getClosestProjectedCoordinateOnGeometry(Coordinate reference, Geometry geometry){
    if(geometry instanceof Point) {
      return geometry.getCoordinate();
    }else if(geometry instanceof LineString ) {
        return getClosestProjectedCoordinateOnLineString(reference, (LineString)geometry);
    }else if(geometry instanceof Polygon) {
      return getClosestPojectedCoordinateOnPolygon(reference, (Polygon)geometry);
    }else {
      throw new PlanItRunTimeException("Method getClosestProjectedCoordinateTo not supported for provided " +
              "geometry type %s",geometry.getClass().getName());
    }     
  }  
  
  
  /** find the closest projected coordinate from the reference point to the line string. Here we project onto the
   * geometry, so we find the location with the actual closest distance and create a coordinate at this
   * location regardless if this coordinate is part of the geometry as a predefined coordinate at an extreme point.
   * 
   * @param reference the reference point
   * @param lineString to find closest distance to point to
   * @return distance found in meters
   */  
  public Coordinate getClosestProjectedCoordinateOnLineString(Coordinate reference, LineString lineString){
    return getClosestProjectedLinearLocationOnGeometry(reference, lineString).getCoordinate(lineString);
  }   
  
  /** find the closest location from the reference coordinate to the polygon expressed as a linear location.
   * Here we project onto the polygon, so we find the location with the actual closest distance and create a
   * linear location regardless if this coordinate is part of the polygon as a predefined coordinate at an extreme point.
   * 
   * @param reference the reference location
   * @param polygon to find closest distance to point to using its exterior ring
   * @return linearLocation found
   */  
  public Coordinate getClosestPojectedCoordinateOnPolygon(Coordinate reference, Polygon polygon){
    /* collect linear location and from that reconstruct the line string to extract the projected coordinate from */
    LinearLocation linearLocation = getClosestProjectedLinearLocationOnPolygon(reference, polygon);
    int lineSegmentIndex = linearLocation.getSegmentIndex();
    
    return linearLocation.getCoordinate(
        PlanitJtsUtils.createLineString(
                polygon.getCoordinates()[lineSegmentIndex],polygon.getCoordinates()[lineSegmentIndex+1]));
  }  
    
  /** find the closest distance in meters from the point to the geometry.Here we project onto the geometry,
   * so we find the actual closest distance instead of merely finding the closest modelled coordinated within
   * the geometry.
   * 
   * @param reference the reference point
   * @param geometry to find closest distance to point to
   * @return distance found in meters
   */
  public double getClosestProjectedDistanceInMetersToLineString(Coordinate reference, LineString geometry){
    return getDistanceInMetres(reference, getClosestProjectedCoordinateOnLineString(reference, geometry));
  }  
      
  
  /** find the closest distance in meters from the point to the geometry.Here we project onto the geometry, so we find the actual closest distance instead of merely finding the closest
   * modelled coordinated within the geometry.
   * 
   * @param reference the reference location
   * @param geometry to find closest distance to point to
   * @return distance found in meters
   */  
  public double getClosestDistanceInMetersMultiLineString(Coordinate reference, MultiLineString geometry){
    double minDistanceInMetersForLineSegment = Double.POSITIVE_INFINITY;
    for(int index=0;index<geometry.getNumGeometries();++index) {       
      LineString currLineString = (LineString)geometry.getGeometryN(index);
      minDistanceInMetersForLineSegment =
              Math.min(minDistanceInMetersForLineSegment,
                      getClosestProjectedDistanceInMetersToLineString(reference, currLineString));
    }      
    return minDistanceInMetersForLineSegment;
  }    

  /** find the closest distance in meters from the point to the geometry.Here we project onto the geometry,
   * so we find the actual closest distance instead of merely finding the closest
   * modelled coordinated within the geometry.
   * 
   * @param reference the reference point
   * @param geometry to find closest distance to point to
   * @return distance found in meters
   */
  public double getClosestDistanceInMetersToPolygon(Coordinate reference, Polygon geometry){
    double minDistanceInMetersForLineSegment = Double.POSITIVE_INFINITY;
    /* explore line segments and project location to find closest distance for each segment */
    Coordinate[] coords = geometry.getCoordinates();
    if(coords != null) {
      Coordinate prevCoord = coords[0];
      for(int index=1;index<coords.length;++index) {
        Coordinate currCoord = coords[index];
        LineString lineSegment = PlanitJtsUtils.createLineString(prevCoord,currCoord);
        minDistanceInMetersForLineSegment =
                Math.min(minDistanceInMetersForLineSegment,
                        getClosestDistanceInMeters(reference, lineSegment));
        prevCoord = currCoord;
      }
    }
    return minDistanceInMetersForLineSegment;
  }  

  /** find the closest distance in meters from the point to the geometry. Here we project onto the geometry,
   * so we find the actual closest distance instead of merely finding the closest modelled coordinated within
   * the geometry.
   * 
   * @param reference the reference location
   * @param geometry to find closest distance to point to
   * @return distance found in meters
   */
  public double getClosestDistanceInMeters(Coordinate reference, Geometry geometry){
    if(geometry instanceof Point ) {
      return getClosestExistingCoordinateDistanceInMeters(reference, geometry);
    }else if(geometry instanceof LineString) {
      return getClosestProjectedDistanceInMetersToLineString(reference, (LineString)geometry);
    }else if(geometry instanceof MultiLineString) {
      return getClosestDistanceInMetersMultiLineString(reference, (MultiLineString)geometry);
    }else if(geometry instanceof Polygon){
      return getClosestDistanceInMetersToPolygon(reference, (Polygon)geometry);
    }else {
      throw new PlanItRunTimeException("Unsupported geometry provided for finding closest distance to point");
    }              
  }

  /**
   * Compute the distance in metres between two (JTS) points assuming the positions are provided in the same
   * crs as registered on this class instance
   * 
   * @param startPosition location of the start point
   * @param endPosition   location of the end point
   * @return distance in metres between the points
   */
  public double getDistanceInMetres(Point startPosition, Point endPosition){
    return getDistanceInMetres(startPosition.getCoordinate(), endPosition.getCoordinate());
  }

  /**
   * Compute the distance in metres between two (JTS) coordinates assuming the positions are provided in the same
   * crs as registered on this class instance
   * 
   * @param startCoordinate location of the start point
   * @param endCoordinate   location of the end point
   * @return distance in metres between the points
   */
  public double getDistanceInMetres(Coordinate startCoordinate, Coordinate endCoordinate){
    return getOptimisedDistance(startCoordinate, endCoordinate, checkCrsLinearInMetres, false /* force metres */);
  }

  /**
   * Compute the linear distance between two (JTS) coordinates assuming the positions are provided in the same crs
   * as registered on this class instance. The unit is not guaranteed to be in metres but it is guaranteed to be
   * linear even if the CRS is not
   *
   * @param startCoordinate location of the start point
   * @param endCoordinate   location of the end point
   * @return distance in metres between the points
   */
  public double getLinearDistance(Coordinate startCoordinate, Coordinate endCoordinate){
    return getOptimisedDistance(startCoordinate, endCoordinate, checkCrsLinearInLength, true /* do not force unit */);
  }

  /**
   * Determine if the distance in metres between two (JTS) coordinates (assuming the positions are provided in the same crs as registered on this class instance) is within the given distance
   *
   * @param startCoordinate location of the start point
   * @param endCoordinate   location of the end point
   * @param maxDistanceMeters allowed
   * @return true when distance in metres between the points is smaller, false otherwise
   */
  public boolean isDistanceWithinMetres(Coordinate startCoordinate, Coordinate endCoordinate, double maxDistanceMeters){
    return getDistanceInMetres(startCoordinate,endCoordinate) < maxDistanceMeters;
  }

  /**
   * Determine if the distance in metres between two (JTS) coordinates (assuming the positions are provided
   * in the same crs as registered on this class instance) is within the given distance
   *
   * @param startPosition location of the start point
   * @param endPosition   location of the end point
   * @param maxDistanceMeters allowed
   * @return true when distance in metres between the points is smaller, false otherwise
   */
  public boolean isDistanceWithinMetres(Point startPosition, Point endPosition, double maxDistanceMeters){
    return getDistanceInMetres(startPosition,endPosition) < maxDistanceMeters;
  }

  /**
   * Compute the distance in kilometres between two positions assuming the positions are provided in the same
   * crs as registered on this class instance
   * 
   * @param startPosition location of the start point
   * @param endPosition   location of the end point
   * @return distance in kilometres between the points
   */
  public double getDistanceInKilometres(Point startPosition, Point endPosition) {
    return getDistanceInMetres(startPosition, endPosition) / 1000.0;
  }

  /**
   * Compute the distance in kilometres between two vertices assuming the positions are set and based on
   * the same crs as registered on this class instance
   * 
   * @param vertex1 vertex with location
   * @param vertex2 vertex with location
   * @return distance in kilometres between the points
   */
  public double getDistanceInKilometres(Vertex vertex1, Vertex vertex2) {
    return getDistanceInKilometres(vertex1.getPosition(), vertex2.getPosition());
  }

  /** create a square bounding box envelope instance based on the passed in reference point and length in meters 
   * of each of the legs, with the point residing in the middle, it is expected the locations (when no cartesian)
   * are in LAT/LONG regardless of the transformation of the underlying CRS
   * 
   * @param centrePointX x coord of centre in crs
   * @param centrePointY y coord of centre in crs
   * @param lengthMeters in meters
   * @return envelope with appropriate square bounding box
   */
  @SuppressWarnings("unchecked")
  public Envelope createBoundingBox(double centrePointX, double centrePointY, double lengthMeters) {

    double lengthInCrsUnits = lengthMeters;
    /* non geodetic approach*/
    if(geoCalculator == null) {
      lengthInCrsUnits = toLinearMeterConverter.convert(lengthMeters);
      return new Envelope(
              centrePointX-lengthInCrsUnits,
              centrePointX+lengthInCrsUnits,
              centrePointY-lengthInCrsUnits,
              centrePointY+lengthInCrsUnits);
    }

    // geodetic approach
    geoCalculator.setStartingGeographicPoint(centrePointX, centrePointY);

    geoCalculator.setDirection( 0, lengthInCrsUnits );
    Point2D north = geoCalculator.getDestinationGeographicPoint();

    geoCalculator.setDirection( 90, lengthInCrsUnits );
    Point2D east = geoCalculator.getDestinationGeographicPoint();

    geoCalculator.setDirection( 180, lengthInCrsUnits );
    Point2D south = geoCalculator.getDestinationGeographicPoint();

    geoCalculator.setDirection( -90, lengthInCrsUnits );
    Point2D west = geoCalculator.getDestinationGeographicPoint();

    double y1 = north.getY();
    double y2 = south.getY();
    double x1 = west.getX();
    double x2 = east.getX();

    return new Envelope(x1, x2, y2, y1);
  }
  
  /** create a square bounding box envelope instance based on an existing envelope bounding box and buffer length
   * in meters resulting in a larger bounding box returned, it is expected the locations (when no cartesian)
   * are in lat/long regardless of the transformation of the underlying CRS
   * 
   * @param boundingBox original bounding box
   * @param lengthMeters buffer length in meters
   * @return envelope with appropriate square bounding box
   */
  public Envelope createBoundingBox(Envelope boundingBox, double lengthMeters) {
    return createBoundingBox(
            boundingBox.getMinX(),boundingBox.getMinY(), boundingBox.getMaxX(), boundingBox.getMaxY(), lengthMeters);
  }  

  /** create a square bounding box envelope instance based on the passed in bounding box coordinates and buffer
   *  length in meters
   * resulting in a larger bounding box returned, it is expected the locations (when no cartesian) are in
   * lat/long regardless of the transformation
   * of the underlying CRS
   * 
   * @param minX x (longitude) coord of minimum extreme point
   * @param minY y (latitude) coord of minimum extreme point
   * @param maxX x (longitude) coord of maximum extreme point
   * @param maxY y (latitude) coord of maximum extreme point
   * @param lengthMeters buffer length in meters
   * @return envelope with appropriate square bounding box
   */
  public Envelope createBoundingBox(double minX, double minY, double maxX, double maxY, double lengthMeters) {
    
    /* buffer for one of the extreme points */
    Envelope localExtremeBoundingBox1 = createBoundingBox(minX, minY, lengthMeters);
    /* buffer for other extreme points */
    Envelope localExtremeBoundingBox2 = createBoundingBox(maxX, maxY, lengthMeters);
    
    /* expand 1 to include 2 */
    localExtremeBoundingBox1.expandToInclude(localExtremeBoundingBox2.getMaxX(), localExtremeBoundingBox2.getMaxY());
    /* adding minimum should not alter anything since it is less extreme than the max values. However, if the user
    accidentally swapper the min and max inputs to this method,it avoids a wrong result */
    localExtremeBoundingBox1.expandToInclude(localExtremeBoundingBox2.getMinX(), localExtremeBoundingBox2.getMinY());
    return localExtremeBoundingBox1;    
  }

  /**
   * Compute the length of the line string by traversing all nodes and computing the segment by segment distances
   * TODO: find out if a faster way is possible
   * 
   * @param geometry to extract length from
   * @return length in km
   */
  public double getDistanceInKilometres(LineString geometry){
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
    throw new PlanItRunTimeException("Unable to compute distance for less than two points");
  }
  
  /** extend the given line segment in one or two directions with a given distance in meters. One can also
   * extend on both sides at the same time.
   * Note that this is not correcting for the curvature of the earth in case one uses a geodetic crs.
   * 
   * @param source original line segment
   * @param extensionInMeters desired extension in meters
   * @param extendStart when true extend from start coordinate onwards
   * @param extendEnd when true extend further from end coordinate onwards
   * @return extended line segment based on the passed in parameters
   */
  public LineSegment createExtendedLineSegment(
          final LineSegment source, double extensionInMeters, boolean extendStart, boolean extendEnd){
    /* obtain heading first */        
    Position newStartPosition = null;
    if(extendStart) {
      newStartPosition = createPositionInDirection(
              source.p0, getAzimuthInDegrees(source.p1, source.p0, false),extensionInMeters);
    }
    
    Position newEndPosition = null;
    if(extendEnd) {
      newEndPosition = createPositionInDirection(
              source.p1, getAzimuthInDegrees(source.p0, source.p1, false),extensionInMeters);
    }    
   
    Coordinate startCoordinate = newStartPosition!=null ? PlanitJtsUtils.createCoordinate(newStartPosition) : source.p0;
    Coordinate endCoordinate = newEndPosition!=null ? PlanitJtsUtils.createCoordinate(newEndPosition) : source.p1;
    return PlanitJtsUtils.createLineSegment( startCoordinate, endCoordinate ); 
  }  


  /** create a new direct position in the given direction starting from the start coordinate for a
   * distance of the given number of meters
   * using the crs
   * 
   * @param start position
   * @param azimuthInDegrees heading
   * @param distanceInMeters distance
   * @return new position in desired location
   */
  private Position createPositionInDirection(Coordinate start, double azimuthInDegrees, double distanceInMeters){
    try {
      double distanceInCrsUnits = distanceInMeters;
      /* non geodetic approach*/
      if(geoCalculator == null) {
        distanceInCrsUnits = toLinearMeterConverter.convert(distanceInMeters);

        // Convert azimuth from degrees to radians for Java's Math functions
        double azimuthInRadians = Math.toRadians(azimuthInDegrees);
        // Calculate deltas using standard GIS trigonometry
        double deltaX = distanceInCrsUnits * Math.sin(azimuthInRadians);
        double deltaY = distanceInCrsUnits * Math.cos(azimuthInRadians);
        return new Position2D(start.x + deltaX, start.y + deltaY);
      }

      geoCalculator.setStartingGeographicPoint(start.x, start.y);
      geoCalculator.setDirection(azimuthInDegrees, distanceInMeters);    
      return geoCalculator.getDestinationPosition();
    }catch (Exception e) {
      LOGGER.severe(e.getMessage());
      throw new PlanItRunTimeException("Unable to create a position in the desired direction", e);
    }
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
   *  Collect the azimuth heading between the two coordinates (lat/long) in decimal degrees
   *
   *@param c1X first Coordinate x
   *@param c1Y first Coordinate y
   *@param c2X second Coordinate x
   *@param c2Y second Coordinate y
   *@param zeroTo360 when true, return azimuth in zero to 360 degrees instead of 0-180,0-180 degrees
   *@return azimuth in degrees
   */
  public double getAzimuthInDegrees(double c1X, double c1Y, double c2X, double c2Y, boolean zeroTo360) {
    double azimuth;
    /* non-geodetic approach (Cartesian flat plane) */
    if (geoCalculator == null) {
      double deltaX = c2X - c1X;
      double deltaY = c2Y - c1Y;

      // Math.atan2 returns radians in the range -PI to PI
      // Reversing parameters to (X, Y) aligns 0 degrees to the Y-axis (North)
      double azimuthInRadians = Math.atan2(deltaX, deltaY);
      azimuth = Math.toDegrees(azimuthInRadians);

    } else {
      geoCalculator.setStartingGeographicPoint(c1X, c1Y);
      geoCalculator.setDestinationGeographicPoint(c2X, c2Y);
      azimuth = geoCalculator.getAzimuth();
    }

    // Normalize the output based on the zeroTo360 flag
    if (zeroTo360) {
      if (azimuth < 0) {
        azimuth += 360.0;
      }
    } else {
      // Enforce the standard -180 to 180 range
      if (azimuth > 180.0) {
        azimuth -= 360.0;
      } else if (azimuth <= -180.0) {
        azimuth += 360.0;
      }
    }
    return azimuth;
  }

  /**
   *  Collect the azimuth heading between the two coordinates (lat/long) in decimal degrees
   *  
   *@param coordinate1 first Coordinate
   *@param coordinate2 second Coordinate
   *@param zeroTo360 when true, return azimuth in zero to 360 degrees instead of 0-180,0-180 degrees
   *@return azimuth in degrees
   */
  public double getAzimuthInDegrees(Coordinate coordinate1, Coordinate coordinate2, boolean zeroTo360) {
    return getAzimuthInDegrees(
        coordinate1.getX(), coordinate1.getY(), coordinate2.getX(), coordinate2.getY(), zeroTo360);
  }

  /**
   *  Collect the azimuth heading between the two positions (in user CRS) in decimal degrees,
   *  from location 1 to location 2
   *
   *@param position1 first Coordinate
   *@param position2 second Coordinate
   *@param zeroTo360 when true, return azimuth in zero to 360 degrees instead of 0-180,0-180 degrees
   *@return azimuth in degrees
   */
  public Double getAzimuthInDegrees(Position position1, Position position2, boolean zeroTo360){
    return getAzimuthInDegrees(
        position1.getCoordinate()[0], position1.getCoordinate()[1],
        position2.getCoordinate()[0], position2.getCoordinate()[1],
        zeroTo360);
  }

  /** extract direct position from coordinate
   *
   * @param coordinate to extract from
   * @return coordinate + CRS as direct position
   */
  public Position toDirectPosition(Coordinate coordinate){
    try {
      return JTS.toDirectPosition(coordinate, getCoordinateReferenceSystem());
    } catch (Exception e){
      throw new PlanItRunTimeException(e);
    }
  }

  /** extract direct position from point
   *
   * @param point to extract from
   * @return coordinate + CRS as direct position
   */
  public Position toDirectPosition(Point point){
    return toDirectPosition(point.getCoordinate());
  }

  /** Verify if the provided geometry resides left of the line defined from coordA to coordB.
   * If the geometry is not a point, we first find the closest
   * location on the geometry to coordinate B and use that as a reference instead.
   * 
   * @param geometry to check
   * @param coordA of line 
   * @param coordB of line
   * @return true when left, false otherwise
   */
  public boolean isGeometryLeftOf(Geometry geometry, Coordinate coordA, Coordinate coordB) {
    if(geometry == null) {
      throw new PlanItRunTimeException("geometry null, unable to determine on which side of line AB (%s, %s) " +
              "is resides", coordA.toString(), coordB.toString());
    }

    Coordinate referenceCoordinate = null;
    if(geometry instanceof Point) {
      referenceCoordinate = geometry.getCoordinate();
    }else {
      /* find projected coordinate closest to coordB */
      referenceCoordinate = getClosestProjectedCoordinateOnGeometry(coordB,  geometry);
    }    
    return PlanitJtsUtils.isCoordinateLeftOf(referenceCoordinate, coordA, coordB);
  }
  
  /** Verify if any existing coordinate on the passed in geometry is within the maximum provided distance
   * of the also provided envelope
   * 
   * @param geometry to check
   * @param envelope to consider
   * @param maxDistanceMeters to consider
   * @return true when within maximum distance of bounding box, false otherwise
   */
  public boolean isGeometryNearEnvelope(Geometry geometry, Envelope envelope, double maxDistanceMeters){
    Polygon boundingBoxGeometry = PlanitJtsUtils.create2DPolygon(envelope);
    double distanceMeters  = Double.POSITIVE_INFINITY;
    if(geometry instanceof Point) {
      distanceMeters =
              getClosestDistanceInMetersToPolygon(geometry.getCoordinate(), boundingBoxGeometry);
    }else if(geometry instanceof LineString) {      
      Coordinate closestCoordinate =
              getClosestExistingLineStringCoordinateToGeometry(boundingBoxGeometry, (LineString) geometry);
      distanceMeters = getClosestDistanceInMetersToPolygon(closestCoordinate, boundingBoxGeometry);
    }else if( geometry instanceof Polygon) {
      Coordinate closestCoordinate =
              getClosestExistingPolygonCoordinateToGeometry(boundingBoxGeometry, (Polygon) geometry);
      distanceMeters = getClosestDistanceInMeters(closestCoordinate, boundingBoxGeometry);
    }else {
      throw new PlanItRunTimeException("Unsupported geometry type provided when checking if it is near bounding box");
    }
    return Precision.smallerEqual(distanceMeters, maxDistanceMeters, Precision.EPSILON_6);
  }

  /** Verify if any existing coordinate on the passed in geometry1 is within the maximum provided distance of the other
   * geometry.
   *
   * @param geometry1 to check
   * @param geometry2 to check
   * @param maxDistanceMeters to consider
   * @return true when within maximum distance of bounding box, false otherwise
   */
  public boolean isGeometryNearGeometry(Geometry geometry1,  Geometry geometry2, double maxDistanceMeters){
    /* if any of the coordinates of geometry 1 is within range of geometry2 we return true */
    return Arrays.stream(geometry1.getCoordinates()).anyMatch(c ->
        Precision.smallerEqual(
            getClosestDistanceInMeters(c, geometry2), maxDistanceMeters, Precision.EPSILON_6));
  }

}
