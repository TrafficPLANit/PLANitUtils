package org.goplanit.utils.network.layer.physical;

import org.goplanit.utils.graph.ManagedGraphEntities;

/**
 * Primary managed container class for conjugate links with access to factory capable of creating new conjugate links and registering them on the container
 * directly
 * 
 * @author markr
 *
 */
public interface ConjugatedLinks extends ManagedGraphEntities<ConjugateLink> {
  /* do not derive from DirectedEdges<E> since we require to override the factory method return type. This is only
   * allowed when the return type directly derives from the original return type. LinkFactory cannot
   * derive from DirectedEdgeFactory since the signature of the factory methods differs. Hence, we must derive from
   * the base interface instead which has an empty dummy factory return type which one can always overwrite and
   * the LinkFactory is derived from */
  
  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConjugateLinkFactory getFactory();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConjugatedLinks clone();

}
