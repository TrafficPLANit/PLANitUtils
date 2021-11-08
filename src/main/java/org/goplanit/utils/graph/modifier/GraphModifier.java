package org.goplanit.utils.graph.modifier;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.goplanit.utils.exceptions.PlanItException;
import org.goplanit.utils.geo.PlanitJtsCrsUtils;
import org.goplanit.utils.graph.Edge;
import org.goplanit.utils.graph.Vertex;
import org.goplanit.utils.graph.modifier.event.GraphModifierEventProducer;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Modify graph elements
 * 
 * @author markr
 *
 */
public interface GraphModifier<V extends Vertex, E extends Edge> extends GraphModifierEventProducer, TopologicalModifier{

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
   */
  public abstract void removeSubGraph(Set<? extends V> subGraphToRemove);
  
  /**
   * remove the (sub)graph in which the passed in vertex resides. Apply reordering of internal ids of remaining network.
   * 
   * @param referenceVertex to identify subnetwork by
   * @throws PlanItException thrown if error
   */
  public abstract void removeSubGraphOf(V referenceVertex) throws PlanItException;

  /**
   * Break the passed in edges by inserting the passed in vertex in between. After completion the original edges remain as (VertexA,VertexToBreakAt), and new edges are inserted for
   * (VertexToBreakAt,VertexB).
   * 
   * @param <Ex> edge type
   * @param edgesToBreak    the links to break
   * @param vertexToBreakAt the node to break at
   * @param crs required to update edge lengths
   * @return affectedEdges the list of all result edges of the breaking of links by their original link id
   * @throws PlanItException thrown if error
   */
  public abstract <Ex extends E> Map<Long, Set<Ex>> breakEdgesAt(final List<Ex> edgesToBreak, final V vertexToBreakAt, final CoordinateReferenceSystem crs) throws PlanItException;
  
  /**
   * Break the passed in edge by inserting the passed in vertex in between. After completion the original edge remains as (VertexA,VertexToBreakAt), and new edges are inserted for
   * (VertexToBreakAt,VertexB).
   *
   * @param <Ex> edge type
   * @param edgeToBreak    the link to break
   * @param vertexToBreakAt the node to break at
   * @param geoUtils required to update edge lengths
   * @return newlyCreatedEdge 
   * @throws PlanItException thrown if error
   */
  public abstract <Ex extends E> Ex breakEdgeAt(final V vertexToBreakAt, final Ex edgeToBreak, final PlanitJtsCrsUtils geoUtils) throws PlanItException;  

  /**
   * This method will recreate all ids of the graph's components, e.g., vertices, edges, etc. but only when the containers used for them are the primary ManagedIdEntities containers, i.e., when the graph
   * is responsible of uniquely tracking all entities by their managed id. If not, for example, if this is a subgraph reusing parts of the main graph, it will not recreate the ids. 
   * <p>
   * The reasoning is that if we would recreate ids of the container while the container does not contain all = let's say - vertices, their managedId is no longer guaranteed to be unique which can lead to issues
   * <p> 
   * Method can be used in conjunctions with the removal of parts of the graph and the result is required to have unique contiguous ids
   */
  public abstract void recreateManagedEntitiesIds();
  
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
   * @param <Ex> edge type
   * @param edgesToBreak    the links to break
   * @param vertexToBreakAt the node to break at
   * @return affectedEdges the list of all result edges of the breaking of links by their original link id
   * @throws PlanItException thrown if error
   */
  public default <Ex extends E> Map<Long, Set<Ex>> breakEdgesAt(List<Ex> edgesToBreak, V vertexToBreakAt) throws PlanItException{
    return breakEdgesAt(edgesToBreak, vertexToBreakAt, PlanitJtsCrsUtils.CARTESIANCRS);
  }
  
  /**
   * reset all state related information of the instance
   */
  public abstract void reset();


}
