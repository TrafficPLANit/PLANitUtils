package org.goplanit.utils.graph.directed;

import org.goplanit.utils.graph.GraphEntity;

import java.io.Serializable;

/**
 * A Banned movement comprises a combined and ordered traversal of two adjacent edge segments (from, to).
 *
 * @author markr
 *
 */
public interface BannedMovement extends Serializable, GraphEntity {
  
  /** id class for generating ids */
  public static final Class<BannedMovement> BANNED_MOVEMENT_ID_CLASS = BannedMovement.class;

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
   * Set from segment
   * @param segment to set
   */
  public void setSegmentFrom(EdgeSegment segment);

  /**
   * Set to segment
   * @param segment to set
   */
  public void setSegmentTo(EdgeSegment segment);

  /**
   * Get the vertex in the centre of the movement connecting the two edge segments
   *
   * @return centre-vertex
   */
  public default DirectedVertex getCentreVertex(){
    return getSegmentFrom().getDownstreamVertex();
  }

  /**
   * {@inheritDoc}
   */
  public abstract BannedMovement shallowClone();

  /**
   * {@inheritDoc}
   */
  public abstract BannedMovement deepClone();
  }
