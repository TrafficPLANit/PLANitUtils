package org.goplanit.utils.geo;

import java.util.Collection;
import java.util.logging.Logger;

import org.goplanit.utils.zoning.Zone;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;

/**
 * An item visitor for a quadtree spatial index, keeping the zones in the quadtree that truly intersect the bounding box
 * of the query. The "squares" of the quadtree may be too coarse and select too many matches for a bounding box, so
 * this visitor checks each one against the filter, i.e., the bounding box searched for.
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
