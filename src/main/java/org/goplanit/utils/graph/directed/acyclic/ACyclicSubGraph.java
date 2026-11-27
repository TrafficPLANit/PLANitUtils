package org.goplanit.utils.graph.directed.acyclic;

import org.goplanit.utils.graph.Edge;
import org.goplanit.utils.graph.directed.DirectedEdge;
import org.goplanit.utils.graph.directed.DirectedVertex;
import org.goplanit.utils.graph.directed.EdgeSegment;

/**
 * An acyclic sub graph contains a subset of the full graph without cycles. The active subset is tracked by registering
 * its edge segments, which are directed by definition.
 * <p>
 * A topological sort on the current state of the graph allows fast traversal of the graph for various algorithms
 * (shortest path). It also reveals if the graph is still acyclic.
 * </p>
 *
 * @author markr
 *
 */
public interface ACyclicSubGraph extends UntypedACyclicSubGraph<DirectedVertex, DirectedEdge, EdgeSegment> {

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ACyclicSubGraph shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ACyclicSubGraph deepClone();
}
