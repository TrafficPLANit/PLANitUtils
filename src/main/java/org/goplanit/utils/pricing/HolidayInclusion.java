package org.goplanit.utils.pricing;

/**
 * Whether holidays count for a time band entry: also (included), not (excluded), or alone (only)
 * <p>
 * A morning peak may apply on weekdays but not on public holidays, a holiday rate on public holidays alone. Two
 * entries of a scheme's time bands may not overlap; whether their holiday settings can both hold on one day is asked of
 * the setting itself, see {@link #overlaps(HolidayInclusion)}. A setting a file leaves out is {@link #INCLUDED}, the
 * schema's default.
 * </p>
 *
 * @author markr
 */
public enum HolidayInclusion {

  /** holidays count as any other day */
  INCLUDED,

  /** holidays do not count */
  EXCLUDED,

  /** only holidays count */
  ONLY;

  /**
   * Whether this setting and another can both hold on one day: always, except holidays only against holidays
   * excluded, which no day meets both of. For example entries for public holidays only and for public holidays
   * excluded never overlap, whatever their times
   *
   * @param other setting to check against
   * @return true when some day meets both
   */
  public boolean overlaps(HolidayInclusion other) {
    return !((this == ONLY && other == EXCLUDED) || (this == EXCLUDED && other == ONLY));
  }

  /**
   * Whether a day meets this setting: any day when holidays are included, a day that is no holiday when they are
   * excluded, and a holiday when only holidays count. For example a public holiday does not meet an entry excluding
   * public holidays
   *
   * @param holiday true when the day is a holiday of the kind the setting is about
   * @return true when the day meets the setting
   */
  public boolean isAgreeWith(boolean holiday) {
    return this == INCLUDED || (this == ONLY) == holiday;
  }
}
