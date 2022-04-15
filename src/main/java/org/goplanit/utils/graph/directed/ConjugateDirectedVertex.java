package org.goplanit.utils.graph.directed;

/**
 * Conjugate of directed vertex representing an edge segment in "regular" directed graph
 * 
 * @author markr
 *
 */
public interface ConjugateDirectedVertex extends DirectedVertex{

  /** Original edge segment in original directed graph
   * @return
   */
  public abstract EdgeSegment getOriginalEdgeSegment();
}
