package org.planit.utils.network.physical;

import org.planit.utils.graph.Vertices;

/**
 * wrapper around vertices interface to support Nodes explicitly rather than vertices
 * 
 * @author markr
 *
 * @param <N> node type
 */
public interface Nodes<N extends Node> extends Vertices<N>, Iterable<N> {

}
