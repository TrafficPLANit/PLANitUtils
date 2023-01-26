package org.goplanit.utils.network.layer.physical;

import org.goplanit.utils.network.layer.physical.PhysicalLayer;

/**
 * Physical topological Network consisting of nodes, links and link segments 
 *
 * @author markr
 */
public interface PhysicalLayer extends UntypedPhysicalLayer<Node, Link, LinkSegment> {

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract PhysicalLayer clone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract PhysicalLayer deepClone();
}
