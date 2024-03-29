package org.goplanit.utils.network.layer.physical;

import org.goplanit.utils.graph.ManagedGraphEntities;
import org.goplanit.utils.network.layer.macroscopic.MacroscopicLinkSegment;
import org.goplanit.utils.network.layer.macroscopic.MacroscopicLinkSegments;

import java.util.function.BiConsumer;

/**
 * Primary managed container class for conjugate links with access to factory capable of creating new conjugate links and registering them on the container
 * directly
 * 
 * @author markr
 *
 */
public interface ConjugateLinks extends ManagedGraphEntities<ConjugateLink> {
  /* do not derive from ConjugateDirectedEdges<E> since we require to override the factory method return type. This is only
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
  public abstract ConjugateLinks shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConjugateLinks deepClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConjugateLinks deepCloneWithMapping(BiConsumer<ConjugateLink, ConjugateLink> mapper);

}
