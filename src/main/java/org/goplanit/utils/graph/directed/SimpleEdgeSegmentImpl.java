package org.goplanit.utils.graph.directed;

import org.goplanit.utils.id.ExternalIdAbleImpl;
import org.goplanit.utils.id.IdGenerator;
import org.goplanit.utils.id.IdGroupingToken;

/**
 * SimpleEdgeSegmentImpl represents a directed edge. It is a simple implementation to allow for testing within this package.
 *
 * @author markr
 *
 */
public class SimpleEdgeSegmentImpl extends ExternalIdAbleImpl implements EdgeSegment {

  /**
   * Store the direction of this edge segment in relation to its parent edge
   */
  private boolean directionAb;

  /**
   * segment's parent edge
   */
  private DirectedEdge parentEdge;

  // Protected

  /**
   * Constructor
   *
   * @param parentEdge  parent edge of segment
   * @param directionAb direction of travel
   */
  public SimpleEdgeSegmentImpl(final DirectedEdge parentEdge, final boolean directionAb) {
    super(IdGenerator.generateId(IdGroupingToken.collectGlobalToken(), EDGE_SEGMENT_ID_CLASS));
    setParent(parentEdge);
    this.directionAb = directionAb;
  }

  /**
   * Copy constructor
   *
   * @param edgeSegmentImpl to copy
   * @param deepCopy when true, create a deep copy, shallow copy otherwise
   */
  public SimpleEdgeSegmentImpl(SimpleEdgeSegmentImpl edgeSegmentImpl, boolean deepCopy) {
    super(IdGenerator.generateId(IdGroupingToken.collectGlobalToken(), EDGE_SEGMENT_ID_CLASS));
    setParent(edgeSegmentImpl.getParent());
    this.directionAb = edgeSegmentImpl.directionAb;
  }

  // Public

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isDirectionAb() {
    return this.directionAb;
  }

  // Getter - Setters

  /**
   * {@inheritDoc}
   */
  @Override
  public DirectedEdge getParent() {
    return this.parentEdge;
  }

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  @Override
  public void setParent(DirectedEdge parentEdge) {
    this.parentEdge = parentEdge;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void removeParentEdge() {
    this.parentEdge = null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SimpleEdgeSegmentImpl shallowClone() {
    return new SimpleEdgeSegmentImpl(this, false);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SimpleEdgeSegmentImpl deepClone() {
    return new SimpleEdgeSegmentImpl(this, true);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean validate() {
    // simple edge segment does not validate yet, always true for now
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public long recreateManagedIds(IdGroupingToken tokenId) {
    return IdGenerator.generateId(tokenId, EDGE_SEGMENT_ID_CLASS);
  }
}

