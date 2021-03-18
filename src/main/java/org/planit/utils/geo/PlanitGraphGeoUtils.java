package org.planit.utils.geo;

import java.util.Collection;
import java.util.HashSet;
import java.util.logging.Logger;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.index.quadtree.Quadtree;
import org.planit.utils.exceptions.PlanItException;
import org.planit.utils.graph.Edge;
import org.planit.utils.graph.Edges;
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
  protected static <T> Pair<T, Double> findPlanitEntityClosest(Point referencePoint, Collection<? extends T> planitEntities, double maxDistanceMeters, PlanitJtsUtils geoUtils) throws PlanItException {
    double minDistanceMeters = Double.POSITIVE_INFINITY;
    double distanceMeters = minDistanceMeters;
    Coordinate currentClosestCoordinate = null;
    T closestEntity = null;         
    for(T entity : planitEntities) {
      /* supported planit entity types */
      if(entity instanceof Zone) {
        currentClosestCoordinate = geoUtils.getClosestProjectedCoordinateOnGeometry(referencePoint,  ((Zone)entity).getGeometry());
        distanceMeters  = geoUtils.getDistanceInMetres(referencePoint.getCoordinate(), currentClosestCoordinate);
      }else if(entity instanceof Edge) {
        distanceMeters = geoUtils.getClosestProjectedDistanceInMetersToLineString(referencePoint, ((Edge)entity).getGeometry());
      }else {
        LOGGER.warning(String.format("unsupported planit entity to compute closest distance to %s",entity.getClass().getCanonicalName()));
      }      
     
      if(distanceMeters < minDistanceMeters) {
        minDistanceMeters = distanceMeters;
        closestEntity = entity;
      }      
    }
    
    if(closestEntity!=null) {
      return Pair.of(closestEntity, minDistanceMeters);
    }
    return null;
  }
  
  /** find the closest planit entity to the line string from the available entities. This method computes the actual distance between any location on any line segment of the 
   * geometry of the entity and any existing coordinate on the line string and it is therefore is very precise.
   * A cap is placed on how far an entities geometry is allowed to be away to still be regarded as closest via maxDistanceMeters.
   * 
   * @param lineString reference line string
   * @param planitEntities to check against using their geometries
   * @param maxDistanceMeters maximum allowedDistance to be eligible
   * @param geoUtils to compute projected distances
   * @return closest, null if none matches criteria
   * @throws PlanItException thrown if error
   */
  protected static <T> T findPlanitEntityClosest(LineString lineString, final Collection<? extends T> planitEntities, double maxDistanceMeters, final PlanitJtsUtils geoUtils) throws PlanItException {
    T closestPlanitEntity = null; 
    double minDistanceMeters = Double.POSITIVE_INFINITY;
    int numCoordinates = lineString.getCoordinates().length;
    for(int index=0; index<numCoordinates; ++index) {
      Coordinate coordinate = lineString.getCoordinateN(index);
      Pair<? extends T, Double> resultPair = findPlanitEntityClosest(PlanitJtsUtils.createPoint(coordinate),planitEntities,maxDistanceMeters, geoUtils);
      if(resultPair != null && resultPair.bothNotNull()) {
        if(resultPair.second() < minDistanceMeters) {
          closestPlanitEntity = resultPair.first();
          minDistanceMeters = resultPair.second();
        }        
      }
    }
    return closestPlanitEntity;
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
    if(geometry==null || edges==null || geoUtils==null) {
      return null;
    }
    
    if(geometry instanceof Point) {
      return findEdgeClosestToPoint((Point)geometry, edges, geoUtils);
    }else if(geometry instanceof LineString) {
      return findEdgeClosestToLineString((LineString)geometry, edges, geoUtils);     
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
    return (Edge)findPlanitEntityClosest(lineString, edges, Double.POSITIVE_INFINITY, geoUtils);
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
    return findPlanitEntityClosest(point, edges, Double.POSITIVE_INFINITY, geoUtils).first();     
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
}
