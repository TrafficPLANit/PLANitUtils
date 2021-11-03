package org.goplanit.utils.path;

import java.util.function.Function;

import org.goplanit.utils.graph.EdgeSegment;
import org.goplanit.utils.graph.Vertex;
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
   * @param idMapper lambda function to get the required Id value
   * @return the path as a String of comma-separated node Id or external Id values
   */
  public static String getNodePathString(final DirectedPath path, final Function<Vertex, Object> idMapper) {
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
   * @param idGetter lambda function to get the required Id value
   * @return the path as a String of comma-separated link segment Id or external Id values
   */
  public static String getEdgeSegmentPathString(final DirectedPath path, final Function<EdgeSegment, Object> idGetter) {
    final StringBuilder builder = new StringBuilder("[");
    for (final EdgeSegment edgeSegment : path) {
      builder.append(idGetter.apply(edgeSegment));
      builder.append(",");
    }
    builder.deleteCharAt(builder.length() - 1);
    builder.append("]");
    return new String(builder);
  }  
}
