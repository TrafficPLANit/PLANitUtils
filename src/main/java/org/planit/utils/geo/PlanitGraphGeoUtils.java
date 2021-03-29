package org.planit.utils.geo;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Logger;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.index.quadtree.Quadtree;
import org.planit.utils.exceptions.PlanItException;
import org.planit.utils.graph.Edge;
import org.planit.utils.graph.Edges;
import org.planit.utils.graph.Vertex;
import org.planit.utils.math.Precision;
import org.planit.utils.misc.Pair;
import org.planit.utils.zoning.Zone;

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
  
  /** find the minimum value pair from this map
   * @param <T> planit entity type
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
  
  /** remove all entities that fall outside provided maxDistance
   * @param <T> planit entity
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
  
  
  /** find the closest distance to the reference point and the geometry for some planit entity with a supported geometry from the provided collection.
   * This method computes the distance between any location on any line segment of the (outer) boundary
   * of the planit entities geometry (or its point location if no polygon/linestring is available) and the reference coordinate and it is therefore very precise. 
   * A cap is placed on how far an entity is allowed to be to still be regarded as closest via maxDistanceMeters.
   * 
   * @param coordinate reference location to find distance to
   * @param planitEntities to check against using their geometries
   * @param maxDistanceMeters maximum allowedDistance to be eligible
   * @param geoUtils to compute projected distances
   * @return planitEntity closest and distance in meters, null if none matches criteria
   * @throws PlanItException thrown if error
   */  
  protected static <T> Double findPlanitEntityDistance(Point referencePoint, T planitEntity, PlanitJtsUtils geoUtils) throws PlanItException {
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
  
  /** find the distances to the reference point and the geometry for all given planit entities with a supported geometry from the provided collection.
   * This method computes the distance between any location on any line segment of the (outer) boundary
   * of the planit entities geometry (or its point location if no polygon/linestring is available) and the reference coordinate and it is therefore very precise. 
   * 
   * @param coordinate reference location to find distance to
   * @param planitEntities to check against using their geometries
   * @param geoUtils to compute projected distances
   * @return planitEntity closest and distance in meters, null if none matches criteria
   * @throws PlanItException thrown if error
   */  
  protected static <T> Map<T,Double> findPlanitEntitiesDistance(Point referencePoint, Collection<? extends T> planitEntities, PlanitJtsUtils geoUtils) throws PlanItException {
    Map<T,Double> distanceMap = new TreeMap<T,Double>();
    for(T entity : planitEntities) {
      distanceMap.put(entity, findPlanitEntityDistance(referencePoint, entity, geoUtils));
    }
    return distanceMap;    
  } 
  
  /** find the distances to the line string and the geometry for all given planit entities with a supported geometry from the provided collection.
   * This method computes the distance between any location on any line segment of the (outer) boundary
   * of the planit entities geometry (or its point location if no polygon/linestring is available) and the reference coordinate and it is therefore very precise. 
   * 
   * @param coordinate reference location to find distance to
   * @param planitEntities to check against using their geometries
   * @param geoUtils to compute projected distances
   * @return planitEntity closest and distance in meters, null if none matches criteria
   * @throws PlanItException thrown if error
   */   
  protected static <T> Map<T,Double> findPlanitEntitiesDistance(LineString lineString, Collection<? extends T> planitEntities, PlanitJtsUtils geoUtils) throws PlanItException {
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
  
  /** find all planit entities within striking distance of the line string from the available entities based on the max distance provided. 
   * This method computes the actual distance between any location on any line segment of the geometry of the entity and any existing coordinate on 
   * the line string and it is therefore is very precise. A cap is placed on how far an entities geometry is allowed to be away to still be regarded as closest via maxDistanceMeters.
   * 
   * @param lineString reference line string
   * @param planitEntities to check against using their geometries
   * @param maxDistanceMeters maximum allowedDistance to be eligible
   * @param geoUtils to compute projected distances
   * @return closest and its distance, null if none matches criteria
   * @throws PlanItException thrown if error
   */  
  protected static <T> Set<? extends T> findPlanitEntitiesWithinDistance(LineString lineString, Collection<? extends T> planitEntities, Double maxDistanceMeters, PlanitJtsUtils geoUtils) throws PlanItException { 
    /* collect distances */
    Map<? extends T, Double> result = findPlanitEntitiesDistance(lineString, planitEntities, geoUtils);    
    /* remove entries beyond distance */
    removePlanitEntitiesBeyondValue(result, maxDistanceMeters);
    
    return result.keySet();
  } 
  
  /** find all planit entities within striking distance of the line string from the available entities based on the max distance provided. 
   * This method computes the actual distance between any location on any line segment of the geometry of the entity and any existing coordinate on 
   * the line string and it is therefore is very precise. A cap is placed on how far an entities geometry is allowed to be away to still be regarded as closest via maxDistanceMeters.
   * 
   * @param lineString reference line string
   * @param planitEntities to check against using their geometries
   * @param maxDistanceMeters maximum allowedDistance to be eligible
   * @param geoUtils to compute projected distances
   * @return closest and its distance, null if none matches criteria
   * @throws PlanItException thrown if error
   */  
  protected static <T> Set<? extends T> findPlanitEntitiesWithinDistance(Point referencePoint, Collection<? extends T> planitEntities, Double maxDistanceMeters, PlanitJtsUtils geoUtils) throws PlanItException {
    /* collect distances */
    Map<? extends T, Double> result = findPlanitEntitiesDistance(referencePoint, planitEntities, geoUtils);    
    /* remove entries beyond distance */
    removePlanitEntitiesBeyondValue(result, maxDistanceMeters);
    
    return result.keySet();
  }    
  
  /** find the closest distance to the reference point and the geometry for some planit entity with a supported geometry from the provided collection.
   * This method computes the distance between any location on any line segment of the (outer) boundary
   * of the planit entities geometry (or its point location if no polygon/linestring is available) and the reference coordinate and it is therefore very precise. 
   * A cap is placed on how far an entity is allowed to be to still be regarded as closest via maxDistanceMeters.
   * 
   * @param coordinate reference location to find distance to
   * @param planitEntities to check against using their geometries
   * @param maxDistanceMeters maximum allowedDistance to be eligible
   * @param geoUtils to compute projected distances
   * @return planitEntity closest and distance in meters, null if none matches criteria
   * @throws PlanItException thrown if error
   */  
  @SuppressWarnings("unchecked")
  protected static <T> Pair<T, Double> findPlanitEntityClosest(Point referencePoint, Collection<? extends T> planitEntities, double maxDistanceMeters, PlanitJtsUtils geoUtils) throws PlanItException {
    /* collect distances */
    Map<? extends T, Double> result = findPlanitEntitiesDistance(referencePoint, planitEntities, geoUtils);
    /* find minimum entry */
    return findMinimumValuePair(result);               
  }
  

  /** find the closest planit entity to the line string from the available entities. This method computes the actual distance between any location on any line segment of the 
   * geometry of the entity and any existing coordinate on the line string and it is therefore is very precise.
   * A cap is placed on how far an entities geometry is allowed to be away to still be regarded as closest via maxDistanceMeters.
   * 
   * @param lineString reference line string
   * @param planitEntities to check against using their geometries
   * @param maxDistanceMeters maximum allowedDistance to be eligible
   * @param geoUtils to compute projected distances
   * @return closest and its distance, null if none matches criteria
   * @throws PlanItException thrown if error
   */
  protected static <T> Pair<T,Double> findPlanitEntityClosest(LineString lineString, final Collection<? extends T> planitEntities, double maxDistanceMeters, final PlanitJtsUtils geoUtils) throws PlanItException {
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
   * @throws PlanItException thrown if error
   */  
  public static Edge findEdgeClosest(Geometry geometry, Collection<? extends Edge> edges, PlanitJtsUtils geoUtils) throws PlanItException {
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
   * @throws PlanItException thrown if error
   */  
  public static Pair<? extends Edge,Set<? extends Edge>> findEdgesClosest(Geometry geometry, Collection<? extends Edge> edges, double marginToClosestMeters, PlanitJtsUtils geoUtils) throws PlanItException {
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
    
  /**
   * created quadtree based on edge envelopes as spatial index. Requires PlanitJtsIntersectEdgeVisitor to filter out true spatial matches when querying.
   * 
   *  @param edgesCollection collections to add
   */
  public static <T extends Edge> Quadtree createSpatiallyIndexedPlanitEdges(Collection<Edges<T>> edgesCollection) {
    Quadtree spatiallyIndexedEdges = new Quadtree();
    for(Edges<T> edges : edgesCollection) {
      edges.forEach(edge -> spatiallyIndexedEdges.insert(edge.getGeometry().getEnvelope().getEnvelopeInternal(),edge));
    }
    return spatiallyIndexedEdges;
  }   
  
  /** Find the edge closest to the passed in line string using a projection from any existing coordinate on the line string to the geometry of the link.
   * 
   * @param lineString to find closest link for
   * @param edges to check against
   * @param geoUtils used to compute distances
   * @return closest edge found
   * @throws PlanItException thrown if error
   */   
  public static Edge findEdgeClosestToLineString(LineString lineString, Collection<? extends Edge> edges, PlanitJtsUtils geoUtils) throws PlanItException {    
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
  public static Edge findEdgeClosestToPoint(Point point, Collection<? extends Edge> edges, PlanitJtsUtils geoUtils) throws PlanItException {
    Pair<? extends Edge,Set<? extends Edge>> result = findEdgesClosestToPoint(point, edges, 0, geoUtils);
    return result!=null ? result.first() : null;
  }
    
  
  /** Find the edge closest to the passed in line string using a projection from any existing coordinate on the line string to the geometry of the link. All edges that are
   * found within a small buffer distance of the closest distance are also retrieved
   * 
   * @param lineString to find closest link for
   * @param edges to check against
   * @param bufferDistanceMeters margin used to collect all edges with distance <= (distance to closest edge + this margin)
   * @param geoUtils used to compute distances
   * @return closest edge found and edges within margin
   * @throws PlanItException thrown if error
   */   
  public static Pair<? extends Edge,Set<? extends Edge>> findEdgesClosestToLineString(LineString lineString, Collection<? extends Edge> edges, double bufferDistanceMeters, PlanitJtsUtils geoUtils) throws PlanItException {
    return findEdgesClosestToGeometry(lineString, edges, bufferDistanceMeters, geoUtils);   
  }   
  
  /** Find the edge closest to the passed in point as well as all other edges within the given margin 
   * using a projection from any existing coordinate on the geometry to the geometry of the link. All edges that are
   * found within a small buffer distance of the closest distance are also retrieved
   * 
   * @param point to find closest link for
   * @param edges to check against
   * @param bufferDistanceMeters margin used to collect all edges with distance <= (distance to closest edge + this margin)
   * @param geoUtils used to compute distances
   * @return closest edge found and all other edges within the given margin
   * @throws PlanItException thrown if error
   */    
  public static Pair<? extends Edge,Set<? extends Edge>> findEdgesClosestToPoint(Point point, Collection<? extends Edge> edges, double bufferDistanceMeters, PlanitJtsUtils geoUtils) throws PlanItException {
   return findEdgesClosestToGeometry(point, edges, bufferDistanceMeters, geoUtils); 
  }   
  
  /** Find the edge closest to the passed in geometry as well as all other edges within the given margin 
   * using a projection from any existing coordinate on the geometry to the geometry of the link. All edges that are
   * found within a small buffer distance of the closest distance are also retrieved
   * 
   * @param point to find closest link for
   * @param edges to check against
   * @param bufferDistanceMeters margin used to collect all edges with distance <= (distance to closest edge + this margin)
   * @param geoUtils used to compute distances
   * @return closest edge found and all other edges within the given margin
   * @throws PlanItException thrown if error
   */    
  public static Pair<? extends Edge,Set<? extends Edge>> findEdgesClosestToGeometry(Geometry geometry, Collection<? extends Edge> edges, double bufferDistanceMeters, PlanitJtsUtils geoUtils) throws PlanItException {
    /* collect entity distances */
    Map<Edge, Double> result = null;
    if(geometry instanceof Point) {
      result = findPlanitEntitiesDistance((Point)geometry, edges, geoUtils);
    }else if(geometry instanceof LineString) {
      result = findPlanitEntitiesDistance((LineString)geometry, edges, geoUtils);
    }else if(geometry instanceof Polygon) {
      result = findPlanitEntitiesDistance((LineString)((Polygon)geometry).getExteriorRing(), edges, geoUtils);
    }else {
      throw new PlanItException("Unsupported geometry encountered when finding edges closest to geometry");
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
   * @param searchBoundingBox to use
   * @return links found intersecting or within bounding box provided
   */
  public static <T extends Edge> Collection<T> findEdgesSpatially(Envelope searchBoundingBox, Quadtree spatiallyIndexedEdgeTree) {
    PlanitJtsIntersectEdgeVisitor<T> edgevisitor = new PlanitJtsIntersectEdgeVisitor<T>(PlanitJtsUtils.create2DPolygon(searchBoundingBox), new HashSet<T>());
    spatiallyIndexedEdgeTree.query(searchBoundingBox, edgevisitor);
    return edgevisitor.getResult();
  }  
  
  /** Verify if node is within maximum distance of provided bounding box
   * @param node the node
   * @param boundingBox the bounding box
   * @param maxDistanceMeters maximum distance between node and nounding box
   * @param geoUtils to use
   * @return true when within given distance, false otherwise
   * @throws PlanItException thrown if error
   */
  public static boolean isVertexNearBoundingBox(Vertex node, Envelope boundingBox, double maxDistanceMeters, PlanitJtsUtils geoUtils) throws PlanItException {
    return geoUtils.isGeometryNearBoundingBox(node.getPosition(), boundingBox, maxDistanceMeters);
  }  
}
