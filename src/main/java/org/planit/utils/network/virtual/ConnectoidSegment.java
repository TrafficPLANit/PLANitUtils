package org.planit.utils.network.virtual;

import org.planit.utils.graph.EdgeSegment;

/** Connectoid segment represents a directional virtual segment connecting a centroid and a physical node.
 * It has a unique id across all connectoid segments
 * 
 * @author markr
 *
 */
public interface ConnectoidSegment extends EdgeSegment{

	/** Collect the unqiue connectoid segment id
	 * @return
	 */
	int getConnectoidSegmentId();

}