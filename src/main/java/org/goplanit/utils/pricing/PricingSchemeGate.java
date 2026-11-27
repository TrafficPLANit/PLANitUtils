package org.goplanit.utils.pricing;

import org.goplanit.utils.id.ExternalIdAble;
import org.goplanit.utils.id.ManagedId;
import org.goplanit.utils.network.layer.macroscopic.MacroscopicLinkSegment;

import java.io.Serializable;
import java.util.List;

/**
 * A gate of an entry-exit scheme: an entry and exit point the operator prices, usually an interchange with several
 * ramps
 * <p>
 * For example the central interchange of a closed motorway, entered along one ramp and left along two. Its entries and
 * exits are link segments the scheme charges on, where they are entered and left, mirroring the XML's
 * {@code entrylsrefs} and {@code exitlsrefs}; they are not a node's entry and exit link segments. Gates belong to the
 * pricing, each to one scheme. Its external id is the operator's code, such as the junction number.
 * </p>
 *
 * @author markr
 */
public interface PricingSchemeGate extends ExternalIdAble, ManagedId, Serializable {

  /** id class for generating ids */
  public static final Class<PricingSchemeGate> SCHEME_GATE_ID_CLASS = PricingSchemeGate.class;

  /**
   * {@inheritDoc}
   */
  @Override
  public default Class<? extends PricingSchemeGate> getIdClass() {
    return SCHEME_GATE_ID_CLASS;
  }

  /**
   * The name of the gate, such as "Central interchange"
   *
   * @return the name, null when it has none
   */
  public abstract String getName();

  /**
   * Set the name of the gate
   *
   * @param name to set, null for none
   */
  public abstract void setName(String name);

  /**
   * The link segments of the scheme entered at the gate, referenced, not owned: their layer owns them
   *
   * @return unmodifiable list of the link segments, empty when the gate is no entry
   */
  public abstract List<MacroscopicLinkSegment> getEntryLinkSegments();

  /**
   * The link segments of the scheme left at the gate, referenced, not owned: their layer owns them
   *
   * @return unmodifiable list of the link segments, empty when the gate is no exit
   */
  public abstract List<MacroscopicLinkSegment> getExitLinkSegments();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract PricingSchemeGate shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract PricingSchemeGate deepClone();

  /**
   * Whether the gate still has a link segment it is entered or left at, as one with neither is passed nowhere
   *
   * @return true when it has either
   */
  public default boolean hasEntryOrExitLinkSegments() {
    return !getEntryLinkSegments().isEmpty() || !getExitLinkSegments().isEmpty();
  }
}
