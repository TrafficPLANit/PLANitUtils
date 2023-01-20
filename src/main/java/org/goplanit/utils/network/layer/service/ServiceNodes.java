package org.goplanit.utils.network.layer.service;

import org.goplanit.utils.graph.ManagedGraphEntities;

/**
 * Service node container and factory
 * 
 * @author markr
 *
 */
public interface ServiceNodes extends ManagedGraphEntities<ServiceNode> {
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
  
  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ServiceNodes clone();

  public default boolean hasServiceNode(long serviceNodeId){
    return containsKey(serviceNodeId);
  }

  public default boolean hasServiceNode(ServiceNode serviceNode){
    return hasServiceNode(serviceNode.getId());
  }

}
