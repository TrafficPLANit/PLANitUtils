package org.goplanit.utils.pricing;

import org.goplanit.utils.id.ManagedIdEntities;
import org.goplanit.utils.network.layer.physical.LinkSegment;

import java.util.List;

/**
 * The gates of a network pricing's entry-exit schemes, found by their entry and exit link segments
 * <p>
 * Held by the pricing rather than by each scheme, so their ids are unique across the pricing file. A gate whose entry
 * or exit the network removes is found without going over all of them.
 * </p>
 *
 * @author markr
 */
public interface PricingSchemeGates extends ManagedIdEntities<PricingSchemeGate> {

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract PricingSchemeGateFactory getFactory();

  /**
   * The registered gates entered or left along a link segment, of any scheme
   *
   * @param linkSegment to look up
   * @return the gates, empty when none is entered or left along it, unmodifiable and a copy: later changes to the
   *     container do not show in it
   */
  public abstract List<PricingSchemeGate> getByLinkSegment(LinkSegment linkSegment);

  /**
   * The registered gates entered along a link segment, of any scheme, such as the gates a route search enters as it
   * takes the link segment
   *
   * @param linkSegment to look up
   * @return the gates, empty when none is entered along it, unmodifiable and a copy: later changes to the container do
   *     not show in it
   */
  public abstract List<PricingSchemeGate> getByEntryLinkSegment(LinkSegment linkSegment);

  /**
   * The registered gates left along a link segment, of any scheme
   *
   * @param linkSegment to look up
   * @return the gates, empty when none is left along it, unmodifiable and a copy: later changes to the container do
   *     not show in it
   */
  public abstract List<PricingSchemeGate> getByExitLinkSegment(LinkSegment linkSegment);
}
