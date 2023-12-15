package org.goplanit.utils.path;

import java.util.Collection;
import java.util.function.Function;

import org.goplanit.utils.graph.Vertex;
import org.goplanit.utils.graph.directed.EdgeSegment;
import org.goplanit.utils.network.layer.physical.Node;

/**
 * Path utilities
 * 
 * @author markr
 *
 */
public class PathUtils {

  /**
   * Returns the path as a String of comma-separated node Ids using the id mapper
   *
   * @param path to extract from 
   * @param idMapper lambda function to get the required Id value
   * @return the path as a String of comma-separated node Id or external Id values
   */
  public static String getNodePathString(final ManagedDirectedPath path, final Function<Vertex, Object> idMapper) {
    final StringBuilder builder = new StringBuilder("[");
    for (final EdgeSegment edgeSegment : path) {
      final Vertex vertex = edgeSegment.getUpstreamVertex();
      if (vertex instanceof Node) {
        final Node node = (Node) vertex;
        builder.append(idMapper.apply(node));
        if (edgeSegment.getDownstreamVertex() instanceof Node) {
          builder.append(",");
        }
      }
    }
    builder.append("]");
    return new String(builder);
  }

  /**
   * Returns the path as a String of comma-separated edge segment Id or external Id values
   *
   * @param path to extract from
   * @param idGetter lambda function to get the required Id value
   * @return the path as a String of comma-separated link segment Id or external Id values
   */
  public static String getEdgeSegmentPathString(final ManagedDirectedPath path, final Function<EdgeSegment, Object> idGetter) {
    final StringBuilder builder = new StringBuilder("[");
    for (final EdgeSegment edgeSegment : path) {
      builder.append(idGetter.apply(edgeSegment));
      builder.append(",");
    }
    builder.deleteCharAt(builder.length() - 1);
    builder.append("]");
    return new String(builder);
  }

  /**
   * Compute path cost by summing the edge segment costs provided based on id as index in cost array for ech provided path
   *
   * @param paths to get cost for
   * @param edgeSegmentCostsById array with costs per edge segment
   * @return path cost array found in order of collection
   */
  public static double[] computeEdgeSegmentAdditivePathCost(Collection<? extends SimpleDirectedPath> paths, double[] edgeSegmentCostsById){
    final double pathCosts[] = new double[paths.size()];
    int index = 0;
    for(var path : paths){
      pathCosts[index++] = computeEdgeSegmentAdditivePathCost(path, edgeSegmentCostsById);
    }
    return pathCosts;
  }

  /**
   * Compute path cost by summing the edge segment costs provided based on id as index in cost array
   *
   * @param path to get cost for
   * @param edgeSegmentCostsById array with costs per edge segment
   * @return path cost found
   */
  public static double computeEdgeSegmentAdditivePathCost(SimpleDirectedPath path, double[] edgeSegmentCostsById){
    double pathCost = 0.0;
    for(var iter = path.iterator(); iter.hasNext();){
      var currEdgeSegment = iter.next();
      pathCost += edgeSegmentCostsById[ (int) currEdgeSegment.getId()];
    }
    return pathCost;
  }


}
