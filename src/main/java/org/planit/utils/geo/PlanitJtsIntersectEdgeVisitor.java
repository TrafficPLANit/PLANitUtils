package org.planit.utils.geo;

import java.util.Collection;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;
import org.planit.utils.graph.Edge;

/**
 * An item visitor for quadtree spatial index to filter out the edges in a quadtree that truly intersect with the
 * given bounding box provided in the query. Since spatial granularity of the "squares" in the quadtree might be too coarse 
 * it may select a too large a set of matches for any given bounding box. By providing this visitor we explicitly check the provided subset
 * if it indeed intersects with the given filer, i.e., bounding box that we are searching for.
 * 
 * @author markr
 *
 */
public class PlanitJtsIntersectEdgeVisitor<T extends Edge> extends PlanitJtsItemVisitor<T>{

    /** Constructor
     * 
     * @param geometryFilter
     * @param filteredResultToPopulate
     */
    public PlanitJtsIntersectEdgeVisitor(Polygon geometryFilter, Collection<T> filteredResultToPopulate) {
      super(geometryFilter, filteredResultToPopulate);        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Geometry getGeometry(T edge) {
      if(edge.getExternalId()==String.valueOf(197882870l)) {
        int bla = 4;
      }
      return edge.getGeometry();
    }
}
