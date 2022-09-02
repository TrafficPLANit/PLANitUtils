package org.goplanit.utils.geo;

import org.goplanit.utils.graph.Edge;
import org.goplanit.utils.graph.Vertex;
import org.goplanit.utils.misc.Pair;
import org.goplanit.utils.zoning.Zone;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;

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
}
