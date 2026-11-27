package org.goplanit.utils.pricing;

/**
 * What happens to a scheme with a problem: refused, a part dropped, or kept and reported
 * <p>
 * A problem that makes a scheme's price rules ambiguous, such as two time bands overlapping, refuses the scheme; one
 * that can be fixed by taking a part away, such as a mode its toll roads do not price, drops that part; one that may be
 * meant, such as two schemes charging on one link segment, is reported and the scheme kept.
 * </p>
 *
 * @author markr
 */
public enum PricingProblemOutcome {

  /** the scheme is not added */
  REFUSED,

  /** the part with the problem is removed, the rest kept */
  DROPPED,

  /** the scheme is kept as it is, with a warning */
  REPORTED
}
