package org.planit.utils.network.virtual;

import org.planit.utils.graph.Vertex;

/**
 * A centroid is a special type of vertex representing the location of departure/arrival of traffic in
 * a zone
 * 
 * @author markr
 *
 */
public interface Centroid extends Vertex {

	/**
	 * Return the parent zone of this centroid
	 * 
	 * @return parent zone of this centroid
	 */
	Zone getParentZone();

}