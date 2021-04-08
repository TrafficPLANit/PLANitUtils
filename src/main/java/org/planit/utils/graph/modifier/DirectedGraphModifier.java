package org.planit.utils.graph.modifier;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.planit.utils.graph.Edge;
import org.planit.utils.graph.EdgeSegment;
import org.planit.utils.graph.Vertex;

/**
 * Modify directed graph elements .
 * 
 * @author markr
 *
 */
public interface DirectedGraphModifier<V extends Vertex, E extends Edge, ES extends EdgeSegment> extends GraphModifier<V, E>{

  /** register listener for removing directed sub graphs
   * @param subGraphRemovalListener to register
   */
  public abstract void registerRemoveSubGraphListener(RemoveDirectedSubGraphListener<V, E, ES> subGraphRemovalListener);


}
