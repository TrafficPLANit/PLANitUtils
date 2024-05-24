package org.goplanit.utils.network.layer.physical;

import org.goplanit.utils.graph.directed.EdgeSegment;
import org.goplanit.utils.id.ExternalIdAble;
import org.goplanit.utils.id.ManagedId;

import java.io.Serializable;

/**
 * A Movement comprises a combined and ordered traversal of two adjacent edge segments (from, to) without itself representing
 * a particular graph entity (unlike a conjugate link segment).
 * 
 * @author markr
 *
 */
public interface Movement extends ExternalIdAble, ManagedId, Serializable {
  
  /** id class for generating ids */
  public static final Class<Movement> MOVEMENT_ID_CLASS = Movement.class;


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
   * {@inheritDoc}
   */
  public abstract Movement shallowClone();

  /**
   * {@inheritDoc}
   */
  public abstract Movement deepClone();
  
}
