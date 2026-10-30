package org.goplanit.utils.geo;

import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.goplanit.utils.exceptions.PlanItException;
import org.goplanit.utils.exceptions.PlanItRunTimeException;
import org.goplanit.utils.graph.Edge;
import org.goplanit.utils.graph.Vertex;
import org.goplanit.utils.graph.directed.EdgeSegment;
import org.goplanit.utils.id.IdAble;
import org.goplanit.utils.math.Precision;
import org.goplanit.utils.misc.Pair;
import org.goplanit.utils.zoning.Zone;
import org.locationtech.jts.algorithm.ConvexHull;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineSegment;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.linearref.LinearLocation;

/**
 * A class with utility methods that leverage the geospatial information of PLANit enities pertaining to graphs.
 * For geo utilities only utilising JTS entities, we refer the user to PlanitJtsUtils instead. this class is dedicated to
 * functionality that directly requires the involvement of Planit Graph elements (vertices, edges, etc.).
 * 
 * @author markr
 *
 */
public class PlanitGraphGeoUtils {
  
  /** the logger */
  static final Logger LOGGER = Logger.getLogger(PlanitGraphGeoUtils.class.getCanonicalName());
  
  /** Find the minimum value pair from this map
   * 
   * @param <T> PLANit entity type
   * @param valueMap to check
   * @return minimum value pair
   */
  protected static <T>  Pair<T, Double> findMinimumValuePair(Map<? extends T, Double> valueMap) {
    var minEntry = valueMap.entrySet().stream().min(Entry.comparingByValue()).get();
    return Pair.of(minEntry.getKey(),minEntry.getValue());
  }  
  
  /** Remove all entities that fall outside provided maxDistance
   * 
   * @param <T> PLANit entity
   * @param entitiesToFilter with values , this map is filtered
   * @param maxValue to filter on
   */
  protected static <T> void removePlanitEntitiesBeyondValue(
          Map<? extends T, Double> entitiesToFilter, Double maxValue) {

    Iterator<?> iterator = entitiesToFilter.entrySet().iterator();
    while(iterator.hasNext()) {
      @SuppressWarnings("unchecked") 
      Map.Entry<? extends T, Double> entry = (Map.Entry<? extends T, Double>)iterator.next();
      if( (entry.getValue() - Precision.EPSILON_6) > maxValue) {
        iterator.remove();
      }
    }
  }  
  
  
  /** Find the closest distance to the reference point and the geometry for some PLANit entity with a supported geometry from the provided collection.
   * This method computes the distance between any location on any line segment of the (outer) boundary
   * of the PLANit entities geometry (or its point location if no polygon/linestring is available) and the reference coordinate and it is therefore very precise. 
   * A cap is placed on how far an entity is allowed to be to still be regarded as closest via maxDistanceMeters.
   * 
   * @param <T> PLANit entity type
   * @param reference reference location to find distance to
   * @param planitEntity to check against using their geometries
   * @param geoUtils to compute projected distances
   * @return planitEntity closest and distance in meters, null if none matches criteria
   */  
  protected static <T> Double findPlanitEntityDistance(
      Coordinate reference, T planitEntity, PlanitJtsCrsUtils geoUtils){

    if(planitEntity instanceof Zone) {
      Coordinate closestCoordinate =
              geoUtils.getClosestProjectedCoordinateOnGeometry(reference,  ((Zone)planitEntity).getGeometry());
      return geoUtils.getDistanceInMetres(reference, closestCoordinate);
    }else if(planitEntity instanceof Edge) {
      return geoUtils.getClosestProjectedDistanceInMetersToLineString(reference, ((Edge)planitEntity).getGeometry());
    }else {
      LOGGER.warning(String.format("Unsupported planit entity to compute closest distance to %s",
              planitEntity.getClass().getCanonicalName()));
    }      
    return null;

  }  
  
  /** Find the distances to the reference point and the geometry for all given PLANit entities with a supported geometry from the provided collection.
   * This method computes the distance between any location on any line segment of the (outer) boundary
   * of the PLANit entities geometry (or its point location if no polygon/linestring is available) and the reference coordinate and it is therefore very precise. 
   * 
   * @param <T> PLANit entity type
   * @param reference reference location to find distance to
   * @param planitEntities to check against using their geometries
   * @param geoUtils to compute projected distances
   * @return planitEntity closest and distance in meters, null if none matches criteria
   */  
  protected static <T extends IdAble> Map<T,Double> findPlanitEntitiesDistance(
      Coordinate reference, Collection<? extends T> planitEntities, PlanitJtsCrsUtils geoUtils){
    var distanceMap = new TreeMap<T,Double>();
    for(T entity : planitEntities) {
      distanceMap.put(entity, findPlanitEntityDistance(reference, entity, geoUtils));
    }
    return distanceMap;    
  } 
  
