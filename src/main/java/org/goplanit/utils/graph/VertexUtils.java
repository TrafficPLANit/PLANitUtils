package org.goplanit.utils.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;

/**
 * Utilities for vertices that ar unfit for inclusion in the class itself
 */
public class VertexUtils {

  /**
   * Update the edges of all vertices based on the mapping provided (if any)
   *
   * @param <V> type of vertex
   * @param <E> type of edge
   * @param vertices the vertices to update
   * @param edgeToEdgeMapping to use should contain original edge as currently used on vertex and then the value is the new edge to replace it
   * @param removeMissingMappings when true if there is no mapping, the edge is removed as adjacent to the vertex, otherwise they are left in-tact
   */
  public static <V extends Vertex, E extends Edge> void updateVertexEdges(Iterable<V> vertices, Function<E,E> edgeToEdgeMapping, boolean removeMissingMappings) {
    for(var vertex : vertices){
      var toBeAdded = new ArrayList<E>(vertex.getEdges().size());
      var toBeRemoved = new ArrayList<E>(vertex.getEdges().size());
      for(var currEdge : vertex.getEdges()){
        var newEdge = edgeToEdgeMapping.apply((E) currEdge);
        if (newEdge != null) {
          toBeAdded.add(newEdge);
        }
        if (removeMissingMappings || newEdge != null) {
          toBeRemoved.add((E) currEdge);
        }
      }
      vertex.addEdges(toBeAdded);
      vertex.removeEdges(toBeRemoved);
    }
  }
}
