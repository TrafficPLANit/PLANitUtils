package org.goplanit.utils.graph.directed;

import java.util.function.Function;

/**
 * Utilities for Edge Segments that ar unfit for inclusion in the class itself
 */
public class DirectedEdgeUtils {

  /**
   * Update the edge segments of all directed edge based on the mapping provided (if any)
   * @param edgeSegmentToEdgeSegmentMapping to use should contain original edgeSegment and then the value is the new edgeSegment to replace it
   * @param removeMissingMappings when true if there is no mapping, the edgeSegment on the directed edge is nullified, otherwise it is left in-tact
   */
  public static <E extends DirectedEdge, ES extends EdgeSegment> void updateDirectedEdgeEdgeSegments(
      Iterable<E> edges, Function<ES, ES> edgeSegmentToEdgeSegmentMapping, boolean removeMissingMappings) {
    for(var directedEdge :  edges){
      var newAbSegment = edgeSegmentToEdgeSegmentMapping.apply((ES) directedEdge.getEdgeSegmentAb());
      if(newAbSegment != null || removeMissingMappings){
        directedEdge.registerEdgeSegment(newAbSegment, true);
      }
      var newBaSegment = edgeSegmentToEdgeSegmentMapping.apply((ES) directedEdge.getEdgeSegmentBa());
      if(newBaSegment != null || removeMissingMappings){
        directedEdge.registerEdgeSegment(newBaSegment, false);
      }
    }
  }
}
