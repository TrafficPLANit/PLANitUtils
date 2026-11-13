package org.goplanit.utils.settings.annotations;

/**
 * How often users need a key of a settings file, from the keys nearly every run sets to those that only change how a
 * method works
 * <p>
 * Settings can have many keys, and most users only ever need a few of them. When settings are written out as a file,
 * and in the documentation generated from that, each section lists its keys by level, the standard ones first, so a
 * user finds what matters at the top and the keys for tuning a method at the bottom. The level changes nothing about
 * how a key is read.
 * </p>
 *
 * @author markr
 */
public enum SettingsKeyLevel {

  /** Nearly every run sets or should check it, such as the input to read or the area to cover */
  STANDARD,

  /** Many uses change it, such as whether an optional step runs */
  COMMON,

  /**
   * A finer choice of a method, such as {@code maximumJoiningReachMeters}, how far apart the parts of one intersection
   * may be, or a correction for particular input data
   */
  UNCOMMON,

  /**
   * It changes how a method works or what it logs, not what it produces, such as
   * {@code subdivisionsKeptPerSearchStep}, how much of a search for the best grouping is tried
   */
  RARE
}
