package org.planit.utils.network.physical;

import org.planit.utils.graph.DirectedGraphBuilder;

/**
 * Builder for physical networks of given parameterised types.
 * 
 * @author markr
 *
 */
public interface PhysicalNetworkBuilder<N extends Node, L extends Link, LS extends LinkSegment> extends DirectedGraphBuilder<N, L, LS> {

}
