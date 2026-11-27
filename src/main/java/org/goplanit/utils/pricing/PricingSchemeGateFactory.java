package org.goplanit.utils.pricing;

import org.goplanit.utils.id.ManagedIdEntityFactory;
import org.goplanit.utils.network.layer.macroscopic.MacroscopicLinkSegment;

import java.util.Collection;

/**
 * Registers gates on the gates of a network pricing
 * <p>
 * Gates are registered before the scheme they belong to, as an entry-exit scheme is made with its gates, so the
 * pricing reader registers each {@code <gate>} through it and hands the gates to the scheme's factory.
 * </p>
 *
 * @author markr
 */
public interface PricingSchemeGateFactory extends ManagedIdEntityFactory<PricingSchemeGate> {

  /**
   * Create a gate and register it on its container
   *
   * @param entryLinkSegments the scheme's link segments entered at the gate, may be empty
   * @param exitLinkSegments the scheme's link segments left at the gate, may be empty
   * @return the registered gate
   * @throws IllegalArgumentException when both are empty, as a gate is an entry, an exit, or both
   */
  public abstract PricingSchemeGate registerNew(Collection<MacroscopicLinkSegment> entryLinkSegments,
      Collection<MacroscopicLinkSegment> exitLinkSegments);
}
