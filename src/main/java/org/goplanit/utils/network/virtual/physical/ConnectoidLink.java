package org.goplanit.utils.network.virtual.physical;

import org.goplanit.utils.exceptions.PlanItRunTimeException;
import org.goplanit.utils.graph.directed.EdgeSegment;
import org.goplanit.utils.mode.Mode;
import org.goplanit.utils.network.layer.physical.Link;
import org.goplanit.utils.network.layer.physical.LinkSegment;
import org.goplanit.utils.network.virtual.graph.ConnectoidDirectedEdge;

import java.util.Collection;

/**
 * Connectoid version of a link
 * 
 * @author markr
 *
 */
public interface ConnectoidLink extends ConnectoidDirectedEdge, Link {

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConnectoidSegment registerEdgeSegment(
      final EdgeSegment edgeSegment, final boolean directionAB, final boolean force);

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConnectoidSegment removeEdgeSegmentAb();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConnectoidSegment removeEdgeSegmentBa();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConnectoidSegment getEdgeSegmentAb();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConnectoidSegment getEdgeSegmentBa();

  /**
   * {@inheritDoc}
   */
  @Override
  public default ConnectoidSegment getEdgeSegment(boolean directionAb) {
    return directionAb ? getEdgeSegmentAb() : getEdgeSegmentBa();
  }

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  @Override
  public default Collection<? extends ConnectoidSegment> getEdgeSegments(){
    return (Collection<? extends ConnectoidSegment>) Link.super.getEdgeSegments();
  }

  @Override
  public default boolean isModeAllowedOnAnySegment(Mode mode){
    throw new PlanItRunTimeException("Connectoid links do not support modes explicitly yet");
  }

  @Override
  public default boolean isModeAllowedOnAllSegments(Mode mode){
    throw new PlanItRunTimeException("Connectoid links do not support modes explicitly yet");
  }

  @Override
  public default LinkSegment getLinkSegmentIfLinkIsOneWayForMode(Mode mode) {
    throw new PlanItRunTimeException("Connectoid links do not support modes explicitly yet");
  }

  /**
   * {@inheritDoc}
   */
  public abstract ConnectoidLink shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConnectoidLink deepClone();
  
}
