package org.goplanit.utils.pricing;

import org.goplanit.utils.id.ManagedIdEntityFactory;
import org.goplanit.utils.network.layer.macroscopic.MacroscopicLinkSegment;
import org.goplanit.utils.network.layer.macroscopic.MacroscopicLinkSegmentType;

import java.util.Collection;

/**
 * Registers sections on the sections of a network pricing
 * <p>
 * Sections are registered before the scheme they belong to, as a scheme is made with its sections, so the pricing
 * reader registers each {@code <section>} through it and hands the sections to the scheme's factory.
 * </p>
 *
 * @author markr
 */
public interface PricingSchemeSectionFactory extends ManagedIdEntityFactory<PricingSchemeSection> {

  /**
   * Create a section and register it on its container
   *
   * @param linkSegments the section's link segments, as given, may be empty when it is given by type
   * @param linkSegmentTypes the section's link segment types, may be empty when it is given by link segment
   * @return the registered section
   */
  public abstract PricingSchemeSection registerNew(Collection<MacroscopicLinkSegment> linkSegments,
      Collection<MacroscopicLinkSegmentType> linkSegmentTypes);
}
