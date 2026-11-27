package org.goplanit.utils.pricing;

import org.goplanit.utils.id.ExternalIdAble;
import org.goplanit.utils.id.ManagedId;
import org.goplanit.utils.mode.Mode;
import org.goplanit.utils.network.layer.MacroscopicNetworkLayer;
import org.goplanit.utils.network.layer.macroscopic.MacroscopicLinkSegment;
import org.goplanit.utils.network.layer.macroscopic.tollroad.PricedModeSelection;
import org.goplanit.utils.network.layer.physical.LinkSegment;
import org.goplanit.utils.problem.Problems;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.goplanit.utils.containers.ListUtils;

/**
 * What every pricing scheme has: the modes it charges, the link segments it charges on, its pricing, and its problems
 * <p>
 * A scheme is one charge as an operator or a policy sets it: a truck charge per km on motorways (a mode scheme),
 * WestConnex across four toll roads with one flagfall (a location scheme), or a closed motorway priced by a table of
 * entry and exit gates (an entry-exit scheme). Schemes add up: a truck on WestConnex pays both the truck charge and
 * the toll. Each scheme has an id across all three kinds, for arrays over every scheme, and one within its kind.
 * </p>
 *
 * @author markr
 */
public interface PricingScheme extends ExternalIdAble, ManagedId, Serializable {

  /** id class for generating ids across all kinds of scheme */
  public static final Class<PricingScheme> SCHEME_ID_CLASS = PricingScheme.class;

  /**
   * {@inheritDoc}
   */
  @Override
  public default Class<? extends PricingScheme> getIdClass() {
    return SCHEME_ID_CLASS;
  }

  /**
   * The name of the scheme, such as "WestConnex"
   *
   * @return the name, null when it has none
   */
  public abstract String getName();

  /**
   * The description of the scheme, such as its operator
   *
   * @return the description, null when it has none
   */
  public abstract String getDescription();

  /**
   * Set the description of the scheme
   *
   * @param description to set, null for none
   */
  public abstract void setDescription(String description);

  /**
   * The modes the scheme charges as its file states them: named, exempted, or not given, in which case it charges what
   * its toll roads, or a mode scheme's layers, price. Kept as given so the scheme is written back as read; ask
   * {@link #getChargedModes()} for the modes it charges
   *
   * @return the priced mode selection, as given
   */
  public abstract PricedModeSelection getPricedModeSelection();

  /**
   * The modes the scheme charges, worked out from its priced mode selection when asked, of the modes its toll roads
   * price or its layers support, so they follow changes to the network
   *
   * @return unmodifiable set of the modes charged
   */
  public abstract Set<Mode> getChargedModes();

  /**
   * Whether the scheme charges on a link segment
   *
   * @param linkSegment to check
   * @return true when it is one of the scheme's priced link segments
   */
  public abstract boolean isPricedLinkSegment(LinkSegment linkSegment);

  /**
   * Whether the scheme charges on a link segment of a layer the caller knows, such as a route search within that
   * layer, so no other layer is looked at
   *
   * @param layer the link segment lies in
   * @param linkSegment to check
   * @return true when it is one of the scheme's priced link segments
   */
  public abstract boolean isPricedLinkSegment(MacroscopicNetworkLayer layer, LinkSegment linkSegment);

  /**
   * The link segments the scheme charges on: those of its toll roads, or, for a mode scheme, those its coverage covers
   *
   * @return the priced link segments
   */
  public abstract Stream<MacroscopicLinkSegment> streamPricedLinkSegments();

  /**
   * The priced link segments where traffic comes into the scheme: those with an adjacent upstream link segment the
   * scheme does not charge on, such as the first one and each one an on-ramp joins
   *
   * @return the priced link segments entering the scheme from outside it
   */
  public abstract List<MacroscopicLinkSegment> findPricedLinkSegmentsEnteringFromOutside();

  /**
   * The priced link segments where traffic goes out of the scheme: those with an adjacent downstream link segment the
   * scheme does not charge on, such as the last one and each one an off-ramp leaves
   *
   * @return the priced link segments leaving the scheme to outside it
   */
  public abstract List<MacroscopicLinkSegment> findPricedLinkSegmentsLeavingToOutside();

  /**
   * Whether this scheme and another charge on a link segment both
   *
   * @param other scheme to check against
   * @return true when they share a priced link segment
   */
  public abstract boolean sharesPricedLinkSegmentWith(PricingScheme other);

  /**
   * The section of the scheme a priced link segment lies in: a section listing it, or a section given by its link
   * segment type. For example the section giving the rate a route search charges on it
   *
   * @param linkSegment to look up
   * @return the section, null when it lies in none or is not a priced link segment
   */
  public abstract PricingSchemeSection findSection(LinkSegment linkSegment);

  /**
   * What the scheme charges, of its kind of price rule; each kind of scheme gives it typed
   *
   * @return the pricing
   */
  public abstract SchemePricing<?> getPricing();

  /**
   * The problems of the scheme against its own rules: its time bands, distance bands, overrides, sections and gates
   * <p>
   * The rules across schemes, such as one mode scheme per mode, are checked by the network pricing. Public, so tests
   * and tools can ask a scheme what is wrong with it.
   * </p>
   *
   * @return the problems found, empty when none
   */
  public abstract Problems<PricingProblemKind> findProblems();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract PricingScheme shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract PricingScheme deepClone();

  /**
   * Whether the scheme charges a mode
   *
   * @param mode to check
   * @return true when it is one of the scheme's charged modes
   */
  public default boolean chargesMode(Mode mode) {
    return ListUtils.containsByIdentity(getChargedModes(), mode);
  }
}
