package org.goplanit.utils.pricing;

import java.io.Serializable;

/**
 * The price rule of one section of a scheme: a start each time the section is passed, and a rate per km in it
 * <p>
 * For example a gantry charging 2.50 per pass, or zone 1 westbound of a road charged per km by zone at 0.40 per km.
 * Its rate per km, left out, comes from the price rule it is in; its start never does, as there it is the charge per
 * use of the scheme.
 * </p>
 *
 * @author markr
 */
public interface SectionPriceRule extends Serializable {

  /**
   * The section priced, one of the scheme's own
   *
   * @return the section
   */
  public abstract PricingSchemeSection getSection();

  /**
   * The amount charged each time the section is passed, a pass being a continuous run through its link segments
   *
   * @return the start, null when not given
   */
  public abstract Double getStartAmountInCurrency();

  /**
   * The amount charged per km travelled in the section
   *
   * @return the rate per km, null when not given
   */
  public abstract Double getPerKmAmountInCurrency();
}
