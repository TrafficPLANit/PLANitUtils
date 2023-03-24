package org.goplanit.utils.geo;

import org.goplanit.utils.exceptions.PlanItRunTimeException;
import org.goplanit.utils.graph.Edge;
import org.goplanit.utils.graph.Vertex;
import org.goplanit.utils.misc.Pair;
import org.goplanit.utils.network.layer.macroscopic.MacroscopicLinkSegment;
import org.goplanit.utils.zoning.TransferZone;
import org.goplanit.utils.zoning.Zone;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.linearref.LinearLocation;

import java.util.Collection;
import java.util.logging.Logger;

public class PlanitEntityGeoUtils {

  /** Logger to use */
  private static final Logger LOGGER = Logger.getLogger(PlanitEntityGeoUtils.class.getCanonicalName());

  /** find the distance from the zone to the coordinate. This method computes the actual distance between any location on any line segment of the outer boundary
   * of the zones (or its centroid if no polygon/linestring is available) and the reference point and it is therefore very precise.
   *
   * @param coord used
   * @param zone to check against using its geometry
   * @param geoUtils to compute projected distances
   * @return distance to zone, if not possible to compute positive infinity is returned
   */
  public static double getDistanceToZone(Coordinate coord, final Zone zone, final PlanitJtsCrsUtils geoUtils) {
    if(zone.hasGeometry()) {
      return geoUtils.getClosestDistanceInMeters(coord,zone.getGeometry());
    }else if(zone.getCentroid().hasPosition()) {
      return geoUtils.getDistanceInMetres(coord, zone.getCentroid().getPosition().getCoordinate());
    }else {
      LOGGER.warning(String.format("Zone has no geographic information to determine closeness to coordinate %s",coord));
    }
    return Double.POSITIVE_INFINITY;
  }

  /** find the distance from the edge to the point. This method computes the actual distance between any location on any line segment of the edge
   * and the reference node and it is therefore very precise.
   *
   * @param coord used
   * @param edge to check against using its geometry
   * @param geoUtils to compute projected distances
   * @return distance to edge, if not possible to compute positive infinity is returned
   */
  public static double getDistanceToEdge(final Coordinate coord, final Edge edge, final PlanitJtsCrsUtils geoUtils) {
    if(edge.hasGeometry()) {
      return geoUtils.getClosestDistanceInMeters(coord,edge.getGeometry());
    }else {
      LOGGER.warning(String.format("Edge has no geographic information to determine closeness to reference location %s",coord));
    }
    return Double.POSITIVE_INFINITY;
  }

  /** find the closest distance to the coordinate for some PLANit entity with a supported geometry from the provided collection.
   * This method computes the actual distance between any location on any line segment of the (outer) boundary
   * of the planit entities geometry (or its point location if no polygon/linestring is available) and the reference node and it is therefore very precise.
   * A cap is placed on how far a zone is allowed to be to still be regarded as closest via maxDistanceMeters.
   *
   * @param <T> type of the PLANit entity
   * @param coord reference location
   * @param planitEntities to check against using their geometries
   * @param maxDistanceMeters maximum allowedDistance to be eligible
   * @param geoUtils to compute projected distances
   * @return planitEntity closest and distance in meters, null if none matches criteria
   */
  public static <T> Pair<T, Double> findPlanitEntityClosest(Coordinate coord, Collection<? extends T> planitEntities, double maxDistanceMeters, PlanitJtsCrsUtils geoUtils)  {
    double minDistanceMeters = Double.POSITIVE_INFINITY;
    double distanceMeters = minDistanceMeters;
    T closestEntity = null;
    for(T entity : planitEntities) {
      /* supported planit entity types */
      if(entity instanceof Zone) {
        distanceMeters = getDistanceToZone(coord, (Zone)entity, geoUtils);
      }else if(entity instanceof Edge) {
        distanceMeters = getDistanceToEdge(coord, (Edge)entity, geoUtils);
      }else if(entity instanceof Vertex) {
        distanceMeters = geoUtils.getDistanceInMetres(coord, ((Vertex) entity).getPosition().getCoordinate());
      }else {
        LOGGER.warning(String.format("unsupported PLANit entity to compute closest distance to %s",entity.getClass().getCanonicalName()));
      }

      if(distanceMeters < minDistanceMeters) {
        minDistanceMeters = distanceMeters;
        if(minDistanceMeters < maxDistanceMeters) {
          closestEntity = entity;
        }
      }
    }

    if(closestEntity!=null) {
      return Pair.of(closestEntity, minDistanceMeters);
    }
    return null;
  }

  /** Extract the closest existing linear line segment based on the closest two coordinates on the link segment geometry in its intended direction to the reference geometry provided
   *
   * @param referenceGeometry to find closest line segment to
   * @param linkSegment to extract line segment from
   * @param geoUtils for distance calculations
   * @return line segment if found
   */
  public static LineSegment extractClosestLineSegmentToGeometryFromLinkSegment(Geometry referenceGeometry, MacroscopicLinkSegment linkSegment, PlanitJtsCrsUtils geoUtils) {
    LineString linkSegmentGeometry = linkSegment.getParent().getGeometry();
    var closestLinearLoc = extractClosestProjectedLinearLocationToGeometryFromEdge(referenceGeometry, linkSegment.getParentLink(),geoUtils);
    LineSegment lineSegment = closestLinearLoc.getSegment(linkSegmentGeometry);
    if(linkSegment.isDirectionAb()!=linkSegment.getParent().isGeometryInAbDirection()) {
      lineSegment.reverse();
    }
    return lineSegment;
  }

  /** Find the linear location reflecting the closest projected location between the transfer zone and link geometries. For the transfer zone geometry we use existing coordinates
   * rather than projected ones
   *
   * @param referenceGeometry to use
   * @param accessEdge to use
   * @param geoUtils to use
   * @return closest projected linear location on link geometry
   */
  public static LinearLocation extractClosestProjectedLinearLocationToGeometryFromEdge(Geometry referenceGeometry, Edge accessEdge, PlanitJtsCrsUtils geoUtils) {

    if(referenceGeometry == null){
      throw new PlanItRunTimeException("Geometry not allowed to be null");
    }

    LinearLocation projectedLinearLocationOnLink = null;
    if(referenceGeometry instanceof Point) {
      projectedLinearLocationOnLink = geoUtils.getClosestProjectedLinearLocationOnGeometry(referenceGeometry.getCoordinate(),accessEdge.getGeometry());
    }else{
      projectedLinearLocationOnLink = geoUtils.getClosestGeometryExistingCoordinateToProjectedLinearLocationOnLineString(referenceGeometry,accessEdge.getGeometry());
    }
    return projectedLinearLocationOnLink;
  }
}