  /** Find the distances to the line string and the geometry for all given PLANit entities with a supported geometry from the provided collection.
   * This method computes the distance between any location on any line segment of the (outer) boundary
   * of the PLANit entities geometry (or its point location if no polygon/linestring is available) and the reference coordinate and it is therefore very precise. 
   * 
   * @param <T> PLANit entity type
   * @param lineString reference to find distance to
   * @param planitEntities to check against using their geometries
   * @param geoUtils to compute projected distances
   * @return planitEntity closest and distance in meters, null if none matches criteria
   */   
  protected static <T extends IdAble> Map<T,Double> findPlanitEntitiesDistance(
      LineString lineString, Collection<? extends T> planitEntities, PlanitJtsCrsUtils geoUtils){
    Map<T,Double> distanceMap = null;     
    int numCoordinates = lineString.getCoordinates().length;
    for(int index=0; index<numCoordinates; ++index) {
      Coordinate coordinate = lineString.getCoordinateN(index);      
      Map<T,Double> coordinateDistanceMap = findPlanitEntitiesDistance(coordinate,planitEntities, geoUtils);
      if(distanceMap==null) {
        distanceMap = coordinateDistanceMap;
      }else {
        /* update if closer */
        for(Entry<T, Double> entry : coordinateDistanceMap.entrySet()) {
          if(!distanceMap.containsKey(entry.getKey()) || distanceMap.get(entry.getKey()) > entry.getValue()) {
            distanceMap.put(entry.getKey(), entry.getValue());
          }
        }
      }
    }
    return distanceMap;
  }   
  
  /** Find all PLANit entities within striking distance of the line string from the available entities based on the
   * max distance provided.
   * This method computes the actual distance between any location on any line segment of the geometry of the
   * entity and any existing coordinate on
   * the line string and it is therefore is very precise. A cap is placed on how far an entities geometry is
   * allowed to be away to still be regarded as closest via maxDistanceMeters.
   * 
   * @param <T> PLANit entity type
   * @param lineString reference line string
   * @param planitEntities to check against using their geometries
   * @param maxDistanceMeters maximum allowedDistance to be eligible
   * @param geoUtils to compute projected distances
   * @return closest and its distance, null if none matches criteria
   */  
  protected static <T extends IdAble> Set<? extends T> findPlanitEntitiesWithinDistance(
      LineString lineString, Collection<? extends T> planitEntities,
          Double maxDistanceMeters,
          PlanitJtsCrsUtils geoUtils) {

    /* collect distances */
    Map<? extends T, Double> result = findPlanitEntitiesDistance(lineString, planitEntities, geoUtils);    
    /* remove entries beyond distance */
    removePlanitEntitiesBeyondValue(result, maxDistanceMeters);
    
    return result.keySet();
  } 
  
  /** Find all PLANit entities within striking distance of the line string from the available entities based on the
   * max distance provided. This method computes the actual distance between any location on any line segment of
   * the geometry of the entity and any existing coordinate on the line string and it is therefore is very precise.
   * A cap is placed on how far an entities geometry is allowed to be away to still be regarded as closest
   * via maxDistanceMeters.
   * 
   * @param <T> PLANit entity type
   * @param reference reference location to use
   * @param planitEntities to check against using their geometries
   * @param maxDistanceMeters maximum allowedDistance to be eligible
   * @param geoUtils to compute projected distances
   * @return closest and its distance, null if none matches criteria
   */  
  protected static <T extends IdAble> Set<? extends T> findPlanitEntitiesWithinDistance(
      Coordinate reference, Collection<? extends T> planitEntities,
          Double maxDistanceMeters, PlanitJtsCrsUtils geoUtils) {
    /* collect distances */
    Map<? extends T, Double> result = findPlanitEntitiesDistance(reference, planitEntities, geoUtils);
    /* remove entries beyond distance */
    removePlanitEntitiesBeyondValue(result, maxDistanceMeters);
    
    return result.keySet();
  }    
  
