package org.goplanit.utils.graph.modifier;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.goplanit.utils.exceptions.PlanItException;
import org.goplanit.utils.geo.PlanitJtsCrsUtils;
import org.goplanit.utils.graph.Edge;
import org.goplanit.utils.graph.UntypedSubGraph;
import org.goplanit.utils.graph.Vertex;
import org.goplanit.utils.graph.modifier.event.GraphModifierEventProducer;
import org.goplanit.utils.misc.Pair;

/**
 * Modify graph elements
 * 
 * @author markr
 *
 */
public interface GraphModifier<V extends Vertex, E extends Edge>
        extends GraphModifierEventProducer, TopologicalModifier{

  /**
   * Remove a vertex by removing it from the graph and the edges it is connected to. Any registered events for
   * vertex removal will be triggered. It does not remove the attached edges themselves.
   *
   * @param vertex to remove
   */
  public abstract void removeVertex(V vertex);

  /**
   * Remove an edge by removing it from the graph and the vertices it is connected to. Any registered events for
   * edge removal will be triggered. It does not remove the vertices themselves.
   *
   * @param edge to remove
   */
  public abstract void removeEdge(E edge);

  /**
   * remove any subgraphs below a given size from the graph if they exist and subsequently reorder the
   * internal ids if needed.
   *
   * @param belowSize         remove subgraphs below the given size
   * @param aboveSize         remove subgraphs above the given size (typically set to maximum value)
   * @param alwaysKeepLargest indicate if the largest of the subgraphs is always to be kept even if it does
   *                          not match the criteria
   */
  public abstract void removeDanglingSubGraphs(Integer belowSize, Integer aboveSize, boolean alwaysKeepLargest);

  /**
   * remove any subgraphs below a given size from the graph if they exist and subsequently reorder the
   * internal ids if needed.
   * 
   * @param belowSize         remove subgraphs below the given size
   * @param aboveSize         remove subgraphs above the given size (typically set to maximum value)
   * @param alwaysKeepLargest indicate if the largest of the subgraphs is always to be kept even if it does
   *                          not match the criteria
   * @param identifySubGraphForVertex function that given a starting vertex identifies the connected subgraph
   */
  public abstract void removeDanglingSubGraphs(
      Integer belowSize,
      Integer aboveSize,
      boolean alwaysKeepLargest,
      Function<V, UntypedSubGraph<V,E>> identifySubGraphForVertex);

  /**
   * remove the subgraph identified
   * 
   * @param subGraphToRemove the one to remove
   */
  public void removeSubGraph(UntypedSubGraph<V,E> subGraphToRemove);

  /**
   * Remove the (sub)graph in which the passed in vertex resides. Apply reordering of internal ids of remaining network.
   * remove  all attached entities such as links as well.
   *
   * @param referenceVertex to identify subnetwork by
   * @throws PlanItException thrown if error
   */
  public abstract void removeSubGraphOf(Vertex referenceVertex) throws PlanItException;

  /**
   * Break the passed in edges by inserting the passed in vertex in between. After completion the original edges
   * remain as (VertexA,VertexToBreakAt), and new edges are inserted for (VertexToBreakAt,VertexB).
   * 
   * @param <Ex> edge type
   * @param edgesToBreak    the links to break
   * @param vertexToBreakAt the node to break at
   * @param crs required to update edge lengths
   * @return affectedEdges the list of all result edges of the breaking of links by their original link id
   */
  public abstract <Ex extends E> Map<Long, Pair<Ex, Ex>> breakEdgesAt(
          final List<Ex> edgesToBreak, final V vertexToBreakAt, final CoordinateReferenceSystem crs);
  
  /**
   * Break the passed in edge by inserting the passed in vertex in between. After completion the original
   * edge remains as (VertexA,VertexToBreakAt), and new edges are inserted for (VertexToBreakAt,VertexB).
   *
   * @param <Ex> edge type
   * @param edgeToBreak    the link to break
   * @param vertexToBreakAt the node to break at
   * @param geoUtils required to update edge lengths
   * @return newlyCreatedEdge 
   */
  public abstract <Ex extends E> Ex breakEdgeAt(
          final V vertexToBreakAt, final Ex edgeToBreak, final PlanitJtsCrsUtils geoUtils);

  /**
   * This method will recreate all ids of the graph's components, e.g., vertices, edges, etc. but only when the
   * containers used for them are the primary ManagedIdEntities containers, i.e., when the graph is responsible for
   * uniquely tracking all entities by their managed id. If not, for example, if this is a subgraph reusing parts
   * of the main graph, it will not recreate the ids.
   * <p>
   * The reasoning is that if we would recreate ids of the container while the container does not contain
   * all = let's say - vertices, their managedId is no longer guaranteed to be unique which can lead to issues
   * <p> 
   * Method can be used in conjunctions with the removal of parts of the graph and the result is required to
   * have unique contiguous ids
   * <p>
   *   Should fire #RecreatedGraphEntitiesManagedIdsEvent after it has been executed
   * </p>
   *
   */
  public abstract void recreateManagedEntitiesIds();
  
  /**
   * remove any dangling sub graphs from the graph if they exist and reorder the ids if needed
   * 
   */
  public default void removeDanglingSubGraphs(){
    boolean alwaysKeepLargest = true;
    removeDanglingSubGraphs(Integer.MAX_VALUE, Integer.MAX_VALUE, alwaysKeepLargest);
  }
  
  /**
   * Break the passed in edges by inserting the passed in vertex in between. After completion the original
   * edges remain as (VertexA,VertexToBreakAt), and new edges are inserted for (VertexToBreakAt,VertexB).
   * No coordinate reference system provided, so we assume cartesian coordinates
   * 
   * @param <Ex> edge type
   * @param edgesToBreak    the links to break
   * @param vertexToBreakAt the node to break at
   * @return affectedEdges the list of all result edges of the breaking of links by their original link id
   */
  public default <Ex extends E> Map<Long, Pair<Ex,Ex>> breakEdgesAt(List<Ex> edgesToBreak, V vertexToBreakAt){
    return breakEdgesAt(edgesToBreak, vertexToBreakAt, PlanitJtsCrsUtils.CARTESIANCRS);
  }
  
  /**
   * reset all state related information of the instance
   */
  public abstract void reset();


}
