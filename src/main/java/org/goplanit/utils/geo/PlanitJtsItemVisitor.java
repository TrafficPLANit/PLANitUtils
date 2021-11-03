package org.goplanit.utils.geo;

import java.util.Collection;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.index.ItemVisitor;

/**
 * An item visitor for quadtree spatial index to filter out planit entities in a quadtree that truly intersect with the
 * given bounding box provided in the query. Since spatial granularity of the "squares" in the quadtree might be too coarse 
 * it may select a too large a set of matches for any given bounding box. By providing this visitor we explicitly check the provided subset
 * if it indeed intersects with the given filer, i.e., bounding box that we are searching for.
 * 
 * @author markr
 *
 */
public abstract class PlanitJtsItemVisitor<T> implements ItemVisitor{

    /** result to populate */  
    private Collection<T> filteredResultToPopulate;
    
    /** filter to apply, i.e., the area to filter intersection test on */
    private Polygon geometryFilter;
    
    /**
     * The implementing class is expected to provide the envelope for the entity
     * 
     * @param planitEntity to extract envelope for
     * @return envelope of the entity to match the filter
     */
    protected abstract Geometry getGeometry(T planitEntity);

    /** Constructor
     * 
     * @param geometryFilter to use
     * @param filteredResultToPopulate to populate
     */
    public PlanitJtsItemVisitor(Polygon geometryFilter, Collection<T> filteredResultToPopulate) {
      this.geometryFilter = geometryFilter;
      this.filteredResultToPopulate = filteredResultToPopulate;
        
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void visitItem(Object planitEntity) {
      if(getGeometry((T)planitEntity).intersects(geometryFilter)){
        filteredResultToPopulate.add((T)planitEntity); 
      }
    }

    /** Collect the filtered result created by the visitor
     *  
     * @return populated container
     */
    public Collection<T> getResult() {
      return filteredResultToPopulate;
    }
}
