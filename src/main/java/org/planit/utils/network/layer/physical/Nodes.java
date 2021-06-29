package org.planit.utils.network.layer.physical;

import org.planit.utils.graph.Vertices;

/**
 * Wrapper around vertices interface to support Nodes explicitly rather than vertices
 * 
 * @author markr
 *
 * @param <N> node type
 */
public interface Nodes<N extends Node> extends Vertices<N> {

}
