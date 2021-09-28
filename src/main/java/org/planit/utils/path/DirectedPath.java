package org.planit.utils.path;

import java.util.Collection;

import org.planit.utils.graph.EdgeSegment;
import org.planit.utils.id.ExternalIdAble;
import org.planit.utils.id.ManagedId;

/**
 * Path interface representing a path through the network on edge segment level
 * 
 * @author markr
 *
 */
public interface DirectedPath extends ExternalIdAble, ManagedId, Iterable<EdgeSegment> {

  /** class to use for id generation */
  public static final Class<DirectedPath> PATH_ID_CLASS = DirectedPath.class;
  
  /**
   * {@inheritDoc}
   */
  @Override
  public default Class<DirectedPath> getIdClass() {
    return PATH_ID_CLASS;
  }  
    
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

}
