package org.goplanit.utils.path;

import org.goplanit.utils.geo.PlanitJtsUtils;
import org.goplanit.utils.graph.EdgeUtils;
import org.goplanit.utils.graph.directed.EdgeSegment;
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
    if (subPathIter == null && subPathIter.hasNext()) {
      return false;
    }

    EdgeSegment subPathSegment = subPathIter.next();
    boolean started = false;
    for (EdgeSegment edgeSegment : path) {
      if (started){
        subPathSegment = subPathIter.next();
      }

      if (edgeSegment.idEquals(subPathSegment)) {
        started = true;
      } else if (started) {
        started = false;
        break;
      }

      if (!subPathIter.hasNext()) {
        break;
      }
    }

    return started && !subPathIter.hasNext();
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
    Arrays.stream(edgeSegments).forEach(e -> this.path.add(e));
  }

  /**
   * Prepend given edge segments to the simple path
   *
   * @param edgeSegments to add
   */
  public void prepend(EdgeSegment... edgeSegments){
    Arrays.stream(edgeSegments).forEach(e -> this.path.push(e));
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
    if(path == null || path.isEmpty()){
      return null;
    }

    List<LineString> linkGeometries = new ArrayList<>(this.path.size());
    for(var es : this){
      if(!es.hasParent()){
        LOGGER.warning(String.format("Found path where one or more links have no geometry, path geometry incomplete"));
        break;
      }
      LineString result =
        es.getParent().hasGeometry() ? es.getParent().getGeometry() : EdgeUtils.createLineStringFromVertexLocations(es.getParent(), es.isDirectionAb());
      if(result == null){
        continue;
      }

      linkGeometries.add(result);
    }

    if(linkGeometries.isEmpty()){
      LOGGER.warning(String.format("Found path where no links have a geometry nor their nodes, path geometry unavailable"));
      return null;
    }

    if(linkGeometries.size() < path.size()){
      LOGGER.warning(String.format("Found path where one or more links have no geometry or node positions, path geometry incomplete"));
    }

    // deduplicate shared vertices between link (segments) when concatenating
    return PlanitJtsUtils.createCopyWithoutAdjacentDuplicateCoordinates(PlanitJtsUtils.concatenate(linkGeometries.toArray(LineString[]::new)));
  }

}
