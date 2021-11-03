package org.goplanit.utils.path;

import org.goplanit.utils.id.ManagedIdEntities;

/**
 * Support directed paths as container and factory class 
 * 
 * @author markr
 *
 */
public interface DirectedPaths extends ManagedIdEntities<DirectedPath>  {

  /**
   * Factory to create paths on this container
   */
  @Override
  public abstract ContainerisedDirectedPathFactory getFactory();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract DirectedPaths clone();

}
