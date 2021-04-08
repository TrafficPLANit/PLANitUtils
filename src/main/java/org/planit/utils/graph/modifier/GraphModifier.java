package org.planit.utils.graph.modifier;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.planit.utils.exceptions.PlanItException;
import org.planit.utils.geo.PlanitJtsCrsUtils;
import org.planit.utils.graph.Edge;
import org.planit.utils.graph.Vertex;

/**
 * Modify graph elements .
 * 
 * @author markr
 *
 */
public interface GraphModifier<V extends Vertex, E extends Edge> {

  /**
   * remove any dangling subgraphs below a given size from the graph if they exist and subsequently reorder the internal ids if needed
   * 
   * @param belowSize         remove subgraphs below the given size
   * @param aboveSize         remove subgraphs above the given size (typically set to maximum value)
   * @param alwaysKeepLargest indicate if the largest of the subgraphs is always to be kept even if it does not match the criteria
   * @throws PlanItException thrown if error
   */
  public abstract void removeDanglingSubGraphs(Integer belowSize, Integer aboveSize, boolean alwaysKeepLargest) throws PlanItException;

  /**
   * remove the subgraph identified by the passed in vertices
   * 
   * @param subGraphToRemove the one to remove
   * @param recreateIds      indicate if the ids of the graph entities are to be recreated, if false gaps will occur so it is expected to be handled by the user afterwards in this
   *                         case
   */
  public abstract void removeSubGraph(Set<? extends V> subGraphToRemove, boolean recreateIds);
  
  /**
   * remove the (sub)graph in which the passed in vertex resides. Apply reordering of internal ids of remaining network.
   * 
   * @param referenceVertex to identify subnetwork by
   * @param recreateIds     indicate if the ids of the graph entities are to be recreated, if false gaps will occur so it is expected to be handled by the user afterwards in this
   *                        case
   * @throws PlanItException thrown if error
   */
  public abstract void removeSubGraphOf(V referenceVertex, boolean recreateIds) throws PlanItException;

  /**
   * Break the passed in edges by inserting the passed in vertex in between. After completion the original edges remain as (VertexA,VertexToBreakAt), and new edges are inserted for
   * (VertexToBreakAt,VertexB).
   * 
   * @param edgesToBreak    the links to break
   * @param vertexToBreakAt the node to break at
   * @param crs required to update edge lengths
   * @return affectedEdges the list of all result edges of the breaking of links by their original link id
   * @throws PlanItException thrown if error
   */
  public abstract Map<Long, Set<E>> breakEdgesAt(List<? extends E> edgesToBreak, V vertexToBreakAt, CoordinateReferenceSystem crs) throws PlanItException;  

  /**
   * this method will recreate all ids of the graph's main components, e.g., vertices, edges, and potentially other eligible components of derived graph implementations. Can be
   * used in conjunctions with the removal of subgraphs in case the recreation of ids was switched off manually for some reason.
   */
  public abstract void recreateIds();
  
  /**
   * register a listener that will be invoked whenever an entity of a subgraph is removed via {@link removeSubGraph}
   * 
   * @param listener to register
   */
  public abstract void registerRemoveSubGraphListener(RemoveSubGraphListener<V,E> listener);
  
  /**
   * unregister a listener that is currently registered
   * 
   * @param listener to unregister
   */  
  public abstract void unregisterRemoveSubGraphListener(RemoveSubGraphListener<V,E> listener);  
  
  /**
   * register a listener that will be invoked whenever a link is broken via {@link breakEdgesAt}
   * 
   * @param listener to register
   */  
  public void unregisterBreakEdgeListener(BreakEdgeListener<V, E> listener);

  /**
   * unregister a listener that is currently registered
   * 
   * @param listener to unregister
   */   
  public void registerBreakEdgeListener(BreakEdgeListener<V, E> listener);  

  /**
   * remove any dangling sub graphs from the graph if they exist and reorder the ids if needed
   * 
   * @throws PlanItException thrown if error
   */
  public default void removeDanglingSubGraphs() throws PlanItException {
    boolean alwaysKeepLargest = true;
    removeDanglingSubGraphs(Integer.MAX_VALUE, Integer.MAX_VALUE, alwaysKeepLargest);
  }
  
  /**
   * Break the passed in edges by inserting the passed in vertex in between. After completion the original edges remain as (VertexA,VertexToBreakAt), and new edges are inserted for
   * (VertexToBreakAt,VertexB). No coordinate reference system provided, so we assume cartesian coordinates
   * 
   * @param edgesToBreak    the links to break
   * @param vertexToBreakAt the node to break at
   * @return affectedEdges the list of all result edges of the breaking of links by their original link id
   * @throws PlanItException thrown if error
   */
  public default Map<Long, Set<E>> breakEdgesAt(List<? extends E> edgesToBreak, V vertexToBreakAt) throws PlanItException{
    return breakEdgesAt(edgesToBreak, vertexToBreakAt, PlanitJtsCrsUtils.CARTESIANCRS);
  }
  
  /**
   * reset all state related information of the instance
   */
  public abstract void reset();


}
