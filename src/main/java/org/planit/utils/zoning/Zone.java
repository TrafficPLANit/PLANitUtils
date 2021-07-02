package org.planit.utils.zoning;

import java.util.logging.Logger;

import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.planit.utils.id.ExternalIdAble;

/**
 * Zone represent a geographical area with a centroid which in turn represent the
 * single point of departure of all traffic in the zone
 * 
 * @author markr
 *
 */
public interface Zone extends ExternalIdAble {
  
  /** the logger */
  public static final Logger LOGGER = Logger.getLogger(Zone.class.getCanonicalName());

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
   * Returns the centroid of this zone
   * 
   * @return centroid of this zone
   */
  public abstract Centroid getCentroid();
  
  /** set the geometry (outer border) of this zone, can be a polygon, but also a line string
   * 
   * @param geometry of the zone
   */
  public abstract void setGeometry(Geometry geometry);  
  
  /** Collect the geometry (outer border) of this zone
   * 
   * @return geometry of the zone
   */
  public abstract Geometry getGeometry();
  
  /** Name of the zone 
   * 
   * @param name of the zone
   */
  public abstract void setName(String name);
  
  /** Name of the zone 
   * 
   * @return name of the zone
   */
  public abstract String getName();
  
  /** verify if the zone has a geometry
   * 
   * @return true if available, false otherwise
   */
  public default boolean hasGeometry() {
    return getGeometry() != null;
  }
  
  /** verify if centroid is present
   * 
   * @return true when present, false otherwise
   */
  public default boolean hasCentroid() {
    return getCentroid() != null;
  }
  
  /** verify if a name has been set
   * @return true when present, false otherwise
   */
  public default boolean hasName() {
    return getName() != null && !getName().isBlank();
  }
  
  /** Verify if input property exists
   * @param key to check
   * @return truw when present, false otherwise
   */
  public default boolean hasInputProperty(String key) {
    return getInputProperty(key) != null;
  }
  
  /** collect the envelope (bounding box) of this zone's geometry. In case, the zone has no geometry
   * we revert to using the geometry (location) of its centroid.
   * 
   * @return envelope of the zone
   */
  public default Envelope getEnvelope() {
    if(hasGeometry() && !getGeometry().isEmpty()) {
      return getGeometry().getEnvelope().getEnvelopeInternal();
    }else if(hasCentroid() && getCentroid().hasPosition()) {
      return getCentroid().getPosition().getEnvelopeInternal();
    }else {
      LOGGER.warning(String.format("zone (id:%s) has no valid geometry to collect envelope (bounding box) for",getXmlId()));
    }
    return null;    
  }
    

}