  /** Find the closest distance to the reference point and the geometry for some PLANit entity with a supported geometry from the provided collection.
   * This method computes the distance between any location on any line segment of the (outer) boundary
   * of the PLANit entities geometry (or its point location if no polygon/linestring is available) and the reference coordinate and it is therefore very precise. 
   * A cap is placed on how far an entity is allowed to be to still be regarded as closest via maxDistanceMeters.
   * 
   * @param <T> PLANit entity type
   * @param reference reference location to find distance to
   * @param planitEntities to check against using their geometries
   * @param maxDistanceMeters maximum allowedDistance to be eligible
   * @param geoUtils to compute projected distances
   * @return planitEntity closest and distance in meters, null if none matches criteria
   * @throws PlanItException thrown if error
   */  
  protected static <T extends IdAble> Pair<T, Double> findPlanitEntityClosest(
      Coordinate reference, Collection<? extends T> planitEntities, double maxDistanceMeters,
          PlanitJtsCrsUtils geoUtils) throws PlanItException {

    /* collect distances */
    Map<? extends T, Double> result = findPlanitEntitiesDistance(reference, planitEntities, geoUtils);
    /* find minimum entry */
    return findMinimumValuePair(result);               
  }
  

  /** Find the closest PLANit entity to the line string from the available entities. This method computes the
   * actual distance between any location on any line segment of the geometry of the entity and any
   * existing coordinate on the line string and it is therefore is very precise. A cap is placed on how far
   * an entities geometry is allowed to be away to still be regarded as closest via maxDistanceMeters.
   * 
   * 
   * @param <T> PLANit entity type
   * @param lineString reference line string
   * @param planitEntities to check against using their geometries
   * @param maxDistanceMeters maximum allowedDistance to be eligible
   * @param geoUtils to compute projected distances
   * @return closest and its distance, null if none matches criteria
   */
  protected static <T extends IdAble> Pair<T,Double> findPlanitEntityClosest(
      LineString lineString, final Collection<? extends T> planitEntities,
          double maxDistanceMeters, final PlanitJtsCrsUtils geoUtils) {
    /* collect distances */
    Map<? extends T, Double> result = findPlanitEntitiesDistance(lineString, planitEntities, geoUtils);
    
    /* find minimum entry */
    return findMinimumValuePair(result);
  }

  /**
   * Find the index of the vertex closest to the coordinate provided
   *
   * @param coordinate to check against
   * @param geoUtils to use
   * @param vertices to find closest one for
   * @return index of closest vertex, -1 if something went wrong
   */
  public static int findVertexClosestTo(Coordinate coordinate, PlanitJtsCrsUtils geoUtils, Vertex... vertices) {
    int closestIndex = -1;
    double distance = Double.POSITIVE_INFINITY;
    for(int index = 0; index < vertices.length ; ++index) {
      var currVertex = vertices[index];
      if(currVertex.hasPosition()) {
        double currDistance = geoUtils.getDistanceInMetres(coordinate, vertices[index].getPosition().getCoordinate());
        if(currDistance < distance){
          distance = currDistance;
          closestIndex = index;
        }
      }
    }
    return closestIndex;
  }
   

  /** Find the edge closest to a geometry, see
   * {@link #findEdgesClosest(Geometry, Collection, double, PlanitJtsCrsUtils)}
   *
   * @param geometry to find the closest edge for: a point, line string or polygon
   * @param edges to check against
   * @param geoUtils used to compute distances
   * @return closest edge found, null when the geometry or edges are missing, or the geometry is of another kind
   */
  public static Edge findEdgeClosest(Geometry geometry, Collection<? extends Edge> edges, PlanitJtsCrsUtils geoUtils){
    Pair<? extends Edge,Set<? extends Edge>> result = findEdgesClosest(geometry, edges,0,geoUtils);
    return result!=null ? result.first() : null;   
  }
  
  /** Find the edge closest to a geometry, and the other edges within a margin beyond its distance. Distances are
   * measured from each existing coordinate of the geometry, of its ring for a polygon, to the geometry of each edge,
   * see {@link #findEdgesWithinClosestDistanceDeltaToGeometry(Geometry, Collection, double, PlanitJtsCrsUtils)}. A
   * single edge is the closest without measuring
   *
   * @param geometry to find the closest edges for: a point, line string or polygon
   * @param edges to check against
   * @param marginToClosestMeters beyond the distance of the closest edge, within which the other edges are collected
   * @param geoUtils used to compute distances
   * @return the closest edge with the other edges within the margin, without them (null) when only one edge is given.
   *         Null when the geometry or edges are missing, or the geometry is not a point, line string or polygon
   */
  public static Pair<? extends Edge,Set<? extends Edge>> findEdgesClosest(
      Geometry geometry, Collection<? extends Edge> edges, double marginToClosestMeters, PlanitJtsCrsUtils geoUtils){
    if(geometry==null || edges==null || geoUtils==null) {
      return null;
    }
    
    if(edges.size()==1) {
      return Pair.of(edges.iterator().next(),null);
    }

    if(!(geometry instanceof Point || geometry instanceof LineString || geometry instanceof Polygon)) {
      return null;
    }

    var result = findEdgesWithinClosestDistanceDeltaToGeometry(geometry, edges, marginToClosestMeters, geoUtils);

    /* remove minimum entry as it is returned separately */
    var minEntry = findMinimumValuePair(result);
    result.remove(minEntry.first());

    return Pair.of(minEntry.first(), new TreeSet<Edge>(result.keySet()));
  }

