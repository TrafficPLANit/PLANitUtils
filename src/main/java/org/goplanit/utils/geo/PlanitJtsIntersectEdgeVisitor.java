package org.goplanit.utils.geo;

import java.util.Collection;

import org.goplanit.utils.graph.Edge;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;

/**
 * An item visitor for a quadtree spatial index, keeping the edges in the quadtree that truly intersect the bounding box
 * of the query. The "squares" of the quadtree may be too coarse and select too many matches for a bounding box, so
 * this visitor checks each one against the filter, i.e., the bounding box searched for.
 * 
 * @author markr
 *
 */
public class PlanitJtsIntersectEdgeVisitor<T extends Edge> extends PlanitJtsItemVisitor<T>{

    /** Constructor
     * 
     * @param geometryFilter to use
     * @param filteredResultToPopulate to populate
     */
    public PlanitJtsIntersectEdgeVisitor(Polygon geometryFilter, Collection<T> filteredResultToPopulate) {
      super(geometryFilter, filteredResultToPopulate);        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Geometry getGeometry(T edge) {
      return edge.getGeometry();
    }
}
