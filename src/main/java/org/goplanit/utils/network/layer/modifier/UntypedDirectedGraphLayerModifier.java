package org.goplanit.utils.network.layer.modifier;

import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.goplanit.utils.exceptions.PlanItRunTimeException;
import org.goplanit.utils.graph.directed.DirectedEdge;
import org.goplanit.utils.graph.directed.DirectedVertex;
import org.goplanit.utils.graph.directed.EdgeSegment;
import org.goplanit.utils.graph.modifier.event.GraphModifierEventProducer;
import org.goplanit.utils.id.ExternalIdAble;
import org.goplanit.utils.misc.Pair;
import org.goplanit.utils.network.layer.UntypedDirectedGraphLayer;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Modifier with additional functionality related to modifications to layers derived from
 * {@link UntypedDirectedGraphLayer}. Since it wraps an untyped directed graph it exposes the event listener
 * functionality to the end user by implementing the GraphModifierEventProducer interface allowing users to register
 * listeners for the events fired when breaking links or removing subnetworks (subgraphs)
 *
 * @author markr
 */
public interface UntypedDirectedGraphLayerModifier<V extends DirectedVertex, E extends DirectedEdge, S extends EdgeSegment>
        extends TopologicalLayerModifier, GraphModifierEventProducer {
    
  /**
   * Break the passed in link by inserting the passed in node in between. After completion the original links remain
   * as (NodeA,NodeToBreakAt), and new links as inserted for (NodeToBreakAt,NodeB).
   * Underlying link segments (if any) are also updated accordingly in the same manner
   * 
   * @param linkToBreak        the link to break
   * @param nodeToBreakAt      the node to break at
   * @param crs                to use to recompute link lengths of broken links
   * @return the broken edges for each original edge's id
   */
  public default Map<Long, Pair<E,E>> breakAt(E linkToBreak, V nodeToBreakAt, CoordinateReferenceSystem crs) {
    return breakAt(List.of(linkToBreak), nodeToBreakAt, crs);
  }

  /**
   * Break the passed in links by inserting the passed in node in between. After completion the original links remain
   * as (NodeA,NodeToBreakAt), and new links as inserted for (NodeToBreakAt,NodeB).
   * Underlying link segments (if any) are also updated accordingly in the same manner.
   * 
   * @param linksToBreak       the links to break
   * @param nodeToBreakAt      the node to break at
   * @param crs                to use to recompute link lengths of broken links
   * @return the broken edges for each original edge's id
   */
  public abstract Map<Long, Pair<E,E>> breakAt(List<E> linksToBreak, V nodeToBreakAt, CoordinateReferenceSystem crs);

  /**
   * Break the passed in links by inserting the passed in node in between. After completion the original links remain
   * as (NodeA,NodeToBreakAt), and new links as inserted for (NodeToBreakAt,NodeB).
   * Underlying link segments (if any) are also updated accordingly in the same manner.
   *
   * @param <K> type of key
   * @param linksToBreak links that will be broken if they contain theNode as an internal geo location
   * @param nodeToBreakAt      the node to break at
   * @param crs of the network layer
   * @param linkToKey mapping results in group by key for result map
   * @return newly created PLANit links by a custom key obtained from original broken link and each entry containing
   * all broken links for all original link's mapping to the given key value
   *
   */
  public default <K> Map<K, Set<E>> breakAt(
      List<E> linksToBreak, V nodeToBreakAt, CoordinateReferenceSystem crs, Function<E,K> linkToKey){
    Map<K, Set<E>> groupNewLinksByOriginalLinkKey = new HashMap<>();

    if(linksToBreak != null) {

      try {
        /* performing breaking of links at the node given, returns the broken links by the original link's PLANit
         * edge id */
        Map<Long, Pair<E,E>> localBrokenLinks = breakAt(linksToBreak, nodeToBreakAt, crs);
        /* add newly created links to the mapping from original broken link to a key, e.g. external id, that together
         * form this entire original link*/
        if(localBrokenLinks != null) {
          localBrokenLinks.forEach((id, links) -> {
            List.of(links.first(),links.second()).forEach( brokenLink -> {
              final K brokenLinkKey = linkToKey.apply(brokenLink);
              groupNewLinksByOriginalLinkKey.putIfAbsent(brokenLinkKey, new HashSet<>());
              groupNewLinksByOriginalLinkKey.get(brokenLinkKey).add(brokenLink);
            });
          });
        }
      }catch(PlanItRunTimeException e) {
        throw new PlanItRunTimeException("Unable to break links %s for node %s, something unexpected went wrong",e,
            linksToBreak.stream().map(ExternalIdAble::getExternalId).collect(
                    Collectors.toSet()).toString(), nodeToBreakAt.getExternalId());
      }
    }

    return groupNewLinksByOriginalLinkKey;
  }

  /**
   * Recreate all managed id entities on the layer
   * <p>
   *   Should fire #RecreatedGraphEntitiesManagedIdsEvent and #RecreatedDirectedGraphEntitiesManagedIdsEvent after each
   *   relevant managedIds container (nodes, links) and (link segments) that has updated
   * </p>
   */
  public abstract void recreateManagedIdEntities();

  /**
   * Remove an edge from the layer and any edge segments in the process
   *
   * <p>
   *   Should fire #RemoveSubGraphEdgeEvent for the edge that is to be removed
   * </p>
   *
   * @param edge to remove
   */
  public void removeEdge(E edge);

  /**
   * Remove an edge segment by removing it from the graph and the edge it is connected to. Any registered events
   * for edge segment removal will be triggered.
   * <p>
   *   Should fire #RemoveSubGraphEdgeSegmentEvent for the edge segment that is to be removed
   * </p>
   *
   * @param edgeSegment to remove
   */
  public void removeEdgeSegment(S edgeSegment);
}
