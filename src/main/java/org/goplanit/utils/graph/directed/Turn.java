package org.goplanit.utils.graph.directed;

/**
 * A turn comprises an ordered traversal from one edge segment (from) onto another (to), at the node they share. A
 * turn is in effect a non-spatial movement.
 *
 * @author markr
 *
 */
public interface Turn {

  /** collect edge segment from, i.e., the incoming leg of the turn
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

  /** collect edge segment to, i.e., the outgoing leg of the turn
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
   * Verify if the turn has a centre vertex, i.e., both edge segments are present and the from segment ends where
   * the to segment starts
   *
   * @return true when present, false otherwise
   */
  public default boolean hasCentreVertex() {
    return hasSegmentFrom() && hasSegmentTo() && getSegmentFrom().getDownstreamVertex() != null &&
        getSegmentFrom().getDownstreamVertex().equals(getSegmentTo().getUpstreamVertex());
  }

  /**
   * Get the vertex in the centre of the turn connecting the two edge segments
   *
   * @return centre-vertex, null when the turn has none, see {@link #hasCentreVertex()}
   */
  public default DirectedVertex getCentreVertex(){
    return hasCentreVertex() ? getSegmentFrom().getDownstreamVertex() : null;
  }
}
