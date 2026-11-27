package org.goplanit.utils.pricing;

/**
 * The problems a pricing scheme can have, each with what happens to the scheme, its outcome
 * <p>
 * Every rule that makes a scheme's price rules ambiguous, or that a scheme breaks in a way that can be fixed, is named
 * here once, as the settings loader names its problems in {@code SettingsProblemKind}. A scheme collects its problems
 * as it is registered and they are logged once, together; a test asks which kinds were found, never the text. For
 * example, two time bands of a scheme whose entries overlap give {@link #TIME_BANDS_OVERLAP}, which refuses the
 * scheme.
 * </p>
 *
 * @author markr
 */
public enum PricingProblemKind {

  /** toll roads of one scheme price different modes */
  TOLL_ROADS_PRICE_DIFFERENT_MODES(PricingProblemOutcome.REFUSED, "Its toll roads price different modes"),

  /** a location or entry-exit scheme charges on no link segment */
  CHARGES_ON_NO_LINK_SEGMENT(PricingProblemOutcome.REFUSED, "It charges on no link segment"),

  /** two sections of a scheme share a link segment */
  SECTIONS_SHARE_LINK_SEGMENT(PricingProblemOutcome.REFUSED, "Two of its sections share a link segment"),

  /** an entry-exit scheme has no gates */
  NO_GATES(PricingProblemOutcome.REFUSED, "It has no gates"),

  /** two gates of a scheme share an entry, or an exit */
  GATES_SHARE_ENTRY_OR_EXIT(PricingProblemOutcome.REFUSED, "Two of its gates share an entry or an exit"),

  /** a mode is in two overrides of one level: of the base, or of one time band */
  MODE_IN_TWO_OVERRIDES(PricingProblemOutcome.REFUSED, "A mode is in two overrides of the base or of one time band"),

  /** entries of two time bands of a scheme overlap */
  TIME_BANDS_OVERLAP(PricingProblemOutcome.REFUSED, "Entries of two of its time bands overlap"),

  /** two distance bands of a scheme overlap */
  DISTANCE_BANDS_OVERLAP(PricingProblemOutcome.REFUSED, "Two of its distance bands overlap"),

  /** the distance bands of a scheme count over different periods */
  DISTANCE_BANDS_MIX_PERIODS(PricingProblemOutcome.REFUSED, "Its distance bands count over different periods"),

  /** a second mode scheme charges a mode another mode scheme charges */
  SECOND_MODE_SCHEME_FOR_MODE(PricingProblemOutcome.REFUSED, "Another mode scheme already charges one of its modes"),

  /** a cap whose amount is lower than a minimum for one of its modes on a scheme it covers */
  CAP_BELOW_MINIMUM(PricingProblemOutcome.REFUSED,
      "Its amount is lower than a minimum for one of its modes on a scheme it covers"),

  /** a mode named by a scheme on toll roads that its toll roads do not price */
  MODE_NOT_PRICED_BY_TOLL_ROADS(PricingProblemOutcome.DROPPED, "Its toll roads do not price a mode it names"),

  /** a mode named by an override, cap or minimum that its scheme does not charge */
  MODE_NOT_CHARGED_BY_SCHEME(PricingProblemOutcome.DROPPED,
      "An override, cap or minimum names a mode the scheme does not charge"),

  /** an override, cap or minimum left without modes once modes were dropped */
  LEFT_WITHOUT_MODES(PricingProblemOutcome.DROPPED, "An override, cap or minimum is left without modes"),

  /** a link segment of a section that is not one of the link segments its scheme charges on */
  SECTION_LINK_SEGMENT_NOT_PRICED(PricingProblemOutcome.DROPPED,
      "A section holds a link segment the scheme does not charge on"),

  /** a gate's entry or exit not where the link segments its scheme charges on are entered or left */
  GATE_NOT_AT_EDGE(PricingProblemOutcome.DROPPED,
      "A gate's entry or exit is not where the scheme's link segments are entered or left"),

  /** a section or gate left without link segments, with the section price rules and gate pairs referring to it */
  SECTION_OR_GATE_LEFT_EMPTY(PricingProblemOutcome.DROPPED, "A section or gate is left without link segments"),

  /** a scheme left without toll roads, its last one removed from the network */
  LEFT_WITHOUT_TOLL_ROADS(PricingProblemOutcome.DROPPED, "It is left without toll roads"),

  /**
   * a mode scheme whose own priced link segments are left with neither an included type nor an included link segment,
   * the last ones removed from the network, which would otherwise charge on every link segment of its layers
   */
  LEFT_WITHOUT_INCLUDED_TYPES_OR_LINK_SEGMENTS(PricingProblemOutcome.DROPPED,
      "It is left with neither an included link segment type nor an included link segment"),

  /** a mode exempted by a scheme on toll roads that its toll roads do not price anyway */
  EXEMPT_MODE_NOT_PRICED_BY_TOLL_ROADS(PricingProblemOutcome.REPORTED,
      "It exempts a mode its toll roads do not price"),

  /** two location or entry-exit schemes charge one mode on one link segment */
  SCHEMES_SHARE_LINK_SEGMENT(PricingProblemOutcome.REPORTED,
      "Another location or entry-exit scheme charges one of its modes on one of its link segments"),

  /** an entry and an exit connected through the scheme's link segments that no price rule lists */
  PAIR_WITHOUT_PRICE(PricingProblemOutcome.REPORTED, "An entry and an exit connected through it have no price");

  /** what happens to the scheme */
  private final PricingProblemOutcome outcome;

  /** what is wrong, for people */
  private final String description;

  /**
   * Constructor
   *
   * @param outcome what happens to the scheme
   * @param description what is wrong, for people
   */
  private PricingProblemKind(PricingProblemOutcome outcome, String description) {
    this.outcome = outcome;
    this.description = description;
  }

  /**
   * What happens to a scheme with this problem
   *
   * @return the outcome
   */
  public PricingProblemOutcome getOutcome() {
    return outcome;
  }

  /**
   * What is wrong, written for people
   *
   * @return the description
   */
  public String getDescription() {
    return description;
  }
}
