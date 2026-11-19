package org.goplanit.utils.settings;

/**
 * Which keys settings written out as a file hold: every key, or only those whose value differs from fresh settings
 * <p>
 * For example, settings of a network whose maximum speed was changed to 80 in code give every key at its value with
 * {@link #ALL_KEYS}, a long file to see every option in or to start a file from, and only {@code maximumSpeed: 80}
 * with {@link #KEYS_DIFFERING_FROM_DEFAULTS}, a short file of the choices made. Either file read back gives the same
 * settings.
 * </p>
 *
 * @author markr
 */
public enum SettingsFileContentType {

  /** Every key at its value, and with descriptions a key with no value commented out */
  ALL_KEYS,

  /** Only the keys whose value differs from that of fresh settings, made with the same values at the root */
  KEYS_DIFFERING_FROM_DEFAULTS
}
