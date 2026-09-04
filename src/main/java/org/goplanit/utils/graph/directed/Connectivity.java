package org.goplanit.utils.graph.directed;

/**
 * The notion of connectivity applied when partitioning a directed graph into subgraphs.
 * <p>
 * The distinction only exists for directed graphs, since it is direction that allows two vertices to be joined
 * without being mutually reachable.
 * </p>
 *
 * @author markr
 */
public enum Connectivity {

  /**
   * Two vertices belong together when a path exists between them while ignoring direction.
   * <p>
   * The cheaper notion, and the right one when the question is whether infrastructure is attached to the network
   * at all. It considers a one way trap perfectly healthy: a car park reachable only by a service road pointing
   * outwards is weakly connected to the network, yet nothing can ever drive into it.
   * </p>
   */
  WEAK,

  /**
   * Two vertices belong together only when each can reach the other while following direction.
   * <p>
   * The notion routing needs, since a route has to exist in both directions for a location to serve as both an
   * origin and a destination. Strictly finer than {@link #WEAK}: every strongly connected component lies wholly
   * within one weakly connected component.
   * </p>
   */
  STRONG;

  /**
   * Verify whether this is the strong notion
   *
   * @return true when strong, false otherwise
   */
  public boolean isStrong() {
    return this == STRONG;
  }
}
