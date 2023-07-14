package org.goplanit.utils.geo;

import java.util.Collection;
import java.util.logging.Logger;

import org.goplanit.utils.zoning.Zone;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;

/**
 * An item visitor for quadtree spatial index to filter out the zones in a quadtree that truly intersect with the
 * given bounding box provided in the query. Since spatial granularity of the "squares" in the quadtree might be too coarse 
 * it may select a too large a set of matches for any given bounding box. By providing this visitor we explicitly check the provided subset
 * if it indeed intersects with the given filer, i.e., bounding box that we are searching for.
 * 
 * @author markr
 *
 */
public class PlanitJtsIntersectZoneVisitor<T extends Zone> extends PlanitJtsItemVisitor<T>{

  /** the logger to use */
    private static final Logger LOGGER = Logger.getLogger(PlanitJtsIntersectZoneVisitor.class.getCanonicalName());
  
    /** Constructor
     * 
     * @param geometryFilter to use
     * @param filteredResultToPopulate to populate
     */
    public PlanitJtsIntersectZoneVisitor(Polygon geometryFilter, Collection<T> filteredResultToPopulate) {
      super(geometryFilter, filteredResultToPopulate);        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Geometry getGeometry(T zone) {
      
      if(zone.hasGeometry()) {
        return zone.getGeometry();
      }else if(zone.hasCentroid() && zone.getCentroid().hasPosition()) {
        return zone.getCentroid().getPosition();
      }else {
        LOGGER.severe(String.format("Zone %s has no geometry information available",zone.getXmlId()));
      }
      return null;
    }

}
