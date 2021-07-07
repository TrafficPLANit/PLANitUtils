package org.planit.utils.network.layer.service;

import org.planit.utils.graph.GraphEntities;

/**
 * Service node container and factory
 * 
 * @author markr
 *
 */
public interface ServiceNodes extends GraphEntities<ServiceNode> {
  /* do not derive from Nodes since we require to override the factory method return type. This is only
   * allowed when the return type directly derives from the original return type. ServiceNodeFactory cannot
   * derive from NodeFactory since the signature of the factory methods differs. Hence, we must derive from
   * the base interface instead which has an empty dummy factory return type which one can always overwrite and
   * the ServiceNodeFactory is derived from */
  
  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ServiceNodeFactory getFactory();
}
