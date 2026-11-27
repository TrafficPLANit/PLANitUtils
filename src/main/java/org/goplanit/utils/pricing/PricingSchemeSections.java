package org.goplanit.utils.pricing;

import org.goplanit.utils.id.ManagedIdEntities;
import org.goplanit.utils.network.layer.physical.LinkSegment;

import java.util.List;

/**
 * The sections of a network pricing's schemes, found by the link segments they list
 * <p>
 * Held by the pricing rather than by each scheme, so their ids are unique across the pricing file and a section is
 * referable from anywhere in it. A section listing a link segment the network removes is found without going over all
 * of them. Sections given by type list no link segments and are found through their scheme instead.
 * </p>
 *
 * @author markr
 */
public interface PricingSchemeSections extends ManagedIdEntities<PricingSchemeSection> {

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract PricingSchemeSectionFactory getFactory();

  /**
   * The registered sections listing a link segment, of any scheme
   *
   * @param linkSegment to look up
   * @return the sections, empty when none lists it, unmodifiable and a copy: later changes to the container do not show
   *     in it
   */
  public abstract List<PricingSchemeSection> getByLinkSegment(LinkSegment linkSegment);
}
