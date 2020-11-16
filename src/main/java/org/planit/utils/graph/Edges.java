package org.planit.utils.graph;

import org.geotools.geometry.jts.JTS;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.planit.utils.exceptions.PlanItException;

import com.vividsolutions.jts.geom.LineString;

/**
 * Container and factory class for edges in a graph, also to be used to create and register edges of any
 * (derived) type
 * 
 * @author markr
 */
public interface Edges<E extends Edge> extends Iterable<E> {
  
  /**
   * remove an edge.
   * 
   * @param edge to remove
   */
  public void remove(E edge);
  
  /**
   * remove an edge.
   * 
   * @param edgeId of the edge to remove
   */
  public void remove(long edgeId);  

  /**
   * Create new edge to graph identified via its id
   *
   * @param vertexA the first vertex of this link
   * @param vertexB the second vertex of this link
   * @param length  the length (in km)
   * @return the created edge
   * @throws PlanItException thrown if there is an error
   */
  public E registerNew(final Vertex vertexA, final Vertex vertexB, final double length) throws PlanItException;
  
  /** copy the passed in edge and register it
   * 
   * @param edgeToCopy as is except for its ids which will be updated to makeit uniquely identifiable
   * @return copy of edge now registered
   */
  public E registerUniqueCopyOf(E edgeToCopy);

  /**
   * Get edge by id
   *
   * @param id the id of the edge
   * @return the retrieved edge, null if not present
   */
  public E get(final long id);

  /**
   * Get the number of edges in the graph
   *
   * @return the number of edges in the graph
   */
  public int size();

  /**
   * Check if is empty
   * @return true when no edges, false otherwise
   */
  public boolean isEmpty();
  

}
