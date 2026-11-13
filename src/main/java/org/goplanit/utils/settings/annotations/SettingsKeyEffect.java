package org.goplanit.utils.settings.annotations;

/**
 * How applying one key of a settings file changes the settings it is read into
 * <p>
 * Each key is written through one method of the settings. Some keys hold one value that replaces what was there;
 * others hold a list or a map whose entries are handed to the method one at a time, each adding to or removing from
 * what the settings hold. The loader needs to know which, to call the method once or once per entry. Files stacked in
 * order rely on it too: a later file's replacing key wins, while the entries of an adding key add up across files.
 * </p>
 * <p>
 * For example, each line of a file on the left calls the method of its key as on the right:
 * </p>
 * <pre>
 * maximumJoiningReachMeters: 60    REPLACE      setMaximumJoiningReachMeters(60)
 * excludedIds: [7, 9]              ADD_EACH     excludeId(7), excludeId(9)
 * modes-: [bus, tram]              REMOVE_EACH  deactivateMode("bus"), deactivateMode("tram")
 * trafficSignalKindOverrides:      PUT_EACH     overrideTrafficSignalKind(123, RAMP_METER),
 *   123: RAMP_METER                             overrideTrafficSignalKind(456, CROSSING_SIGNAL)
 *   456: CROSSING_SIGNAL
 * </pre>
 *
 * @author markr
 */
public enum SettingsKeyEffect {

  /** One call with the whole value, which replaces what was there */
  REPLACE,

  /** One call per entry of the key's list, each adding to what was there */
  ADD_EACH,

  /** One call per entry of the key's list, each removing from what was there */
  REMOVE_EACH,

  /** One call per entry of the key's map, with the entry's key and value, each replacing only the entry of that key */
  PUT_EACH
}
