package org.goplanit.utils.path;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Function;

import org.goplanit.utils.graph.Vertex;
import org.goplanit.utils.graph.directed.EdgeSegment;
import org.goplanit.utils.misc.Pair;
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
    if(path != null) {
      for (final EdgeSegment edgeSegment : path) {
        final Vertex vertex = edgeSegment.getUpstreamVertex();
        if (vertex instanceof Node) {
          final Node node = (Node) vertex;
          builder.append(idMapper.apply(node));
          builder.append(",");
        }
      }
    }

    if(builder.length() > 1){
      builder.deleteCharAt(builder.length() - 1);
    }

    builder.append("]");
    return builder.toString();
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
    if(path != null) {
      for (final EdgeSegment edgeSegment : path) {
        builder.append(idGetter.apply(edgeSegment));
        builder.append(",");
      }
    }

    if(builder.length() > 1){
      builder.deleteCharAt(builder.length() - 1);
    }

    builder.append("]");
    return builder.toString();
  }

  /**
   * Compute summed path value by combining the edge segment values provided based on id as index in value array for ech provided path
   *
   * @param <T> type of path
   * @param paths to get sum for
   * @param edgeSegmentValuesById array with values, e.g., costs per edge segment
   * @return path value array found in order of collection
   */
  public static <T extends SimpleDirectedPath> double[] computeEdgeSegmentsSummedValue(
      Collection<T> paths, double[] edgeSegmentValuesById){
    final double pathValues[] = new double[paths.size()];
    int index = 0;
    for(var path : paths){
      pathValues[index++] = computeEdgeSegmentsSummedValue(path, edgeSegmentValuesById);
    }
    return pathValues;
  }

  /**
   * Compute a path relevant total value by summing the edge segment values provided based on id as index in value array
   *
   * @param path to get summed value for
   * @param edgeSegmentValuesById array with values per edge segment
   * @return path total found
   */
  public static double computeEdgeSegmentsSummedValue(SimpleDirectedPath path, double[] edgeSegmentValuesById){
    double pathAdditiveValue = 0.0;
    for(var iter = path.iterator(); iter.hasNext();){
      var currEdgeSegment = iter.next();
      pathAdditiveValue += edgeSegmentValuesById[ (int) currEdgeSegment.getId()];
    }
    return pathAdditiveValue;
  }

  /**
   * Compute a path relevant average value by summing the edge segment values provided based on id as index in value array
   *
   * @param path to get average value for
   * @param edgeSegmentValuesById array with values per edge segment
   * @return path average found
   */
  public static double computeEdgeSegmentsAverageValue(SimpleDirectedPath path, double[] edgeSegmentValuesById) {
    return computeEdgeSegmentsSummedValue(path, edgeSegmentValuesById)/path.size();
  }


  /**
   * Find path overlap between two paths by it shared link segment ids
   *
   * @param path1 to check against path2
   * @param path2 to check against path 1
   * @return shared link segment ids
   */
  public static int[] getOverlappingPathLinkIndices(SimpleDirectedPath path1, SimpleDirectedPath path2) {
    int[] overlappingIndices = new int[(int)path1.size()];
    int overlapIndex = 0;
    for(var p1LinkSegment : path1){
      if(path2.containsLinkSegmentId(p1LinkSegment.getId())){
        overlappingIndices[overlapIndex++] = (int)p1LinkSegment.getId();
      }
    }
    int[] overlappingIndicesTrimmed = new int[overlapIndex];
    System.arraycopy(overlappingIndices, 0, overlappingIndicesTrimmed, 0, overlapIndex);
    return overlappingIndicesTrimmed;
  }

  /**
   * Find path overlap distance between two paths by it shared link segment lengths, where the portion found relates
   * to path1 overlapping with path2
   *
   * @param path1 to check against overlap with path2
   * @param path2 to use as reference
   * @return distance overlapping with path 2 and path 1 total distance in km
   */
  public static Pair<Double, Double> getOverlappingPathLinkDistanceKm(SimpleDirectedPath path1, SimpleDirectedPath path2){
    double distanceP1 = 0;
    double overlapDistance = 0;
    for(var p1LinkSegment : path1){
      distanceP1 += p1LinkSegment.getLengthKm();
      if(path2.containsLinkSegmentId(p1LinkSegment.getId())){
        overlapDistance += p1LinkSegment.getLengthKm();
      }
    }
    return Pair.of(overlapDistance, distanceP1);
  }

  /**
   * Find path overlap between two paths by it shared link segment lengths, where the portion found relates
   * to path1 overlapping with path2
   *
   * @param path1 to check against overlap with path2
   * @param path2 to use as reference
   * @return portion overlapping in terms of distance between 0 (nothing) and 1 (full overlap)
   */
  public static double getOverlapFactor(SimpleDirectedPath path1, SimpleDirectedPath path2){
    var absoluteOverlapResult = getOverlappingPathLinkDistanceKm(path1, path2);
    return absoluteOverlapResult.first()/absoluteOverlapResult.second();
  }

  /**
   * Verify if the super path contains the sub path
   *
   * @param superPathIter iterator representing the super path
   * @param subPathIter iterator representing the subpath
   * @return true when sub path exists, false otherwise
   */
  public static boolean containsSubPath(
      Iterator<? extends EdgeSegment> superPathIter, Iterator<? extends EdgeSegment> subPathIter) {
    if (superPathIter == null && superPathIter.hasNext()) {
      return false;
    }

    if (subPathIter == null && subPathIter.hasNext()) {
      return false;
    }

    EdgeSegment subPathSegment = subPathIter.next();
    boolean started = false;
    while (superPathIter.hasNext()) {
      var edgeSegment = superPathIter.next();
      if(started){
        subPathSegment = subPathIter.next();
      }

      if (edgeSegment.idEquals(subPathSegment)) {
        started = true;
      } else if (started) {
        started = false;
        break;
      }

      if (!subPathIter.hasNext()) {
        break;
      }
    }

    return started && !subPathIter.hasNext();
  }

}
