package org.goplanit.utils.graph.directed;

/**
 * A movement comprises an ordered traversal from one edge segment (from) onto another (to).
 *
 * @author markr
 *
 */
public interface Movement {

  /** collect edge segment from, i.e., the incoming leg of the movement
   *
   * @return link segment from
   */
  public abstract EdgeSegment getSegmentFrom();

  /** verify if edge Segment from is present
   *
   * @return true when present, false otherwise
   */
  public default boolean hasSegmentFrom() {
    return getSegmentFrom() != null;
  }

  /** collect edge segment to, i.e., the outgoing leg of the movement
   *
   * @return edge segment to
   */
  public abstract EdgeSegment getSegmentTo();

  /** verify if edge Segment to is present
   *
   * @return true when present, false otherwise
   */
  public default boolean hasSegmentTo() {
    return getSegmentTo() != null;
  }

  /**
   * Verify if the movement has a centre vertex, i.e., both edge segments are present and the from segment ends where
   * the to segment starts
   *
   * @return true when present, false otherwise
   */
  public default boolean hasCentreVertex() {
    return hasSegmentFrom() && hasSegmentTo() && getSegmentFrom().getDownstreamVertex() != null &&
        getSegmentFrom().getDownstreamVertex().equals(getSegmentTo().getUpstreamVertex());
  }

  /**
   * Get the vertex in the centre of the movement connecting the two edge segments
   *
   * @return centre-vertex, null when the movement has none, see {@link #hasCentreVertex()}
   */
  public default DirectedVertex getCentreVertex(){
    return hasCentreVertex() ? getSegmentFrom().getDownstreamVertex() : null;
  }
}
