package org.goplanit.utils.graph.modifier;

import org.goplanit.utils.graph.EdgeSegment;

/**
 * A listener interface to be triggered whenever planit entities are removed from the network due to a sub graph being
 * removed. Can be used by any exogenous party to receive a call baack in case it relies on these removed entities 
 * 
 * @author markr
 *
 */
public interface RemoveDirectedSubGraphListener extends RemoveSubGraphListener{
  
  /** callback whenever an edge segment is removed from a subgraph it is part of
   * 
   * @param edgeSegment that is removed
   */
  public void onRemoveSubGraphEdgeSegment(EdgeSegment edgeSegment);  
}
