package org.planit.utils.graph;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

import org.geotools.geometry.jts.JTS;
import org.locationtech.jts.geom.Point;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.planit.utils.id.ExternalIdable;

/**
 * Vertex representation connected to one or more edges and/or edge segments
 * 
 * @author markr
 *
 */
public interface Vertex extends Serializable, ExternalIdable {
  
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
   * Remove edge
   * 
   * @param edge Edge to be removed
   * @return true when removed, false when not present (and not removed)
   */
  public abstract boolean removeEdge(Edge edge);
  
  /**
   * Remove edge
   * 
   * @param edgeId Edge to be removed
   * @return true when removed, false when not present (and not removed)
   */
  public abstract boolean removeEdge(long edgeId);  

  /**
   * Returns a collection of Edge objects
   * 
   * @return Set of Edge objects
   */
  public abstract Collection<? extends Edge> getEdges();
  
  /**
   * Number of entries in edge segments
   * @return the number of edges connected to this vertex
   */
  public abstract int getNumberOfEdges();  
  
  /**
   * colect the edge(s) based on the other vertex
   * 
   * @param otherVertex that defines the edge(s)
   * @return edges for which this holds, if none hold an empty set is returned
   */
  public abstract Set<Edge> getEdges(Vertex otherVertex);   
  
  /**
   * Clone the vertex
   * @return the cloned vertex
   */
  public abstract Vertex clone();

  /** validate the vertex regarding it connections to edges etc.
   * 
   * @return true when valid, false otherwise
   */
  public abstract boolean validate();
  
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
}
