package org.goplanit.utils.network.layer.physical;

import org.goplanit.utils.graph.directed.ConjugateDirectedVertex;

/**
 * Conjugate Node is the conjugate of a normal link. It is expected that its id is synced with the
 * original link it represents
 * 
 * @author markr
 *
 */
public interface ConjugateNode extends ConjugateDirectedVertex, Node {

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConjugateNode shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConjugateNode deepClone();
}
