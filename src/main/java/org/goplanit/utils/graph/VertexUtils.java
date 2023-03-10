package org.goplanit.utils.graph;

import java.util.ArrayList;
import java.util.function.Function;

/**
 * Utilities for vertices that ar unfit for inclusion in the class itself
 */
public class VertexUtils {

  /**
   * Update the edges of all vertices based on the mapping provided (if any)
   * @param edgeToEdgeMapping to use should contain original edge as currently used on vertex and then the value is the new edge to replace it
   * @param removeMissingMappings when true if there is no mapping, the edge is removed as adjacent to the vertex, otherwise they are left in-tact
   */
  public static <V extends Vertex, E extends Edge> void updateVertexEdges(Iterable<V> vertices, Function<E,E> edgeToEdgeMapping, boolean removeMissingMappings) {
    for(var vertex : vertices){
      var edgeIter = vertex.getEdges().iterator();
      var toBeAdded = new ArrayList<E>(vertex.getEdges().size());
      while (edgeIter.hasNext()) {
        var currEdge = edgeIter.next();
        var newEdge = edgeToEdgeMapping.apply((E) currEdge);
        if (newEdge != null) {
          toBeAdded.add(newEdge);
        }
        if (removeMissingMappings && newEdge == null) {
          edgeIter.remove();
        }
      }
      vertex.addEdges(toBeAdded);
    }
  }
}
