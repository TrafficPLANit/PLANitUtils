package org.goplanit.utils.network.layer.modifier.event;

import org.goplanit.utils.event.EventImpl;
import org.goplanit.utils.network.layer.macroscopic.MacroscopicLinkSegmentType;
import org.goplanit.utils.network.layer.modifier.MacroscopicNetworkLayerModifier;

/**
 * Event for when the layer modifier removes a link segment type, once no mode may use it and the link segments carrying
 * it are gone
 * <p>
 * Whatever refers to a type by itself, rather than through a link segment, would otherwise keep a type the network no
 * longer has: for example a pricing scheme charging on every motorway, or a section given by type, which a writer would
 * then write with a type no network file holds. Removing the link segments announces nothing about their type, so the
 * type's removal is announced on its own, as a change of a link segment's type is.
 * </p>
 *
 * @author markr
 */
public class RemoveLinkSegmentTypeEvent extends EventImpl implements LinkSegmentTypeModificationEvent {

  /** event type fired off when a link segment type has been removed */
  public static final LinkSegmentTypeModifierEventType EVENT_TYPE =
      new LinkSegmentTypeModifierEventType("MACROSCOPICNETWORKLAYERMODIFIER.LINKSEGMENTTYPE.REMOVE");

  /**
   * Constructor
   *
   * @param source modifier of the layer the type was part of
   * @param removedLinkSegmentType the type removed
   */
  public RemoveLinkSegmentTypeEvent(
      MacroscopicNetworkLayerModifier source,
      MacroscopicLinkSegmentType removedLinkSegmentType) {
    super(EVENT_TYPE, source, new Object[] { removedLinkSegmentType });
  }

  /**
   * The link segment type removed
   *
   * @return the removed type
   */
  public MacroscopicLinkSegmentType getRemovedLinkSegmentType() {
    return (MacroscopicLinkSegmentType) getContent()[0];
  }
}
