package org.goplanit.utils.zoning;

import org.goplanit.utils.graph.directed.DirectedVertex;
import org.goplanit.utils.id.IdGenerator;
import org.goplanit.utils.id.IdGroupingToken;
import org.goplanit.utils.id.ManagedId;
import org.goplanit.utils.network.layer.macroscopic.MacroscopicLinkSegmentType;
import org.goplanit.utils.network.virtual.ConnectoidSegment;
import org.locationtech.jts.geom.Point;

import java.io.Serializable;

/**
 * A centroid is a singular spatial location in a zone that is deemed most representative if the zone's geometry were to be condensed into a single point
 *
 * @author markr
 *
 */
public interface Centroid extends ManagedId, Serializable {

  /** id class for generating ids */
  public static final Class<Centroid> CENTROID_ID_CLASS = Centroid.class;

  /**
   * {@inheritDoc}
   */
  @Override
  public default Class<? extends Centroid> getIdClass() {
    return CENTROID_ID_CLASS;
  }

  /**
   * Set the parent zone of this centroid
   *
   * @param parent zone of this centroid
   */
  public abstract void setParentZone(Zone parent);

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
  public abstract Centroid shallowClone();

  /**
   * {@inheritDoc}
   */
  public abstract Centroid deepClone();

  /**
   * Position of the centroid
   *
   * @return the position
   */
  public abstract Point getPosition();

  /**
   * Set the Position of the centroid
   *
   * @param position to use
   */
  public abstract void setPosition(Point position);

  /**
   * Verify if position is available for centroid
   *
   * @return true when available, false otherwise
   */
  public default boolean hasPosition(){
    return getPosition()!= null;
  }
}
