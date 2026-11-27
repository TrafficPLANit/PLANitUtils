package org.goplanit.utils.pricing;

import java.util.List;

/**
 * The price rule of a mode or location scheme: a start per use and a rate per km, with price rules per section
 * <p>
 * For example WestConnex, a start of 1.73 per use and 0.64 per km, or a road charged per gantry, with a price rule per
 * gantry section only. Each amount may be left out: in a time band or an override it then comes from the price rule it
 * changes; in the price rule for a mode at a moment it is given.
 * </p>
 *
 * @author markr
 */
public interface StartWithPerKmPriceRule extends PriceRule {

  /**
   * The amount charged once per use of the scheme: the flagfall, or the whole charge of a gantry or cordon crossing
   *
   * @return the start, null when not given
   */
  public abstract Double getStartAmountInCurrency();

  /**
   * The amount charged per km travelled on the scheme's link segments outside every section
   *
   * @return the rate per km, null when not given
   */
  public abstract Double getPerKmAmountInCurrency();

  /**
   * The price rules of the scheme's sections
   *
   * @return unmodifiable list of the section price rules, empty when there are none
   */
  public abstract List<SectionPriceRule> getSectionPriceRules();

  /**
   * The price rule of a section, as this price rule gives it, such as the rate a route search charges on a link segment
   * of the section
   *
   * @param section to find, compared by identity
   * @return its section price rule, null when this price rule gives none for it
   */
  public default SectionPriceRule findSectionPriceRule(PricingSchemeSection section) {
    return getSectionPriceRules().stream().filter(sectionPriceRule -> sectionPriceRule.getSection() == section)
        .findFirst().orElse(null);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public default boolean givesNoAmount() {
    return getStartAmountInCurrency() == null && getPerKmAmountInCurrency() == null && getSectionPriceRules().isEmpty();
  }

  /**
   * The rate per km charged in a section: the section price rule's own when it gives one, else this rule's
   *
   * @param section of the scheme
   * @return the rate per km, 0 when neither gives one
   */
  public default double findPerKmAmountInCurrency(PricingSchemeSection section) {
    var sectionPriceRule = findSectionPriceRule(section);
    Double perKmAmountInCurrency = sectionPriceRule != null && sectionPriceRule.getPerKmAmountInCurrency() != null ?
        sectionPriceRule.getPerKmAmountInCurrency() : getPerKmAmountInCurrency();
    return perKmAmountInCurrency == null ? 0 : perKmAmountInCurrency;
  }

  /**
   * The start charged per pass through a section, on top of the rule's own start
   *
   * @param section of the scheme
   * @return the section's start, 0 when its section price rule gives none or there is none
   */
  public default double findSectionStartAmountInCurrency(PricingSchemeSection section) {
    var sectionPriceRule = findSectionPriceRule(section);
    return sectionPriceRule == null || sectionPriceRule.getStartAmountInCurrency() == null
        ? 0 : sectionPriceRule.getStartAmountInCurrency();
  }
}
