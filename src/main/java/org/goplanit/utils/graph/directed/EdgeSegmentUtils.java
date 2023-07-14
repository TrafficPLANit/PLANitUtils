package org.goplanit.utils.graph.directed;

import java.util.function.Function;

/**
 * Utilities for Edge Segments that ar unfit for inclusion in the class itself
 */
public class EdgeSegmentUtils {

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
}
