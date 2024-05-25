package org.goplanit.utils.path;

import org.goplanit.utils.geo.PlanitJtsUtils;
import org.goplanit.utils.graph.EdgeUtils;
import org.goplanit.utils.graph.directed.EdgeSegment;
import org.goplanit.utils.graph.directed.EdgeSegmentUtils;
import org.goplanit.utils.misc.IterableUtils;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * This object represents a simple directed path based on a number of consecutive LinkSegments
 *
 * @author markr
 *
 */
public class SimpleDirectedPathImpl implements SimpleDirectedPath {

  /** the logger */
  @SuppressWarnings("unused")
  private static final Logger LOGGER = Logger.getLogger(SimpleDirectedPathImpl.class.getCanonicalName());

  /**
   * deque containing the edge segments in the path (we use a deque for easy insertion at both ends which is generally preferable when constructing paths based on shortest path
   * algorithms. Access is less of an issue as we only allow one to iterate
   */
  private final Deque<EdgeSegment> path;

  /**
   * Constructor
   */
  public SimpleDirectedPathImpl() {
    super();
    path = new ArrayDeque<>();
  }

  /**
   * Copy constructor (shallow copy)
   *
   * @param other to copy
   */
  public SimpleDirectedPathImpl(SimpleDirectedPathImpl other) {
    super();
    path = new ArrayDeque<>(other.path);
  }

  /**
   * Constructor
   *
   * @param pathEdgeSegments the path to set (not copied)
   */
  @SuppressWarnings("unchecked")
  public SimpleDirectedPathImpl(final Deque<? extends EdgeSegment> pathEdgeSegments) {
    super();
    path = (Deque<EdgeSegment>) pathEdgeSegments;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Iterator<EdgeSegment> iterator() {
    return path.iterator();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public long size() {
    return path.size();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean containsSubPath(Collection<? extends EdgeSegment> subPath) {
    return containsSubPath(subPath.iterator());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean containsSubPath(Iterator<? extends EdgeSegment> subPathIter) {
    return PathUtils.containsSubPath(iterator(), subPathIter);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public EdgeSegment getFirstSegment() {
    return path.getFirst();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public EdgeSegment getLastSegment() {
    return path.getLast();
  }

  /**
   * Append given edge segments to the simple path
   *
   * @param edgeSegments to add
   */
  public void append(EdgeSegment... edgeSegments){
    this.path.addAll(Arrays.asList(edgeSegments));
  }

  /**
   * Prepend given edge segments to the simple path
   *
   * @param edgeSegments to add
   */
  public void prepend(EdgeSegment... edgeSegments){
    Arrays.stream(edgeSegments).forEach(this.path::push);
  }

  /**
   * Hash code  taken over all edge segments
   *
   * @return hashcode
   */
  @Override
  public int hashCode(){
    return StreamSupport.stream(spliterator(), false).collect(Collectors.toList()).hashCode();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Geometry createGeometry() {
    return EdgeSegmentUtils.createGeometryFrom(iterator());
  }

}
