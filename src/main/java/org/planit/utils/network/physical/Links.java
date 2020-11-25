package org.planit.utils.network.physical;

import org.planit.utils.graph.Edges;

/**
 * wrapper around edges interface to support Links explicitly rather than edges
 * 
 * @author markr
 *
 * @param <L>
 */
public interface Links<L extends Link> extends Edges<L> {
  

}
