package org.goplanit.utils.graph.directed;

import org.goplanit.utils.geo.PlanitJtsUtils;
import org.goplanit.utils.graph.EdgeUtils;
import org.goplanit.utils.id.IdMapperType;
import org.goplanit.utils.id.IdMappingUtils;
import org.locationtech.jts.geom.LineString;

import java.util.*;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 * Utilities for Edge Segments that ar unfit for inclusion in the class itself
 */
public class EdgeSegmentUtils {

  private static final Logger LOGGER = Logger.getLogger(EdgeSegmentUtils.class.getCanonicalName());

  /**
   * Update the parent edge of all edge segments based on the mapping provided (if any)
   *
   * @param <E> type of edge
   * @param <ES> type of edge segment
   * @param edgeSegments to update
   * @param edgeToEdgeMapping to use should contain original edge as currently used on vertex and then the value is
   *                          the new edge to replace it
   * @param removeMissingMappings when true if there is no mapping, the parent edge is nullified, otherwise it is
   *                             left in-tact
   */
  public static <ES extends EdgeSegment, E extends DirectedEdge> void updateEdgeSegmentParentEdges(
          Iterable<ES> edgeSegments, Function<E, E> edgeToEdgeMapping, boolean removeMissingMappings) {

    for(var edgeSegment :  edgeSegments){
      var parent = (E) edgeSegment.getParent();
      if(parent == null){
        continue;
      }
      var newParent = edgeToEdgeMapping.apply(parent);
      if(newParent != null || removeMissingMappings) {
        edgeSegment.setParent(newParent);
      }
    }
  }

  /**
   * The geometry of an edge segment's parent edge, or a straight line between the edge's vertices in the segment's
   * direction when the edge has no geometry of its own
   *
   * @param edgeSegment to take the geometry of, with a parent edge
   * @return geometry found or created
   */
  public static LineString createOrGetParentGeometry(EdgeSegment edgeSegment) {
    return edgeSegment.getParent().hasGeometry() ? edgeSegment.getParent().getGeometry() :
        EdgeUtils.createLineStringFromVertexLocations(edgeSegment.getParent(), edgeSegment.isDirectionAb());
  }

  /**
   * The geometry of an edge segment running in the segment's direction, from its upstream to its downstream vertex, see
   * {@link #createOrGetParentGeometry}. The parent edge's own geometry is reversed only when it runs the other way; a
   * created one already runs in the segment's direction
   *
   * @param edgeSegment to take the geometry of, with a parent edge
   * @return geometry in the segment's direction, the parent edge's own geometry when it already runs that way
   */
  public static LineString createOrGetParentGeometryInSegmentDirection(EdgeSegment edgeSegment) {
    var geometry = createOrGetParentGeometry(edgeSegment);
    if (edgeSegment.hasGeometry() && !edgeSegment.isParentGeometryInSegmentDirection(true)) {
      geometry = geometry.reverse();
    }
    return geometry;
  }

  /**
   * Create contiguous (deduplicated) geometry for list of edge segments into a single line string, taking each parent
   * edge's geometry as it is drawn, see {@link #createGeometryFrom(Iterator, boolean)}
   *
   * @param edgeSegmentIter to construct line string for
   * @return geometry created
   */
  public static LineString createGeometryFrom(Iterator<? extends EdgeSegment> edgeSegmentIter) {
    return createGeometryFrom(edgeSegmentIter, false);
  }

  /**
   * Create contiguous (deduplicated) geometry for list of edge segments into a single line string
   *
   * @param edgeSegmentIter to construct line string for
   * @param inSegmentDirection true to turn each parent edge's geometry to run in its segment's direction, see
   *                           {@link #createOrGetParentGeometryInSegmentDirection}, false to take it as it is drawn
   * @return geometry created
   */
  public static LineString createGeometryFrom(
      Iterator<? extends EdgeSegment> edgeSegmentIter, boolean inSegmentDirection) {
    if(edgeSegmentIter == null){
      return null;
    }

    List<LineString> linkGeometries = new ArrayList<>();
    boolean missingGeometries = false;
    while(edgeSegmentIter.hasNext()){
      var es = edgeSegmentIter.next();
      if(!es.hasParent()){
        LOGGER.warning("Found path where one or more links have no geometry, path geometry incomplete");
        break;
      }
      LineString result =
          inSegmentDirection ? createOrGetParentGeometryInSegmentDirection(es) : createOrGetParentGeometry(es);
      if(result == null){
        missingGeometries = true;
      }

      linkGeometries.add(result);
    }

    if(linkGeometries.isEmpty()){
      LOGGER.warning("Found path where no links have a geometry nor their nodes, path geometry unavailable");
      return null;
    }

    if(missingGeometries){
      LOGGER.warning("Found path where one or more links have no geometry or node positions," +
              " path geometry incomplete");
    }

    // deduplicate shared vertices between link (segments) when concatenating
    return PlanitJtsUtils.createCopyWithoutAdjacentDuplicateCoordinates(
            PlanitJtsUtils.concatenate(linkGeometries.toArray(LineString[]::new)));
  }

  /**
   * Based on edge segment construct an id string based on parent link id (configurable which) and the direction resulting
   * in "link_id_mapping-direction" where direction is either BA or BA, e.g. 4-AB.
   *
   * @param edgeSegment to use
   * @param idMapperType parent link id mapping
   * @return string form of proposed id
   */
  public static String createParentLinkDerivedId(final EdgeSegment edgeSegment, IdMapperType idMapperType) {
    var linkId =  IdMappingUtils.getIdMappingFunction(idMapperType).apply(edgeSegment.getParent());
    return String.format("%s-%s",linkId, (edgeSegment.isDirectionAb() ? "AB" : "BA"));
  }
}
