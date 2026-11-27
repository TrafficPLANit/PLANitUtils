package org.goplanit.utils.pricing;

import org.goplanit.utils.id.ExternalIdAble;
import org.goplanit.utils.id.ManagedId;
import org.goplanit.utils.network.layer.macroscopic.MacroscopicLinkSegment;
import org.goplanit.utils.network.layer.macroscopic.MacroscopicLinkSegmentType;

import java.io.Serializable;
import java.util.List;
import org.goplanit.utils.containers.ListUtils;
import org.goplanit.utils.network.layer.physical.LinkSegment;

/**
 * A section of a scheme: part of the link segments it charges on, with price rules of its own
 * <p>
 * For example a gantry of a road charged per gantry, zone 1 westbound of a road charged per km by zone, or, in a truck
 * charge, the primary roads given by their type. Sections belong to the pricing, never to the network or its toll
 * roads: they are the operator's construct, and each belongs to one scheme. Its external id is the operator's code,
 * such as a gantry's {@code M7-03A}.
 * </p>
 *
 * @author markr
 */
public interface PricingSchemeSection extends ExternalIdAble, ManagedId, Serializable {

  /** id class for generating ids */
  public static final Class<PricingSchemeSection> SCHEME_SECTION_ID_CLASS = PricingSchemeSection.class;

  /**
   * {@inheritDoc}
   */
  @Override
  public default Class<? extends PricingSchemeSection> getIdClass() {
    return SCHEME_SECTION_ID_CLASS;
  }

  /**
   * The name of the section, such as "Zone 1 westbound"
   *
   * @return the name, null when it has none
   */
  public abstract String getName();

  /**
   * Set the name of the section
   *
   * @param name to set, null for none
   */
  public abstract void setName(String name);

  /**
   * The link segments of the section, as given, referenced, not owned: their layer owns them
   *
   * @return unmodifiable list of the link segments, empty when the section is given by type only
   */
  public abstract List<MacroscopicLinkSegment> getLinkSegments();

  /**
   * The link segment types of the section, as given, referenced, not owned: their layer owns them. In a scheme on toll
   * roads they select the roads' link segments of those types
   *
   * @return unmodifiable list of the link segment types, empty when the section is given by link segment only
   */
  public abstract List<MacroscopicLinkSegmentType> getLinkSegmentTypes();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract PricingSchemeSection shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract PricingSchemeSection deepClone();

  /**
   * Whether the section holds a link segment: it lists it, or lists its type
   *
   * @param linkSegment to check
   * @return true when the section holds it
   */
  public default boolean contains(LinkSegment linkSegment) {
    return ListUtils.containsByIdentity(getLinkSegments(), linkSegment) ||
        (linkSegment instanceof MacroscopicLinkSegment && ListUtils.containsByIdentity(getLinkSegmentTypes(),
            ((MacroscopicLinkSegment) linkSegment).getLinkSegmentType()));
  }

  /**
   * Whether the section still lists a link segment or a link segment type, as one listing neither holds nothing
   *
   * @return true when it lists either
   */
  public default boolean hasLinkSegmentsOrLinkSegmentTypes() {
    return !getLinkSegments().isEmpty() || !getLinkSegmentTypes().isEmpty();
  }
}