  /** Find the edges within the given buffer distance (around shortest distance found) to the passed in geometry using a projection from any existing coordinate on the geometry to the geometry of the link. All edges that are
   * found within a small buffer distance of the closest distance are also retrieved
   *
   * @param <T> type of edge
   * @param geometry to find closest link for
   * @param edges to check against
   * @param bufferDistanceMeters margin used to collect all edges with distance smaller or equal to buffer (distance to closest edge + this margin)
   * @param geoUtils used to compute distances
   * @return edges found with their distances to the geometry, can be null if none match
   */
  public static <T extends Edge> Map<T, Double> findEdgesWithinClosestDistanceDeltaToGeometry(
      Geometry geometry, Collection<T> edges, double bufferDistanceMeters, PlanitJtsCrsUtils geoUtils){

    /* collect entity distances */
    Map<T, Double> result = null;
    if(geometry instanceof Point) {
      result = findPlanitEntitiesDistance(((Point)geometry).getCoordinate(), edges, geoUtils);
    }else if(geometry instanceof LineString) {
      result = findPlanitEntitiesDistance((LineString)geometry, edges, geoUtils);
    }else if(geometry instanceof Polygon) {
      result = findPlanitEntitiesDistance((LineString)((Polygon)geometry).getExteriorRing(), edges, geoUtils);
    }else {
      throw new PlanItRunTimeException("Unsupported geometry encountered when finding edges closest to geometry");
    }

    /* find minimum entry */
    Pair<Edge, Double> minResult = findMinimumValuePair(result);
    /* filter entries beyond buffer distance */
    removePlanitEntitiesBeyondValue(result, minResult.second() + bufferDistanceMeters);

    return result;
  }

  /** Extract the JTS line segment from the edge segment that is closest to the reference geometry in its intended direction.
   * 
   * @param <T> edge segment type
   * @param referenceGeometry to determine closeness criteria on  
   * @param edgeSegment to extract line segment from
   * @param geoUtils for distance calculations
   * @return line segment if found
   */
  public static <T extends EdgeSegment> LineSegment extractClosestLineSegmentTo(
      Geometry referenceGeometry, T edgeSegment, PlanitJtsCrsUtils geoUtils) {
    
    LineString linkSegmentGeometry = edgeSegment.getParent().getGeometry();
    if(linkSegmentGeometry == null) {
      throw new PlanItRunTimeException("Geometry not available on edge segment %d (external id %s), " +
              "unable to determine closest line segment to reference geometry, this shouldn't happen",
              edgeSegment.getId(), edgeSegment.getExternalId());
    }
    
    LinearLocation linearLocation =
            geoUtils.getClosestGeometryExistingCoordinateToProjectedLinearLocationOnLineString(
                    referenceGeometry, linkSegmentGeometry);
    boolean reverseLinearLocationGeometry =
            edgeSegment.isDirectionAb()!=edgeSegment.getParent().isGeometryInAbDirection();
    
    LineSegment lineSegment = linearLocation.getSegment(edgeSegment.getParent().getGeometry());
    if(reverseLinearLocationGeometry) {
      lineSegment.reverse();
    }
    return lineSegment;        
  }  

  /**
   * Verify if a geometry lies left of an edge segment, as seen in the direction the segment runs, at the piece of its
   * edge's geometry closest to the geometry, see {@link #extractClosestLineSegmentTo(Geometry, EdgeSegment,
   * PlanitJtsCrsUtils)}. So what lies left of one direction of an edge lies right of the other. For example, a stop on
   * the kerb of a road where traffic drives on the left lies left of the segment its vehicles stop on
   *
   * @param geometry to locate, see {@link PlanitJtsCrsUtils#isGeometryLeftOf(Geometry, Coordinate, Coordinate)} for
   *                 a geometry that is not a point
   * @param edgeSegment to locate it against
   * @param geoUtils to use
   * @return true when left of the edge segment, false otherwise
   */
  public static boolean isGeometryLeftOf(Geometry geometry, EdgeSegment edgeSegment, PlanitJtsCrsUtils geoUtils) {
    var closestPiece = extractClosestLineSegmentTo(geometry, edgeSegment, geoUtils);
    return geoUtils.isGeometryLeftOf(geometry, closestPiece.p0, closestPiece.p1);
  }
  
