package org.goplanit.utils.graph.directed.acyclic;

import org.goplanit.utils.graph.directed.ConjugateDirectedEdge;
import org.goplanit.utils.graph.directed.ConjugateDirectedVertex;
import org.goplanit.utils.graph.directed.ConjugateEdgeSegment;

/**
 * A conjugate acyclic sub graph contains a subset of a graph without cycles. The active subset is tracked by
 * registering its conjugate edge segments, which are directed by definition.
 * <p>
 * A topological sort on the current state of the conjugate graph allows fast traversal of the conjugate graph for
 * various algorithms (shortest path). It also reveals if the graph is still acyclic.
 * </p>
 *
 * @author markr
 *
 */
public interface ConjugateACyclicSubGraph extends
    UntypedACyclicSubGraph<ConjugateDirectedVertex, ConjugateDirectedEdge, ConjugateEdgeSegment> {

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConjugateACyclicSubGraph shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConjugateACyclicSubGraph deepClone();
}
