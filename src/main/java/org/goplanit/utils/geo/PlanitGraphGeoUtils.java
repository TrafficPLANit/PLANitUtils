package org.goplanit.utils.geo;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Logger;

import org.goplanit.utils.exceptions.PlanItException;
import org.goplanit.utils.exceptions.PlanItRunTimeException;
import org.goplanit.utils.graph.Edge;
import org.goplanit.utils.graph.Vertex;
import org.goplanit.utils.graph.directed.EdgeSegment;
import org.goplanit.utils.math.Precision;
import org.goplanit.utils.misc.Pair;
import org.goplanit.utils.zoning.Zone;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineSegment;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.index.quadtree.Quadtree;
import org.locationtech.jts.linearref.LinearLocation;

/**
 * A class with utility methods that levarage the geospatial information of planit enities pertaining to graphs.
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
    Entry<? extends T, Double> minEntry = null;
    for( Entry<? extends T, Double> entry : valueMap.entrySet()) {
      if(minEntry==null || (minEntry.getValue() + Precision.EPSILON_6) > entry.getValue()) {
        minEntry = entry;
      }
    }
    if(minEntry==null) {
      return null;
    }
    return Pair.of(minEntry.getKey(), minEntry.getValue());
  }  
  
  /** Remove all entities that fall outside provided maxDistance
   * 
   * @param <T> PLANit entity
   * @param entitiesToFilter with values , this map is filtered
   * @param maxValue to filter on
   */
  protected static <T> void removePlanitEntitiesBeyondValue(Map<? extends T, Double> entitiesToFilter, Double maxValue) {
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
   * @param referencePoint reference location to find distance to
   * @param planitEntity to check against using their geometries
   * @param geoUtils to compute projected distances
   * @return planitEntity closest and distance in meters, null if none matches criteria
   */  
  protected static <T> Double findPlanitEntityDistance(Point referencePoint, T planitEntity, PlanitJtsCrsUtils geoUtils){
    if(planitEntity instanceof Zone) {
      Coordinate closestCoordinate = geoUtils.getClosestProjectedCoordinateOnGeometry(referencePoint,  ((Zone)planitEntity).getGeometry());
      return geoUtils.getDistanceInMetres(referencePoint.getCoordinate(), closestCoordinate);
    }else if(planitEntity instanceof Edge) {
      return geoUtils.getClosestProjectedDistanceInMetersToLineString(referencePoint, ((Edge)planitEntity).getGeometry());
    }else {
      LOGGER.warning(String.format("Unsupported planit entity to compute closest distance to %s",planitEntity.getClass().getCanonicalName()));
    }      
    return null;
  }  
  
  /** Find the distances to the reference point and the geometry for all given PLANit entities with a supported geometry from the provided collection.
   * This method computes the distance between any location on any line segment of the (outer) boundary
   * of the PLANit entities geometry (or its point location if no polygon/linestring is available) and the reference coordinate and it is therefore very precise. 
   * 
   * @param <T> PLANit entity type
   * @param referencePoint reference location to find distance to
   * @param planitEntities to check against using their geometries
   * @param geoUtils to compute projected distances
   * @return planitEntity closest and distance in meters, null if none matches criteria
   */  
  protected static <T> Map<T,Double> findPlanitEntitiesDistance(Point referencePoint, Collection<? extends T> planitEntities, PlanitJtsCrsUtils geoUtils){
    Map<T,Double> distanceMap = new TreeMap<T,Double>();
    for(T entity : planitEntities) {
      distanceMap.put(entity, findPlanitEntityDistance(referencePoint, entity, geoUtils));
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
  protected static <T> Map<T,Double> findPlanitEntitiesDistance(LineString lineString, Collection<? extends T> planitEntities, PlanitJtsCrsUtils geoUtils){
    Map<T,Double> distanceMap = null;     
    int numCoordinates = lineString.getCoordinates().length;
    for(int index=0; index<numCoordinates; ++index) {
      Coordinate coordinate = lineString.getCoordinateN(index);      
      Map<T,Double> coordinateDistanceMap = findPlanitEntitiesDistance(PlanitJtsUtils.createPoint(coordinate),planitEntities, geoUtils);
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
  
  /** Find all PLANit entities within striking distance of the line string from the available entities based on the max distance provided. 
   * This method computes the actual distance between any location on any line segment of the geometry of the entity and any existing coordinate on 
   * the line string and it is therefore is very precise. A cap is placed on how far an entities geometry is allowed to be away to still be regarded as closest via maxDistanceMeters.
   * 
   * @param <T> PLANit entity type
   * @param lineString reference line string
   * @param planitEntities to check against using their geometries
   * @param maxDistanceMeters maximum allowedDistance to be eligible
   * @param geoUtils to compute projected distances
   * @return closest and its distance, null if none matches criteria
   * @throws PlanItException thrown if error
   */  
  protected static <T> Set<? extends T> findPlanitEntitiesWithinDistance(
      LineString lineString, Collection<? extends T> planitEntities, Double maxDistanceMeters, PlanitJtsCrsUtils geoUtils) throws PlanItException { 
    /* collect distances */
    Map<? extends T, Double> result = findPlanitEntitiesDistance(lineString, planitEntities, geoUtils);    
    /* remove entries beyond distance */
    removePlanitEntitiesBeyondValue(result, maxDistanceMeters);
    
    return result.keySet();
  } 
  
  /** Find all PLANit entities within striking distance of the line string from the available entities based on the max distance provided. 
   * This method computes the actual distance between any location on any line segment of the geometry of the entity and any existing coordinate on 
   * the line string and it is therefore is very precise. A cap is placed on how far an entities geometry is allowed to be away to still be regarded as closest via maxDistanceMeters.
   * 
   * @param <T> PLANit entity type
   * @param referencePoint reference point to use
   * @param planitEntities to check against using their geometries
   * @param maxDistanceMeters maximum allowedDistance to be eligible
   * @param geoUtils to compute projected distances
   * @return closest and its distance, null if none matches criteria
   * @throws PlanItException thrown if error
   */  
  protected static <T> Set<? extends T> findPlanitEntitiesWithinDistance(Point referencePoint, Collection<? extends T> planitEntities, Double maxDistanceMeters, PlanitJtsCrsUtils geoUtils) throws PlanItException {
    /* collect distances */
    Map<? extends T, Double> result = findPlanitEntitiesDistance(referencePoint, planitEntities, geoUtils);    
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
   * @param referencePoint reference location to find distance to
   * @param planitEntities to check against using their geometries
   * @param maxDistanceMeters maximum allowedDistance to be eligible
   * @param geoUtils to compute projected distances
   * @return planitEntity closest and distance in meters, null if none matches criteria
   * @throws PlanItException thrown if error
   */  
  protected static <T> Pair<T, Double> findPlanitEntityClosest(
      Point referencePoint, Collection<? extends T> planitEntities, double maxDistanceMeters, PlanitJtsCrsUtils geoUtils) throws PlanItException {
    /* collect distances */
    Map<? extends T, Double> result = findPlanitEntitiesDistance(referencePoint, planitEntities, geoUtils);
    /* find minimum entry */
    return findMinimumValuePair(result);               
  }
  

  /** Find the closest PLANit entity to the line string from the available entities. This method computes the actual distance between any location on any line segment of the 
   * geometry of the entity and any existing coordinate on the line string and it is therefore is very precise.
   * A cap is placed on how far an entities geometry is allowed to be away to still be regarded as closest via maxDistanceMeters.
   * 
   * 
   * @param <T> PLANit entity type
   * @param lineString reference line string
   * @param planitEntities to check against using their geometries
   * @param maxDistanceMeters maximum allowedDistance to be eligible
   * @param geoUtils to compute projected distances
   * @return closest and its distance, null if none matches criteria
   * @throws PlanItException thrown if error
   */
  protected static <T> Pair<T,Double> findPlanitEntityClosest(LineString lineString, final Collection<? extends T> planitEntities, double maxDistanceMeters, final PlanitJtsCrsUtils geoUtils) throws PlanItException {
    /* collect distances */
    Map<? extends T, Double> result = findPlanitEntitiesDistance(lineString, planitEntities, geoUtils);
    
    /* find minimum entry */
    return findMinimumValuePair(result);
  }  
   

  /** Find the edge closest to the passed in geometry using a projection from any existing coordinate on the geometry to the geometry of the link.
   * 
   * @param geometry to find closest link for
   * @param edges to check against
   * @param geoUtils used to compute distances
   * @return closest edge found
   */  
  public static Edge findEdgeClosest(Geometry geometry, Collection<? extends Edge> edges, PlanitJtsCrsUtils geoUtils){
    Pair<? extends Edge,Set<? extends Edge>> result = findEdgesClosest(geometry, edges,0,geoUtils);
    return result!=null ? result.first() : null;   
  }
  
  /** Find the edge closest to the passed in geometry as well as all other edges within the provided marging 
   * using a projection from any existing coordinate on the geometry to the geometry of the link.
   * 
   * @param geometry to find closest link for
   * @param edges to check against
   * @param marginToClosestMeters margin used to collect all edges within this margin from the closest edge
   * @param geoUtils used to compute distances
   * @return closest edge found and the edges within the given margin, null if none
   */  
  public static Pair<? extends Edge,Set<? extends Edge>> findEdgesClosest(Geometry geometry, Collection<? extends Edge> edges, double marginToClosestMeters, PlanitJtsCrsUtils geoUtils){
    if(geometry==null || edges==null || geoUtils==null) {
      return null;
    }
    
    if(edges.size()==1) {
      return Pair.of(edges.iterator().next(),null);
    }
    
    if(geometry instanceof Point) {
      return findEdgesClosestToPoint((Point)geometry, edges, marginToClosestMeters, geoUtils);
    }else if(geometry instanceof LineString) {
      return findEdgesClosestToLineString((LineString)geometry, edges, marginToClosestMeters, geoUtils);     
    }
    
    return null;
  }

  /** Find the edge closest to the passed in line string using a projection from any existing coordinate on the line string to the geometry of the link.
   * 
   * @param lineString to find closest link for
   * @param edges to check against
   * @param geoUtils used to compute distances
   * @return closest edge found
   * @throws PlanItException thrown if error
   */   
  public static Edge findEdgeClosestToLineString(LineString lineString, Collection<? extends Edge> edges, PlanitJtsCrsUtils geoUtils) throws PlanItException {    
    Pair<? extends Edge,Set<? extends Edge>> result = findEdgesClosestToLineString(lineString, edges, 0, geoUtils);
    return result!=null ? result.first() : null;
  }
  
  /** Find the edge closest to the passed in point using a projection from any existing coordinate on the geometry to the geometry of the link.
   * 
   * @param point to find closest link for
   * @param edges to check against
   * @param geoUtils used to compute distances
   * @return closest edge found
   * @throws PlanItException thrown if error
   */    
  public static Edge findEdgeClosestToPoint(Point point, Collection<? extends Edge> edges, PlanitJtsCrsUtils geoUtils) throws PlanItException {
    Pair<? extends Edge,Set<? extends Edge>> result = findEdgesClosestToPoint(point, edges, 0, geoUtils);
    return result!=null ? result.first() : null;
  }
    
  
  /** Find the edge closest to the passed in line string using a projection from any existing coordinate on the line string to the geometry of the link. All edges that are
   * found within a small buffer distance of the closest distance are also retrieved
   * 
   * @param lineString to find closest link for
   * @param edges to check against
   * @param bufferDistanceMeters margin used to collect all edges with distance smaller or equal to buffer (distance to closest edge + this margin)
   * @param geoUtils used to compute distances
   * @return closest edge found and edges within margin
   */   
  public static Pair<? extends Edge,Set<? extends Edge>> findEdgesClosestToLineString(LineString lineString, Collection<? extends Edge> edges, double bufferDistanceMeters, PlanitJtsCrsUtils geoUtils){
    return findEdgesClosestToGeometry(lineString, edges, bufferDistanceMeters, geoUtils);   
  }   
  
  /** Find the edge closest to the passed in point as well as all other edges within the given margin 
   * using a projection from any existing coordinate on the geometry to the geometry of the link. All edges that are
   * found within a small buffer distance of the closest distance are also retrieved
   * 
   * @param point to find closest link for
   * @param edges to check against
   * @param bufferDistanceMeters margin used to collect all edges with distance smaller or equal to buffer (distance to closest edge + this margin)
   * @param geoUtils used to compute distances
   * @return closest edge found and all other edges within the given margin
   */    
  public static Pair<? extends Edge,Set<? extends Edge>> findEdgesClosestToPoint(Point point, Collection<? extends Edge> edges, double bufferDistanceMeters, PlanitJtsCrsUtils geoUtils){
   return findEdgesClosestToGeometry(point, edges, bufferDistanceMeters, geoUtils); 
  }   
  
  /** Find the edge closest to the passed in geometry as well as all other edges within the given margin 
   * using a projection from any existing coordinate on the geometry to the geometry of the link. All edges that are
   * found within a small buffer distance of the closest distance are also retrieved
   * 
   * @param geometry to find closest link for
   * @param edges to check against
   * @param bufferDistanceMeters margin used to collect all edges with distance smaller or equal to buffer (distance to closest edge + this margin)
   * @param geoUtils used to compute distances
   * @return closest edge found and all other edges within the given margin
   */    
  public static Pair<? extends Edge,Set<? extends Edge>> findEdgesClosestToGeometry(Geometry geometry, Collection<? extends Edge> edges, double bufferDistanceMeters, PlanitJtsCrsUtils geoUtils){
    /* collect entity distances */
    Map<Edge, Double> result = null;
    if(geometry instanceof Point) {
      result = findPlanitEntitiesDistance((Point)geometry, edges, geoUtils);
    }else if(geometry instanceof LineString) {
      result = findPlanitEntitiesDistance((LineString)geometry, edges, geoUtils);
    }else if(geometry instanceof Polygon) {
      result = findPlanitEntitiesDistance((LineString)((Polygon)geometry).getExteriorRing(), edges, geoUtils);
    }else {
      throw new PlanItRunTimeException("Unsupported geometry encountered when finding edges closest to geometry");
    }
    
    /* find minimum entry */
    Pair<Edge, Double> minResult = findMinimumValuePair(result);
    /* filter entires beyond buffer distance */
    removePlanitEntitiesBeyondValue(result, minResult.second() + bufferDistanceMeters);
    
    /* remove minimum entry as it is returned separately */
    result.remove(minResult.first());
    return Pair.of(minResult.first(), new TreeSet<Edge>(result.keySet())); 
  }   
  
  /** Find edges spatially based on the provided bounding box and spatially indexed quadtree containing edges as values
   * 
   * @param <T> type of edge
   * @param searchBoundingBox to use
   * @param spatiallyIndexedEdgeTree to consider
   * @return links found intersecting or within bounding box provided
   */
  public static <T extends Edge> Collection<T> findEdgesSpatially(Envelope searchBoundingBox, Quadtree spatiallyIndexedEdgeTree) {
    PlanitJtsIntersectEdgeVisitor<T> edgevisitor = new PlanitJtsIntersectEdgeVisitor<>(PlanitJtsUtils.create2DPolygon(searchBoundingBox), new HashSet<>());
    spatiallyIndexedEdgeTree.query(searchBoundingBox, edgevisitor);
    return edgevisitor.getResult();
  }  
  
  /** Extract the JTS line segment from the edge segment that is closest to the reference geometry in its intended direction.
   * 
   * @param <T> edge segment type
   * @param referenceGeometry to determine closeness criteria on  
   * @param edgeSegment to extract line segment from
   * @param geoUtils for distance calculations
   * @return line segment if found
   */
  public static <T extends EdgeSegment> LineSegment extractClosestLineSegmentTo(Geometry referenceGeometry, T edgeSegment, PlanitJtsCrsUtils geoUtils) {
    
    LineString linkSegmentGeometry = edgeSegment.getParent().getGeometry();
    if(linkSegmentGeometry == null) {
      throw new PlanItRunTimeException("Geometry not available on edge segment %d (external id %s), unable to determine closest line segment to reference geometry, this shouldn't happen", edgeSegment.getId(), edgeSegment.getExternalId());
    }
    
    LinearLocation linearLocation = geoUtils.getClosestGeometryExistingCoordinateToProjectedLinearLocationOnLineString(referenceGeometry, linkSegmentGeometry);
    boolean reverseLinearLocationGeometry = edgeSegment.isDirectionAb()!=edgeSegment.getParent().isGeometryInAbDirection();
    
    LineSegment lineSegment = linearLocation.getSegment(edgeSegment.getParent().getGeometry());
    if(reverseLinearLocationGeometry) {
      lineSegment.reverse();
    }
    return lineSegment;        
  }  
  
  /** Verify if node is within maximum distance of provided bounding box
   * 
   * @param node the node
   * @param boundingBox the bounding box
   * @param maxDistanceMeters maximum distance between node and bounding box
   * @param geoUtils to use
   * @return true when within given distance, false otherwise
   * @throws PlanItException thrown if error
   */
  public static boolean isVertexNearBoundingBox(Vertex node, Envelope boundingBox, double maxDistanceMeters, PlanitJtsCrsUtils geoUtils) throws PlanItException {
    return geoUtils.isGeometryNearBoundingBox(node.getPosition(), boundingBox, maxDistanceMeters);
  }  
}
