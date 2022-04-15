package org.goplanit.utils.graph.directed;

/**
 * Conjugate version of edge segment representing connection between two edge segments on origin network
 * 
 * @author markr
 *
 */
public interface ConjugateEdgeSegment extends EdgeSegment{

  /**
   * Entry segment in original graph for this conjugate
   * @return edge segment
   */
  public abstract EdgeSegment getOriginalEntrySegment();

  /**
   * Exit segment in original graph for this conjugate
   * @return edge segment
   */
  public abstract EdgeSegment getOriginalExitSegment();
}
