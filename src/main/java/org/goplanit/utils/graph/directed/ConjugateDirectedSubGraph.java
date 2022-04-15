package org.goplanit.utils.graph.directed;

/**
 * A conjugate directed subgraph interface for a given parent graph by registering conjugate edge segments on it (and therefore conjugate vertices and edges)
 * 
 * @author markr
 *
 */
public interface ConjugateDirectedSubGraph extends DirectedSubGraph<ConjugateDirectedVertex,ConjugateEdgeSegment> {
  
  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConjugateDirectedSubGraph clone();  
  
}
