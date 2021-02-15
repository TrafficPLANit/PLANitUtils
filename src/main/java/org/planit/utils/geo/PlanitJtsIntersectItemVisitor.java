package org.planit.utils.geo;

import java.util.Collection;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.index.ItemVisitor;
import org.planit.utils.zoning.Zone;

/**
 * An iteme visitor for quadtree spatial index to filter out the zones in a quadtree that truly intersect with the
 * given bounding box provided in the query. Since spatial granularity of the "squares" in the quadtree might be too caorse 
 * it may select a too large a set of matches for any given bounding box. By providing this visitor we explicitly check the provided subset
 * if it indeed intersects with the given filer, i.e., bounding box that we are searching for.
 * 
 * @author markr
 *
 */
public class PlanitJtsIntersectItemVisitor<T extends Zone> implements ItemVisitor{

    /** result to populate */  
    private Collection<T> filteredResultToPopulate;
    
    /** filter to apply, i.e., the bounding box to filter intersection test on */
    private Envelope geometryEnvelopeFilter;

    /** Constructor
     * 
     * @param geometryEnvelopeFilter
     * @param filteredResultToPopulate
     */
    public PlanitJtsIntersectItemVisitor(Envelope geometryEnvelopeFilter, Collection<T> filteredResultToPopulate) {
      this.geometryEnvelopeFilter = geometryEnvelopeFilter;  
      this.filteredResultToPopulate = filteredResultToPopulate;
        
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void visitItem(Object zone) {
      if(geometryEnvelopeFilter.intersects(((T) zone).getEnvelope())){
        filteredResultToPopulate.add((T)zone); 
      }
    }

    /** Collect the filtered result created by the visitor 
     * @return
     */
    public Collection<T> getResult() {
      return filteredResultToPopulate;
    }
}
