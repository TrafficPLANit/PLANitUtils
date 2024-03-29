package org.goplanit.utils.graph;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.geotools.geometry.jts.JTS;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

/**
 * Vertex representation connected to one or more edges and/or edge segments
 * 
 * @author markr
 *
 */
public interface Vertex extends Serializable, GraphEntity {
  
  /** vertex logger */
  public static final Logger LOGGER = Logger.getLogger(Vertex.class.getCanonicalName());
  
  /** id class for generating ids */
  public static final Class<Vertex> VERTEX_ID_CLASS = Vertex.class;  
    
  /**
   * Add a property from the original input that is not part of the readily available members
   *
   * @param key   key (name) of the input property
   * @param value value of the input property
   */
  public abstract void addInputProperty(final String key, final Object value);  
  
  /** collect a property 
   * 
   * @param key for the property
   * @return property itself
   */
  public abstract Object getInputProperty(final String key);

  /**
   * Set the center point geometry for a vertex
   * 
   * @param position the center point for a vertex
   */
  public abstract void setPosition(final Point position);

  /**
   * Collect the geometry of the point location of this vertex
   * 
   * @return direct position reflecting point location
   */
  public abstract Point getPosition();
  
  /**
   * Add edge, do not invoke when parsing networks, this connection is
   * auto-populated before the assignment starts based on the edge and its two
   * vertices that have been registered.
   * 
   * @param edge Edge to be added
   * @return true when added, false when already present (and not added)
   */
  public abstract boolean addEdge(Edge edge);

  /**
   * Add multiple edges at the same time
   *
   * @param toBeAdded the to be added edges
   */
  public default void addEdges(Collection<? extends Edge> toBeAdded){
    toBeAdded.forEach( edge -> addEdge(edge));
  }
  
  /**
   * Remove edge
   * 
   * @param edgeId Edge to be removed
   * @return true when removed, false when not present (and not removed)
   */
  public abstract boolean removeEdge(long edgeId);  

  /**
   * Returns a collection of Edge objects (unmodifiable)
   * 
   * @return Set of Edge objects
   */
  public abstract Collection<? extends Edge> getEdges();  
    
  /**
   * Shallow clone
   * @return the cloned entity
   */
  @Override
  public abstract Vertex shallowClone();

  /**
   * Deep clone
   * @return the cloned entity
   */
  @Override
  public abstract Vertex deepClone();
  
  /**
   * All vertices use the VERTEX_ID_CLASS to generate the unique internal ids
   */
  @Override
  public default Class<? extends Vertex> getIdClass() {
    return VERTEX_ID_CLASS;
  }  
  
  /**
   * Verify if position is available
   * 
   * @return true when available, false otherwise
   */
  public default boolean hasPosition() {
    return getPosition() != null;
  }
  
  /**
   * Remove edge
   * 
   * @param edge Edge to be removed
   * @return true when removed, false when not present (and not removed)
   */
  public default boolean removeEdge(final Edge edge) {
    return removeEdge(edge.getId());
  }

  /**
   * Remove provided edges
   *
   * @param toBeRemoved to remove
   */
  public default void removeEdges(Collection<? extends Edge> toBeRemoved){
    toBeRemoved.forEach(e -> removeEdge(e));
  }

  /**
   * Remove al edges from vertex
   */
  public abstract void removeAllEdges();
  
  /**
   * Collect the edge(s) based on the other vertex
   * 
   * @param otherVertex that defines the edge(s)
   * @return edges for which this holds, if none hold an empty set is returned
   */
  public default Set<? extends Edge> getEdges(Vertex otherVertex) {
    Set<Edge> edges = new HashSet<Edge>();
    for (Edge edge : getEdges()) {
      if (edge.getVertexA().getId() == this.getId() && edge.getVertexB().getId() == otherVertex.getId()) {
        edges.add(edge);
      } else if (edge.getVertexB().getId() == this.getId() && edge.getVertexA().getId() == otherVertex.getId()) {
        edges.add(edge);
      }
    }
    return edges;
  }  
  
  /**
   * Number of entries in edge segments
   * @return the number of edges connected to this vertex
   */
  public default int getNumberOfEdges() {
    return getEdges().size();
  }
  
  /** replace one edge with the other
   * 
   * @param edgeToReplace one to replace
   * @param edgeToReplaceWith one to replace it with
   * @param forceInsert when true the replacement will be added even if original cannot be found, when false not
   * @return successful replacement/insert when true, false otherwise
   */
  public default boolean replace(Edge edgeToReplace, Edge edgeToReplaceWith, boolean forceInsert) {
    if(removeEdge(edgeToReplace) || forceInsert) {
      return addEdge(edgeToReplaceWith);      
    }
    return false;
  }  

  /** transform the position information of this vertex using the passed in MathTransform
   * 
   * @param transformer to apply
   * @throws MismatchedDimensionException thrown if error
   * @throws TransformException thrown if error
   */
  public default void transformPosition(MathTransform transformer) throws MismatchedDimensionException, TransformException {
    setPosition((Point) JTS.transform(getPosition(),transformer));
  }

  /** Verify if the vertex position is exactly equal (in 2D) to the passed in coordinate
   * 
   * @param coordinate to check against
   * @return true when equal location in 2D, false otherwise
   */
  public default boolean isPositionEqual2D(Coordinate coordinate) {
    return hasPosition() && getPosition().getCoordinate().equals2D(coordinate);
  }

  /** Verify if the vertex position is exactly equal (in 2D) to the passed in coordinate
   *
   * @param coordinate to check against
   * @param epsilon allowed difference to still be regarded equal
   * @return true when equal location in 2D, false otherwise
   */
  public default boolean isPositionEqual2D(Coordinate coordinate, double epsilon) {
    return hasPosition() && getPosition().getCoordinate().equals2D(coordinate, epsilon);
  }
  
  /** Validate the vertex regarding it connections to edges etc.
   * 
   * @return true when valid, false otherwise
   */
  public default boolean validate() {
    for (Edge edge : getEdges()) {
      if (!edge.hasVertex(this)) {
        LOGGER.warning(
            String.format(
                "Edge (id:%d) does not contain vertex (id:%d externalId:%s) even though the vertex is connected to it", edge.getId(), getId(), getExternalId()));
        return false;
      }
    }
    return true;
  }

}
