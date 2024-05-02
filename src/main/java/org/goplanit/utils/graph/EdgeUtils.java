package org.goplanit.utils.graph;

import org.goplanit.utils.geo.PlanitJtsUtils;
import org.goplanit.utils.misc.Pair;
import org.locationtech.jts.geom.LineString;

import java.util.function.Function;

/**
 * Utilities for edges that do not belong within the Edge class itself
 */
public class EdgeUtils {

  /** Collect shared vertex between two edges if any exists
   * @param edge1 to check
   * @param edge2 to check
   * @return shared vertex, null if none is found
   */
  public static Vertex getSharedVertex(Edge edge1, Edge edge2) {
    if(edge1 == null || edge2 == null) {
      return null;
    }
    
    if(edge1.getVertexA() == edge2.getVertexA() || edge1.getVertexA() == edge2.getVertexB()) {
      return edge1.getVertexA();
    }else if(edge1.getVertexB() == edge2.getVertexA() || edge1.getVertexB() == edge2.getVertexB()) {
      return edge1.getVertexB();
    }
    return null;
  }

  /**
   * Update the vertices of all edges based on the mapping provided. If no mapping exists, the edge will be assigned a null reference, unless indicated otherwise
   *
   * @param <E> type of edge
   * @param <V> type of vertex
   * @param edges to update vertices for
   * @param vertexToVertexMapping to use should contain original vertex as currently used on edge and then the value is the new vertex to replace it
   * @param replaceMissingMappings when true missing mappings results in a null assignment, otherwise they are left in-tact
   */
  public static <E extends Edge, V extends Vertex> void updateEdgeVertices(Iterable<E> edges, Function<V,V> vertexToVertexMapping, boolean replaceMissingMappings) {
    edges.forEach( edge -> {
      var newVertexA = vertexToVertexMapping.apply((V)edge.getVertexA());
      if(newVertexA!= null || replaceMissingMappings) {
        edge.replace(edge.getVertexA(), newVertexA);
      }
      var newVertexB = vertexToVertexMapping.apply((V)edge.getVertexB());
      if(newVertexB!= null || replaceMissingMappings) {
        edge.replace(edge.getVertexB(), newVertexB);
      }
    });
  }

  /**
   * Create a direct line with two points based on the edge extremities. Direction can be chosen either from vertex A to B or vice versa.
   * If vertices have no known location, then null is returned
   *
   * @param edge to use
   * @param directionAb to apply
   * @return line string created
   */
  public static LineString createLineStringFromVertexLocations(Edge edge, boolean directionAb) {
    if(edge == null || !edge.hasVertexA() || !edge.hasVertexB()){
      return null;
    }

    if(!edge.getVertexA().hasPosition() || !edge.getVertexB().hasPosition()){
      return null;
    }

    var points = directionAb ? Pair.of(edge.getVertexA().getPosition(), edge.getVertexB().getPosition()) :
            Pair.of(edge.getVertexB().getPosition(), edge.getVertexA().getPosition());
    return PlanitJtsUtils.createLineString(points.first().getCoordinate(), points.second().getCoordinate());
  }
}
