package org.planit.utils.network.layer.physical;

import org.planit.utils.graph.EdgeSegments;

/**
 * wrapper around EdgeSegments interface to support LinkSegments explicitly rather than EdgeSegments
 * 
 * @author markr
 *
 * @param <LS> link segment type
 */
public interface LinkSegments<LS extends LinkSegment> extends EdgeSegments<LS> {
    
}