  /** Verify if node is within maximum distance of provided bounding box
   * 
   * @param node the node
   * @param boundingBox the bounding box
   * @param maxDistanceMeters maximum distance between node and bounding box
   * @param geoUtils to use
   * @return true when within given distance, false otherwise
   */
  public static boolean isVertexNearBoundingBox(
      Vertex node, Envelope boundingBox, double maxDistanceMeters, PlanitJtsCrsUtils geoUtils) {
    return geoUtils.isGeometryNearEnvelope(node.getPosition(), boundingBox, maxDistanceMeters);
  }

  /** Create the convex hull around the given vertices, being the tightest convex outline containing all their
   * positions. To be preferred over a bounding box whenever the outline decides what falls within the area the
   * vertices cover, a box also claiming the empty corners around an area that is not itself rectangular.
   *
   * @param <V> type of vertex
   * @param vertices to create the hull around, any vertex without a position is ignored
   * @return the created hull, an empty geometry when no vertex carries a position
   */
  public static <V extends Vertex> Geometry createConvexHull(Iterable<V> vertices) {
    var coordinates = positionsOf(vertices).stream().map(Point::getCoordinate).toArray(Coordinate[]::new);
    return new ConvexHull(coordinates, PlanitJtsUtils.jtsGeometryFactory).getConvexHull();
  }

  /**
   * The positions of the given vertices
   *
   * @param <V> type of vertex
   * @param vertices to take the positions of, any vertex without a position is left out
   * @return the positions, in the order of the vertices
   */
  private static <V extends Vertex> List<Point> positionsOf(Iterable<V> vertices) {
    var positions = new ArrayList<Point>();
    for (var vertex : vertices) {
      if (vertex.hasPosition()) {
        positions.add(vertex.getPosition());
      }
    }
    return positions;
  }

  /** Create the centroid of the positions of the given vertices
   *
   * @param <V> type of vertex
   * @param vertices to create the centroid of, any vertex without a position is ignored
   * @return the centroid, an empty point when no vertex carries a position
   */
  public static <V extends Vertex> Point createCentroid(Iterable<V> vertices) {
    return PlanitJtsUtils.createMultiPoint(positionsOf(vertices).toArray(Point[]::new)).getCentroid();
  }

  /**
   * Distance from a point to the furthest of the given vertices, in metres
   *
   * @param <V> type of vertex
   * @param point to measure from
   * @param vertices to measure to, any vertex without a position is ignored
   * @param geoUtils to measure with
   * @return distance in metres, zero when no vertex carries a position
   */
  public static <V extends Vertex> double getDistanceToFurthestVertexInMetres(
      Point point, Iterable<V> vertices, PlanitJtsCrsUtils geoUtils) {
    return positionsOf(vertices).stream().mapToDouble(position -> geoUtils.getDistanceInMetres(point, position))
        .max().orElse(0);
  }

  /**
   * Distance along an edge segment's geometry from a position on it to the segment's downstream vertex, in the
   * segment's direction of travel, measured without copying the geometry. The position is matched to the first
   * coordinate of the geometry it equals, so the geometry is assumed not to pass the same coordinate twice, as a loop
   * edge starting and ending at the same vertex would
   *
   * @param segment to measure along
   * @param position a coordinate of the parent edge's geometry, e.g., one of its internal points or its upstream vertex
   * @param geoUtils to measure with
   * @return distance in metres
   */
  public static double getDistanceToDownstreamVertexInMetres(
      EdgeSegment segment, Point position, PlanitJtsCrsUtils geoUtils) {
    var geometry = segment.getParent().getGeometry();
    var index = PlanitJtsUtils.findFirstCoordinatePosition(position.getCoordinate(), geometry, Precision.EPSILON_0);
    if (index.isEmpty()) {
      throw new PlanItRunTimeException("Position (%s) is not on the geometry of edge %s, unable to measure distance",
          position.toString(), segment.getParent().getIdsAsString());
    }
    return geoUtils.getDistanceInMetres(
        geometry, index.get(), segment.isDirectionAb() ? geometry.getNumPoints() - 1 : 0);
  }

}
