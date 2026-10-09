package org.goplanit.utils.network.layer.physical;

import org.goplanit.utils.graph.directed.ConjugateEdgeSegment;
import org.goplanit.utils.misc.Pair;

/**
 * Interface for a turn, i.e. conjugate link segment.
 * 
 * @author markr
 *
 */
public interface ConjugateLinkSegment extends ConjugateEdgeSegment, LinkSegment {

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConjugateLink getParent();
  
  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConjugateNode getUpstreamVertex();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConjugateNode getDownstreamVertex();
  
  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  @Override
  public default Pair<? extends LinkSegment,? extends LinkSegment> getOriginalAdjacentEdgeSegments(){
    return (Pair<? extends LinkSegment, ? extends LinkSegment>)
            ConjugateEdgeSegment.super.getOriginalAdjacentEdgeSegments();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConjugateLinkSegment shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConjugateLinkSegment deepClone();

}
