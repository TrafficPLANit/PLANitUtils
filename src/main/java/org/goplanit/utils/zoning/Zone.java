package org.goplanit.utils.zoning;

import java.util.logging.Logger;

import org.goplanit.utils.id.ExternalIdAble;
import org.goplanit.utils.id.ManagedId;
import org.goplanit.utils.misc.StringUtils;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;

/**
 * Zone represent a geographical area with a centroid which in turn represent the
 * single point of departure of all traffic in the zone
 * 
 * @author markr
 *
 */
public interface Zone extends ExternalIdAble, ManagedId {
  
  /** the logger */
  public static final Logger LOGGER = Logger.getLogger(Zone.class.getCanonicalName());
  
  /** the class to use for the id generation */
  public static final Class<Zone> ZONE_ID_CLASS = Zone.class;  

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

  /** Collect the geometry of this zone and allow it to return its internal centroid location in case it has no
   * geometry of its own by means of provided flag.
   *
   * @param considerCentroid when false centroid geometry is ignored, when true it is returned in case no explicit geoemetry is set
   * @return geometry of the zone
   */
  public default Geometry getGeometry(boolean considerCentroid){
    var geometry = getGeometry();
    if(considerCentroid && geometry == null && getCentroid() != null && getCentroid().hasPosition()){
      geometry = getCentroid().getPosition();
    }
    return geometry;
  }
  
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
  
  /**
   * {@inheritDoc}
   */
  @Override
  public abstract Zone clone();
  
  /**
   * {@inheritDoc}
   */
  @Override
  public default Class<Zone> getIdClass() {
    return ZONE_ID_CLASS;
  }
  
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
  
  /** Verify if a name has been set
   * 
   * @return true when present, false otherwise
   */
  public default boolean hasName() {
    return !StringUtils.isNullOrBlank(getName());
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
