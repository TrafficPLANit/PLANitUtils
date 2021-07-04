package org.planit.utils.network.layer.service;

import org.planit.utils.graph.directed.EdgeSegments;

/**
 * Wrapper around EdgeSegments interface to support ServiceLegSegments explicitly rather than EdgeSegments
 * 
 * @author markr
 *
 * @param <LS> leg segment type
 */
public interface ServiceLegSegments<LS extends ServiceLegSegment> extends EdgeSegments<LS> {
    

}
