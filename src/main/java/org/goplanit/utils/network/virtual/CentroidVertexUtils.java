package org.goplanit.utils.network.virtual;

import org.goplanit.utils.graph.directed.DirectedEdge;
import org.goplanit.utils.graph.directed.EdgeSegment;
import org.goplanit.utils.zoning.Centroid;

import java.util.function.Function;

/**
 * Utilities for centroid vertices that do not belong in centroid vertex class itself
 */
public class CentroidVertexUtils {

  /**
   * Update the parent edge of all edge segments based on the mapping provided (if any)
   *
   * @param <V> type of centroid vertex
   * @param <C> type of centroid
   * @param centroidVertices the centroid vertices to use
   * @param centroidToCentroidMapping to use should contain original centroid as used on current centroid vertex and then the value to replace it
   * @param removeMissingMappings when true if there is no mapping, the parent centroid is nullified, otherwise it is left in-tact
   */
  public static <V extends CentroidVertex, C extends Centroid > void updateCentroidVertexCentroidMapping(
      Iterable<V> centroidVertices, Function<C, C> centroidToCentroidMapping, boolean removeMissingMappings) {
    for (var centroidVertex : centroidVertices) {
      var parent = (C) centroidVertex.getParent();
      if (parent == null) {
        continue;
      }
      var newParent = centroidToCentroidMapping.apply(parent);
      if (newParent != null || removeMissingMappings) {
        centroidVertex.setParent(newParent);
      }
    }
  }
}
