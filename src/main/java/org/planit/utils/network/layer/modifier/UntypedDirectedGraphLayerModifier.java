package org.planit.utils.network.layer.modifier;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.planit.utils.exceptions.PlanItException;
import org.planit.utils.graph.EdgeSegment;
import org.planit.utils.graph.GraphEntities;
import org.planit.utils.graph.directed.DirectedEdge;
import org.planit.utils.graph.directed.DirectedVertex;
import org.planit.utils.graph.modifier.event.GraphModifierEventProducer;
import org.planit.utils.network.layer.UntypedDirectedGraphLayer;

/**
 * Modifier with additional functionality related to modifications to layers derived from {@link UntypedDirectedGraphLayer}. Since it wraps
 * an untyped directed graph it exposes the event listener functionality to the end user by implementing the GraphModifierEventProducer interface
 * allowing users tor egister listeners for the events fired when breaking links or removing subnetworks (subgraphs)
 *
 * @author markr
 */
public interface UntypedDirectedGraphLayerModifier<V extends DirectedVertex, VE extends GraphEntities<V>, E extends DirectedEdge, EE extends GraphEntities<E>, S extends EdgeSegment, SE extends GraphEntities<S>> extends TopologicalLayerModifier, GraphModifierEventProducer {
    
  /**
   * Break the passed in link by inserting the passed in node in between. After completion the original links remain as (NodeA,NodeToBreakAt), and new links as inserted for
   * (NodeToBreakAt,NodeB).
   * 
   * Underlying link segments (if any) are also updated accordingly in the same manner
   * 
   * @param linkToBreak        the link to break
   * @param nodeToBreakAt      the node to break at
   * @param crs                to use to recompute link lengths of broken links
   * @return the broken edges for each original edge's id
   * @throws PlanItException thrown if error
   */
  public default Map<Long, Set<E>> breakAt(E linkToBreak, V nodeToBreakAt, CoordinateReferenceSystem crs) throws PlanItException {
    return breakAt(List.of(linkToBreak), nodeToBreakAt, crs);
  }

  /**
   * Break the passed in links by inserting the passed in node in between. After completion the original links remain as (NodeA,NodeToBreakAt), and new links as inserted for
   * (NodeToBreakAt,NodeB). It is assumed no transfer zones exist in the network, otherwise one should use the same method yet provide the zoning as an additiona parameter to
   * ensure affected connectoids are updated to reflect the new situation
   * 
   * Underlying link segments (if any) are also updated accordingly in the same manner
   * 
   * @param linksToBreak  the links to break
   * @param nodeToBreakAt the node to break at
   * @param crs           to use to recompute link lengths of broken links
   * @return the broken edges for each original edge's id
   * @throws PlanItException thrown if error
   */
  public default Map<Long, Set<E>> breakLinksAt(List<E> linksToBreak, V nodeToBreakAt, CoordinateReferenceSystem crs) throws PlanItException {
    return breakAt(linksToBreak, nodeToBreakAt, crs);
  }

  /**
   * Break the passed in links by inserting the passed in node in between. After completion the original links remain as (NodeA,NodeToBreakAt), and new links as inserted for
   * (NodeToBreakAt,NodeB).
   * 
   * Underlying link segments (if any) are also updated accordingly in the same manner
   * 
   * @param linksToBreak       the links to break
   * @param nodeToBreakAt      the node to break at
   * @param crs                to use to recompute link lengths of broken links
   * @return the broken edges for each original edge's id
   * @throws PlanItException thrown if error
   */
  public abstract Map<Long, Set<E>> breakAt(List<E> linksToBreak, V nodeToBreakAt, CoordinateReferenceSystem crs) throws PlanItException;
  
}
