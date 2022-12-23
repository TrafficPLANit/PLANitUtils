package org.goplanit.utils.path;

import org.goplanit.utils.graph.directed.EdgeSegment;
import org.goplanit.utils.id.ExternalIdAble;
import org.goplanit.utils.id.ManagedId;

import java.util.Collection;
import java.util.Iterator;

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

  /** Verify if the path contains the provided subpath. It is only a subpath of the subpath is present continguously
   *
   * @param subPath to verify
   * @return true when it contains the subpath, false otherwise.
   */
  public abstract boolean containsSubPath(Iterator<? extends EdgeSegment> subPath);

}
