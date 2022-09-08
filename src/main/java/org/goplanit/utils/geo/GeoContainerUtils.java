package org.goplanit.utils.geo;

import org.goplanit.utils.graph.Edge;
import org.goplanit.utils.graph.GraphEntities;
import org.goplanit.utils.zoning.TransferZone;
import org.goplanit.utils.zoning.TransferZones;
import org.goplanit.utils.zoning.Zone;
import org.goplanit.utils.zoning.Zones;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.index.quadtree.Quadtree;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Uitlity class to convert various PLANit entities into spatially indexed quad tree containers
 */
public class GeoContainerUtils {

  private static final Logger LOGGER = Logger.getLogger(GeoContainerUtils.class.getCanonicalName());

  /**
   * Created quadtree based on transfer zone envelopes as spatial index.
   * Requires PlanitJtsIntersectZoneVisitor to filter out true spatial matches when querying.
   *
   *  @param <T> type of zone
   *  @param zones to spatially index
   *  @return created quadtree instance
   */
  public static <T extends Zone> Quadtree toGeoIndexed(Zones<T> zones) {
    var spatialZones = new Quadtree();
    for(var zone : zones){
      if(zone.getEnvelope() == null) {
        LOGGER.warning(String.format("Unable to spatially index %s %s, unknown spatial features, ignored", zone.getClass().getName(), zone.getXmlId()));
        continue;
      }
      spatialZones.insert(zone.getEnvelope(), zone);
    }
    return spatialZones;
  }

  /**
   * Created quadtree based on edge envelopes as spatial index. Requires PlanitJtsIntersectEdgeVisitor to filter out true spatial matches when querying.
   *
   *  @param <T> type of edge
   *  @param edgesCollection collections to add
   *  @return created quadtree instance
   */
  public static <T extends Edge> Quadtree toGeoIndexed(Collection<? extends GraphEntities<T>> edgesCollection) {
    Quadtree spatiallyIndexedEdges = new Quadtree();
    for(GraphEntities<T> edges : edgesCollection) {
      edges.forEach(edge -> spatiallyIndexedEdges.insert(edge.getGeometry().getEnvelope().getEnvelopeInternal(),edge));
    }
    return spatiallyIndexedEdges;
  }

  /**
   * Created quadtree based on edge envelopes as spatial index. Requires PlanitJtsIntersectEdgeVisitor to filter out true spatial matches when querying.
   *
   *  @param <T> type of edge
   *  @param edges collections to add
   *  @return created quadtree instance
   */
  public static <T extends GraphEntities<? extends Edge>> Quadtree toGeoIndexed(T edges) {
    Quadtree spatiallyIndexedEdges = new Quadtree();
    edges.forEach(edge -> {
      if(edge.hasGeometry()){
        spatiallyIndexedEdges.insert(edge.getGeometry().getEnvelope().getEnvelopeInternal(),edge);
      }
    });
    return spatiallyIndexedEdges;
  }

  /**
   * Query a quad tree populated with zone locations and return all zones within or touching the given bounding box
   *
   * @param spatialContainer to query
   * @param boundingBox to apply
   * @return found matches
   * @param <T> concrete zone type
   */
  public static <T extends Zone> Collection<T> queryZoneQuadtree(Quadtree spatialContainer, Envelope boundingBox){
    /* result to fill */
    final Set<TransferZone> correctZones = new HashSet<>();

    /* visitor to efficiently apply filtering */
    final PlanitJtsIntersectZoneVisitor<T> spatialZoneFilterVisitor =
            new PlanitJtsIntersectZoneVisitor(PlanitJtsUtils.create2DPolygon(boundingBox), correctZones);

    /* execute */
    spatialContainer.query(boundingBox, spatialZoneFilterVisitor);

    /* return findings */
    return spatialZoneFilterVisitor.getResult();
  }

  /** Find edges spatially based on the provided bounding box and spatially indexed quadtree containing edges as values
   *
   * @param <T> type of edge
   * @param searchBoundingBox to use
   * @param spatiallyIndexedEdgeTree to consider
   * @return links found intersecting or within bounding box provided
   */
  public static <T extends Edge> Collection<T> queryEdgeQuadtree(Envelope searchBoundingBox, Quadtree spatiallyIndexedEdgeTree) {
    PlanitJtsIntersectEdgeVisitor<T> edgevisitor = new PlanitJtsIntersectEdgeVisitor<>(PlanitJtsUtils.create2DPolygon(searchBoundingBox), new HashSet<>());
    spatiallyIndexedEdgeTree.query(searchBoundingBox, edgevisitor);
    return edgevisitor.getResult();
  }

}
