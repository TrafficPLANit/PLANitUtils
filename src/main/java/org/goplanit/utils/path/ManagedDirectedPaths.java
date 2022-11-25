package org.goplanit.utils.path;

import org.goplanit.utils.id.ManagedIdEntities;

/**
 * Support directed paths as container and factory class 
 * 
 * @author markr
 *
 */
public interface ManagedDirectedPaths extends ManagedIdEntities<ManagedDirectedPath>  {

  /**
   * Factory to create paths on this container
   */
  @Override
  public abstract ManagedDirectedPathFactory getFactory();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ManagedDirectedPaths clone();

}
