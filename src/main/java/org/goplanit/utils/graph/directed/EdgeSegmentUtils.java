package org.goplanit.utils.graph.directed;

import org.goplanit.utils.geo.PlanitJtsUtils;
import org.goplanit.utils.graph.EdgeUtils;
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
   * @param edgeToEdgeMapping to use should contain original edge as currently used on vertex and then the value is the new edge to replace it
   * @param removeMissingMappings when true if there is no mapping, the parent edge is nullified, otherwise it is left in-tact
   */
  public static <ES extends EdgeSegment, E extends DirectedEdge> void updateEdgeSegmentParentEdges(Iterable<ES> edgeSegments, Function<E, E> edgeToEdgeMapping, boolean removeMissingMappings) {
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
   * Create contiguous (deduplicated) geometry for list of edge segments into a single line string
   *
   * @param edgeSegmentIter to construct line string for
   * @return geometry created
   */
  public static LineString createGeometryFrom(Iterator<? extends EdgeSegment> edgeSegmentIter) {
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
          es.getParent().hasGeometry() ? es.getParent().getGeometry() : EdgeUtils.createLineStringFromVertexLocations(es.getParent(), es.isDirectionAb());
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
      LOGGER.warning("Found path where one or more links have no geometry or node positions, path geometry incomplete");
    }

    // deduplicate shared vertices between link (segments) when concatenating
    return PlanitJtsUtils.createCopyWithoutAdjacentDuplicateCoordinates(PlanitJtsUtils.concatenate(linkGeometries.toArray(LineString[]::new)));
  }
}
