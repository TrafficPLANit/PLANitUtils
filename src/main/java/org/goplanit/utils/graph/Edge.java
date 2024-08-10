package org.goplanit.utils.graph;

import java.io.Serializable;

import org.geotools.geometry.jts.JTS;
import org.goplanit.utils.exceptions.PlanItRunTimeException;
import org.goplanit.utils.geo.PlanitJtsUtils;
import org.goplanit.utils.math.Precision;
import org.goplanit.utils.misc.Pair;
import org.goplanit.utils.network.layer.macroscopic.MacroscopicLink;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.linearref.LinearLocation;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

/**
 * Edge interface connecting two vertices in a non-directional fashion.
 * 
 * @author markr
 *
 */
public interface Edge extends Serializable, GraphEntity {
  
  /** id class for generating ids */
  public static final Class<Edge> EDGE_ID_CLASS = Edge.class;   
      
  /** Verify if passed in vertex is the same object reference as vertex A
   * 
   * @param vertex to check
   * @return true when identical object instance, false otherwise
   */
  public default boolean isVertexA(Vertex vertex) {
    return getVertexA() == vertex;
  }

  /**
   * Collect the geometry of this line
   * @return lineString
   */
  public abstract LineString getGeometry();
  
  /**
   * set the geometry of this link as a line string
   * @param lineString to set
   */
  public abstract void setGeometry(LineString lineString);

  /**
   * Utilising the A and B vertex construct a direct line between the two points as the geometry
   *
   * @param overwrite  when true, overwrite existing geometry, otherwise ignore
   * @return true when successful, false otherwise
   */
  public default boolean populateBasicGeometry(boolean overwrite){
    if(hasGeometry() && !overwrite){
      return false;
    }

    if(getVertexA()==null || getVertexB() == null){
      return false;
    }

    var posA = getVertexA().getPosition();
    var posB = getVertexB().getPosition();
    if(posA == null || posB == null){
      return false;
    }

    setGeometry(PlanitJtsUtils.createLineString(posA.getCoordinate(),posB.getCoordinate()));
    return true;
  }
   
  /**
   * Remove vertex from edge 
   * 
   * @param vertex to remove
   * @return true when successful false otherwise
   */
  public abstract boolean removeVertex(Vertex vertex);    

  // Getters-Setters  

  /**
   * Vertex A of the edge
   * 
   * @return vertex A
   */
  public abstract Vertex getVertexA();

  /**
   * Vertex B of the edge
   * 
   * @return vertex B
   */
  public abstract Vertex getVertexB();

  /**
   * set vertex B. Use with care.
   *
   * @param vertexB to set
   */
  public abstract void setVertexB(Vertex vertexB);

  /**
   * set vertex A. Use with care
   *
   * @param vertexA to set
   */
  public abstract void setVertexA(Vertex vertexA);

  /**
   * set the name of the edge
   * @param name to set
   */
  public abstract void setName(final String name);  
  
  /**
   * get the name of the edge
   * 
   * @return name
   */
  public abstract String getName();
  
  /**
   * set length of this edge in km
   * 
   * @param lengthInKm of this edge in km
   */
  public abstract void setLengthKm(double lengthInKm);  
  
  /**
   * Return length of this edge in km
   * 
   * @return length of this edge in km
   */
  public abstract double getLengthKm();  

  /**
   * Add a property from the original input that is not part of the readily
   * available link members
   * 
   * @param key (name) of input property
   * @param value of input property
   */
  public abstract void addInputProperty(final String key, final Object value);

  /**
   * Get input property by its key
   * 
   * @param key of input property
   * @return value retrieved value of input property
   */
  public abstract Object getInputProperty(String key);

  /**
   * Replace one of the vertices of the link
   * 
   * @param vertexToReplace the vertex to replace
   * @param vertexToReplaceWith the vertex to replace with
   * @return true when replaced, false otherwise
   */
  public abstract boolean replace(final Vertex vertexToReplace, final Vertex vertexToReplaceWith);
  
  /**
   * Shallow copy
   * 
   * @return copy of this edge
   */
  public abstract Edge shallowClone();

  /**
   * Deep copy, non-owned members are reference copied
   *
   * @return copy of this edge
   */
  public abstract Edge deepClone();
  
  /** validate the contents of this edge 
   * @return true when valid, false otherwise
   */
  public abstract boolean validate();
  
  /**
   * All edges use the EDGE_ID_CLASS to generate the unique internal ids
   */
  @Override
  public default Class<? extends Edge> getIdClass() {
    return EDGE_ID_CLASS;
  }   
  
  /** Verify if a name has been set
   * @return true when present, false otherwise
   */
  public default boolean hasName() {
    return getName()!=null && !getName().isBlank();
  }

  /** check if vertex is present on the edge
   * @param vertex to check
   * @return true when present false otherwise
   */
  public default boolean hasVertex(Vertex vertex) {
    return getVertexA().equals(vertex) || getVertexB().equals(vertex); 
  }

  /**
   * Verify if vertex A is available
   *
   * @return true when present, false otherwise
   */
  public default boolean hasVertexA(){
    return getVertexA() != null;
  }

