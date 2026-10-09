package org.goplanit.utils.graph.directed;

import org.goplanit.utils.graph.ConjugateEdge;

/**
 * A conjugate directed subgraph interface for a given parent graph
 * 
 * @author markr
 *
 */
public interface ConjugateDirectedSubGraph extends
    UntypedDirectedSubGraph<ConjugateDirectedVertex, ConjugateDirectedEdge, ConjugateEdgeSegment> {
  
  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConjugateDirectedSubGraph shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConjugateDirectedSubGraph deepClone();
}
