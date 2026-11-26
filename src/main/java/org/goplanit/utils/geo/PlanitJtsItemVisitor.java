package org.goplanit.utils.geo;

import java.util.Collection;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.index.ItemVisitor;

/**
 * An item visitor for a quadtree spatial index, keeping the planit entities in the quadtree that truly intersect the
 * bounding box of the query. The "squares" of the quadtree may be too coarse and select too many matches for a
 * bounding box, so this visitor checks each one against the filter, i.e., the bounding box searched for.
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