  /**
   * Verify if vertex B is available
   *
   * @return true when present, false otherwise
   */
  public default boolean hasVertexB(){
    return getVertexB() != null;
  }

  /**
   * Verify if vertex A and B are available
   *
   * @return true when present, false otherwise
   */
  public default boolean hasVertices(){
    return hasVertexA() && hasVertexB();
  }
  
  /**
   * check if geometry is available
   * 
   * @return true when available, false otherwise
   */
  public default boolean hasGeometry() {
    return getGeometry()!=null;
  }
  
  /** verify if the geometry is in the A to B direction of the link, both vertices must have geometry present.
   * @return true if in A to B direction, false otherwise
   */
  public default boolean isGeometryInAbDirection() {
    return isGeometryInAbDirection(false);
  }

  /** verify if the geometry is in the A to B direction of the edge. When one of the vertices has no geometry, we may or may not allow for
   * this. If this is the case, we enforce that at least one end of the geometry is matched to the other vertex's geometry to
   * infer direction.
   *
   * @param allowSingleVertexWithoutGeometry when true, we assume that geometry of edge is ok to be not matching vertex on one end
   * @return true if in A to B direction, false otherwise
   */
  public default boolean isGeometryInAbDirection(boolean allowSingleVertexWithoutGeometry) {
    if(!hasGeometry()){
      throw new PlanItRunTimeException("Unable to identify direction as edge (%s) has no geometry of its own", this.getIdsAsString());
    }

    var vertexAHasGeometry = getVertexA().hasPosition();
    var vertexBHasGeometry = getVertexB().hasPosition();
    if(!vertexAHasGeometry && !vertexBHasGeometry){
      throw new PlanItRunTimeException("Unable to identify direction as both vertices of edge %s have no geometry", this.getIdsAsString());
    }else if(!allowSingleVertexWithoutGeometry && !(vertexAHasGeometry && vertexBHasGeometry)){
      throw new PlanItRunTimeException("One of the vertices has no geometry for edge %s, this is not allowed", this.getIdsAsString());
    }

    // Given difficulty of ensuring consistency in rounding between various geometries
    // we check both, ideally we make sure we have a precision model throughout, but this is not implemented yet
    boolean isVertexAStartPoint = vertexAHasGeometry && getGeometry().getStartPoint().getCoordinate().equals2D(getVertexA().getPosition().getCoordinate(), Precision.EPSILON_6);
    boolean isVertexBEndPoint = vertexBHasGeometry && getGeometry().getEndPoint().getCoordinate().equals2D(getVertexB().getPosition().getCoordinate(), Precision.EPSILON_6);
    if(isVertexAStartPoint && isVertexBEndPoint){
      return true;
    }else if(isVertexAStartPoint && !vertexBHasGeometry){
      return true;
    }else if(isVertexBEndPoint && !vertexAHasGeometry){
      return true;
    }

    boolean isVertexAEndPoint = vertexBHasGeometry && getGeometry().getStartPoint().getCoordinate().equals2D(getVertexB().getPosition().getCoordinate(), Precision.EPSILON_6);
    boolean isVertexBStartPoint = vertexAHasGeometry && getGeometry().getEndPoint().getCoordinate().equals2D(getVertexA().getPosition().getCoordinate(), Precision.EPSILON_6);
    if(isVertexBStartPoint && isVertexAEndPoint){
      return false;
    }

    throw new PlanItRunTimeException("Unable to identify direction as vertex locations do not match internal geometry of edge within reason it appears");
  }
  
  /** transform the line string information of this edge using the passed in MathTransform
   * 
   * @param transformer to apply
   * @throws MismatchedDimensionException thrown if error
   * @throws TransformException thrown if error
   */
  public default void transformGeometry(MathTransform transformer) throws MismatchedDimensionException, TransformException {
    setGeometry((LineString) JTS.transform(getGeometry(),transformer));
  }

  /**
   * Update the geometry by taking the current geometry and inject a coordinate at the projected location between existing coordinates using the passed in location.
   * this replaces the existing geometry instance which is returned.
   *
   * @param projectedLinearLocation to use as reference point of new coordinate
   * @return old geometry that is now replaced
   */
  public default Geometry updateGeometryInjectCoordinateAtProjectedLocation(LinearLocation projectedLinearLocation){
    var oldGeometry = getGeometry();
    Pair<LineString, LineString> splitLineString = PlanitJtsUtils.splitLineString(getGeometry(),projectedLinearLocation);
    LineString linkGeometryWithExplicitProjectedCoordinate = PlanitJtsUtils.mergeLineStrings(splitLineString.first(),splitLineString.second());
    setGeometry(linkGeometryWithExplicitProjectedCoordinate);
    return oldGeometry;
  }
  
  /** collect the bounding box of the geometry of this link
   * 
   * @return envelope (bounding box) of this link based on its geometry
   */
  public default Envelope createEnvelope() {
    if(hasGeometry()) {
      return getGeometry().getEnvelopeInternal();
    }
    return null;
  }

}
