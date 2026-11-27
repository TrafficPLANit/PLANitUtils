package org.goplanit.utils.pricing;

import org.goplanit.utils.network.layer.macroscopic.tollroad.PricedModeSelection;

import java.io.Serializable;
import org.goplanit.utils.mode.Mode;
import java.util.List;

/**
 * An override for some modes: either a price rule of the scheme's kind, taking what it leaves out from the price rule
 * at that moment, or a factor multiplying every part of that price rule
 * <p>
 * For example trucks charged three times the car price, {@code factor="3"}, or motorcycles with a start of their own
 * and the car rate per km. An override of the base price rule applies outside the time bands and inside those not
 * overriding it; an override of a time band applies during that band. Each mode is in at most one override of each.
 * An override is not a price rule itself: it holds one, so an override giving a factor holds no price rule at all.
 * </p>
 * <p>
 * An override cannot work out what its modes pay on its own: its price rule takes what it leaves out from the price
 * rule in force, and its factor multiplies that price rule, which is the base's or a band's filled in from the base.
 * The scheme's pricing holds them, so ask it, {@link SchemePricing#getPointPriceRule}; an override answers only
 * which modes it applies to.
 * </p>
 *
 * @param <P> the kind of price rule, {@link StartWithPerKmPriceRule} or {@link FromToGatesPriceRule}
 * @author markr
 */
public interface PriceOverride<P extends PriceRule<P>> extends Serializable {

  /**
   * The first of some overrides applying to a mode, as each mode is in at most one override of a price rule
   *
   * @param <P> the kind of price rule
   * @param priceOverrides to look in
   * @param mode to find
   * @return the override, null when none applies to the mode
   */
  public static <P extends PriceRule<P>> PriceOverride<P> findApplyingTo(
      List<PriceOverride<P>> priceOverrides,
      Mode mode) {
    return priceOverrides.stream()
        .filter(priceOverride -> priceOverride.appliesTo(mode))
        .findFirst()
        .orElse(null);
  }

  /**
   * The modes the override is for, always given by name
   *
   * @return the priced mode selection
   */
  public abstract PricedModeSelection getPricedModeOverrides();

  /**
   * The price rule the override gives, its amounts replacing those of the price rule at that moment
   *
   * @return the price rule, null when the override gives a factor instead
   */
  public abstract P getPriceRule();

  /**
   * The factor multiplying every part of the price rule at that moment
   *
   * @return the factor, null when the override gives a price rule instead
   */
  public abstract Double getPriceOverrideFactor();

  /**
   * Whether the override is for a mode: it names it
   *
   * @param mode to check
   * @return true when the override applies to the mode
   */
  public default boolean appliesTo(Mode mode) {
    return getPricedModeOverrides().appliesTo(mode);
  }
}
