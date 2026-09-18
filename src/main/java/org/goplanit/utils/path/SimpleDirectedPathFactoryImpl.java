package org.goplanit.utils.path;

import org.goplanit.utils.graph.directed.EdgeSegment;

import java.util.Deque;

/**
 * Factory for creating simple directed paths
 * 
 * @author markr
 */
public class SimpleDirectedPathFactoryImpl implements DirectedPathFactory<SimpleDirectedPath> {

  /**
   * {@inheritDoc}
   */
  @Override
  public SimpleDirectedPathImpl createNew() {
    return new SimpleDirectedPathImpl();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SimpleDirectedPathImpl createNew(Deque<? extends EdgeSegment> edgeSegments) {
    return new SimpleDirectedPathImpl(edgeSegments);
  }

}
