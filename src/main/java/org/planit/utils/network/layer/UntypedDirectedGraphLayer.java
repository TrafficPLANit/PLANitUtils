package org.planit.utils.network.layer;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.planit.utils.exceptions.PlanItException;
import org.planit.utils.graph.EdgeSegment;
import org.planit.utils.graph.GraphEntities;
import org.planit.utils.graph.directed.DirectedEdge;
import org.planit.utils.graph.directed.DirectedVertex;
import org.planit.utils.graph.modifier.BreakEdgeListener;
import org.planit.utils.id.IdGroupingToken;
import org.planit.utils.network.layer.UntypedDirectedGraphLayer;

/**
 * Network layer comprising of containers with custom entity types. Use this as a base template for implementations and derived interfaced that
 * are typed. No access to containers is dictated to allow for maximum flexibility for derived interface to define suitable method names to access
 * underlying containers and/or entities. Since all entities are id managed we do require access to the id token used by this layer
 *
 * @author markr
 */
public interface UntypedDirectedGraphLayer<V extends DirectedVertex, VE extends GraphEntities<V>, E extends DirectedEdge, EE extends GraphEntities<E>, S extends EdgeSegment, SE extends GraphEntities<S>> extends TopologicalLayer {

  /**
   * Collect the id grouping token used for all entities registered on the layer, i.e., this layer's specific identifier for generating ids unique and contiguous within this
   * layer 
   * 
   * @return the layer id grouping token
   */
  public abstract IdGroupingToken getLayerIdGroupingToken();
  
  /**
   * Break the passed in link by inserting the passed in node in between. After completion the original links remain as (NodeA,NodeToBreakAt), and new links as inserted for
   * (NodeToBreakAt,NodeB).
   * 
   * Underlying link segments (if any) are also updated accordingly in the same manner
   * 
   * @param linkToBreak        the link to break
   * @param nodeToBreakAt      the node to break at
   * @param crs                to use to recompute link lengths of broken links
   * @param breakEdgeListeners the listeners to register (temporarily) when we break edges so they get invoked for callbacks (may be nnull)
   * @return the broken edges for each original edge's id
   * @throws PlanItException thrown if error
   */
  public default Map<Long, Set<E>> breakAt(E linkToBreak, V nodeToBreakAt, CoordinateReferenceSystem crs, Set<BreakEdgeListener> breakEdgeListeners) throws PlanItException {
    return breakAt(List.of(linkToBreak), nodeToBreakAt, crs, breakEdgeListeners);
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
    return breakAt(linksToBreak, nodeToBreakAt, crs, null);
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
   * @param breakEdgeListeners the listeners to register (temporarily) when we break edges so they get invoked for callbacks
   * @return the broken edges for each original edge's id
   * @throws PlanItException thrown if error
   */
  public abstract Map<Long, Set<E>> breakAt(List<E> linksToBreak, V nodeToBreakAt, CoordinateReferenceSystem crs, Set<BreakEdgeListener> breakEdgeListeners)
      throws PlanItException;  
  
}
