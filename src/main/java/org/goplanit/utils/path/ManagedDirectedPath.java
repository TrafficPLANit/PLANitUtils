package org.goplanit.utils.path;

import java.util.Collection;

import org.goplanit.utils.graph.directed.EdgeSegment;
import org.goplanit.utils.id.ExternalIdAble;
import org.goplanit.utils.id.ManagedId;

/**
 * Path interface representing a path through the network on edge segment level
 * 
 * @author markr
 *
 */
public interface ManagedDirectedPath extends ExternalIdAble, ManagedId, SimpleDirectedPath {

  /** class to use for id generation */
  public static final Class<ManagedDirectedPath> PATH_ID_CLASS = ManagedDirectedPath.class;
  
  /**
   * {@inheritDoc}
   */
  @Override
  public default Class<ManagedDirectedPath> getIdClass() {
    return PATH_ID_CLASS;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ManagedDirectedPath shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ManagedDirectedPath deepClone();

}
