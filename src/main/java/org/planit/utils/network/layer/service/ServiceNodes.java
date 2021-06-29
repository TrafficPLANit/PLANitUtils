package org.planit.utils.network.layer.service;

import org.planit.utils.graph.Vertices;

/**
 * Wrapper around vertices interface to support ServiceNodes explicitly rather than vertices
 * 
 * @author markr
 *
 * @param <N> service node type
 */
public interface ServiceNodes<N extends ServiceNode> extends Vertices<N> {

}
