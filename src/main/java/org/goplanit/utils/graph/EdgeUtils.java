package org.goplanit.utils.graph;

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
}
