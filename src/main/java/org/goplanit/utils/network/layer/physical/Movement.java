package org.goplanit.utils.network.layer.physical;

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


  /** collect link segment from, i.e., the incoming leg of the movement
   *
   * @return link segment from
   */
  public abstract LinkSegment getLinkSegmentFrom();
  
  /** verify if linkSegment from is present
   * 
   * @return true when present, false otherwise
   */
  public default boolean hasLinkSegmentFrom() {
    return getLinkSegmentFrom() != null;
  }

  /** collect link segment to, i.e., the outgoing leg of the movement
   *
   * @return link segment to
   */
  public abstract LinkSegment getLinkSegmentTo();

  /** verify if linkSegment to is present
   *
   * @return true when present, false otherwise
   */
  public default boolean hasLinkSegmentTo() {
    return getLinkSegmentTo() != null;
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
