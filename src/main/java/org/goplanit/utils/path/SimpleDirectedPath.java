package org.goplanit.utils.path;

import org.goplanit.utils.graph.directed.EdgeSegment;
import org.goplanit.utils.id.ExternalIdAble;
import org.goplanit.utils.id.ManagedId;
import org.goplanit.utils.misc.IterableUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.stream.StreamSupport;

/**
 * Simple Path interface representing a path through the network on edge segment level
 * 
 * @author markr
 *
 */
public interface SimpleDirectedPath extends Iterable<EdgeSegment> {

  /** The size of the path is given by the number of edge segments it holds
   * 
   * @return size
   */
  public abstract long size();

  /**
   * verify if the size of the path is zero (empty) or not
   * 
   * @return true when empty, false otherwise
   */
  public default boolean isEmpty() {
    return size()<=0;
  }

  /** Verify if the path contains the provided subpath. It is only a subpath of the subpath is present continguously
   * 
   * @param subPath to verify
   * @return true when it contains the subpath, false otherwise.
   */
  public abstract boolean containsSubPath(Collection<? extends EdgeSegment> subPath);

  /** Verify if the path contains the provided subpath. It is only a subpath of the subpath is present contiguously
   *
   * @param subPath to verify
   * @return true when it contains the subpath, false otherwise.
   */
  public abstract boolean containsSubPath(Iterator<? extends EdgeSegment> subPath);

  /**
   * Compute the length in km of the path based on the sum of the edge segment's getLengthKm method
   *
   * @return total length of path in km
   */
  public default double computeLengthKm(){
    return StreamSupport.stream(this.spliterator(),false).mapToDouble( es -> es.getLengthKm()).sum();
  }

  /**
   * Collect first segment on path
   *
   * @return initial segment
   */
  public abstract EdgeSegment getFirstSegment();

  /**
   * Collect last segment on path
   *
   * @return last segment
   */
  public abstract EdgeSegment getLastSegment();

  /** hash code for simple directed path
   *
   * @return hash code of path
   */
  public abstract int hashCode();

}
