package org.goplanit.utils.zoning;

import org.goplanit.utils.graph.directed.DirectedVertex;
import org.goplanit.utils.network.virtual.ConnectoidSegment;

/**
 * A centroid is a special type of vertex representing the location of departure/arrival of traffic
 * in
 * a zone
 * 
 * @author markr
 *
 */
public interface Centroid extends DirectedVertex {

  /**
   * Return the parent zone of this centroid
   * 
   * @return parent zone of this centroid
   */
  public abstract Zone getParentZone();
  
  /**
   * The name of the centroid
   * 
   * @return its name
   */
  public abstract String getName();
  
  /**
   * Set the name of the centroid
   * 
   * @param name to use
   */
  public abstract void setName(String name);
  
  /** check if a name has been set
   * 
   * @return true when set, false otherwise
   */
  public default boolean hasName() {
    return getName()!= null && !getName().isBlank();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract Centroid clone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract Centroid deepClone();

}
