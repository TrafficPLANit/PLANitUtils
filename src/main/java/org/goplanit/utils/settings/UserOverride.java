package org.goplanit.utils.settings;

import org.goplanit.utils.exceptions.ExceptionUtils;
import org.goplanit.utils.misc.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A value the user gives in place of what would be derived from the input, with the reason for it when one is given
 * <p>
 * For example, the kind of a traffic signal given for one node, with where it comes from:
 * </p>
 * <pre>
 * settings.overrideTrafficSignalKind(123, UserOverride.of(RAMP_METER, "signal on site, checked 2026-10-30"));
 * </pre>
 * <p>
 * which a settings file writes as:
 * </p>
 * <pre>
 * trafficSignalKindOverrides:
 *   123: { value: RAMP_METER, reason: "signal on site, checked 2026-10-30" }
 * </pre>
 * <p>
 * Corrections of input data are quick to make and hard to trace later. Keeping the reason with the value means it
 * survives writing the settings out as a file, and it is logged when the override's id is not found in the input,
 * telling the user why the override was there. It is immutable, a blank reason counts as none, and it is named so it
 * does not clash with Java's {@code @Override}.
 * </p>
 *
 * @param <T> type of the value
 * @author markr
 */
public final class UserOverride<T> {

  /** the value given */
  private final T value;

  /** why it was given, null when no reason was given */
  private final String reason;

  /**
   * Constructor
   *
   * @param value the value given, not null
   * @param reason why it was given, null or blank for none
   */
  private UserOverride(T value, String reason) {
    ExceptionUtils.throwIllegalArgumentIf(value == null, "A user override needs a value");
    this.value = value;
    this.reason = StringUtils.isNullOrBlank(reason) ? null : reason;
  }

  /** What {@link #getReasonOrUnknown()} gives for an override without a reason */
  public static final String REASON_UNKNOWN = "reason unknown";

  /** The field of an override written with its reason in a settings file that holds its value */
  public static final String VALUE_FIELD = "value";

  /** The field of an override written with its reason in a settings file that holds its reason */
  public static final String REASON_FIELD = "reason";

  /**
   * Create an override without a reason
   *
   * @param <T> type of the value
   * @param value the value given, not null
   * @return the override
   */
  public static <T> UserOverride<T> of(T value) {
    return new UserOverride<>(value, null);
  }

  /**
   * Create an override with the reason it was given
   *
   * @param <T> type of the value
   * @param value the value given, not null
   * @param reason why it was given, null or blank for none
   * @return the override
   */
  public static <T> UserOverride<T> of(T value, String reason) {
    return new UserOverride<>(value, reason);
  }

  /**
   * The value of an override that may not be there, such as one looked up by id in a map of overrides
   *
   * @param <T> type of the value
   * @param override the override, null when there is none
   * @return its value, null when there is no override
   */
  public static <T> T getValueOrNull(UserOverride<T> override) {
    return override == null ? null : override.getValue();
  }

  /**
   * The values of overrides without their reasons, for code that has no use for the reasons
   *
   * @param <T> type of the values
   * @param overrides to take the values of
   * @return the values, in the order of the overrides
   */
  public static <T> List<T> valuesOf(List<UserOverride<T>> overrides) {
    return overrides.stream().map(UserOverride::getValue).collect(Collectors.toList());
  }

  /**
   * The reasons of the overrides that have one, joined by "; ", such as for the detail of an id not found that several
   * overrides name
   *
   * @param overrides to join the reasons of, null for an override that is not there
   * @return the reasons, each once, in the order of the overrides, null when none has one
   */
  public static String joinReasons(Stream<? extends UserOverride<?>> overrides) {
    var reasons = overrides.filter(override -> override != null && override.hasReason())
        .map(UserOverride::getReason).distinct().collect(Collectors.toList());
    return reasons.isEmpty() ? null : String.join("; ", reasons);
  }

  /**
   * The value given
   *
   * @return the value, as given
   */
  public T getValue() {
    return value;
  }

  /**
   * Verify whether a reason was given
   *
   * @return true when a reason was given, false otherwise
   */
  public boolean hasReason() {
    return reason != null;
  }

  /**
   * Why the value was given
   *
   * @return the reason, null when none was given
   */
  public String getReason() {
    return reason;
  }

  /**
   * Why the value was given, for showing to a user
   *
   * @return the reason, or {@link #REASON_UNKNOWN} when none was given
   */
  public String getReasonOrUnknown() {
    return hasReason() ? reason : REASON_UNKNOWN;
  }

  /**
   * The same override with its value changed, such as read in another form, keeping the reason
   *
   * @param <R> type of the changed value
   * @param valueChange gives the changed value from the value, not null
   * @return the override of the changed value, with the same reason
   */
  public <R> UserOverride<R> map(Function<T, R> valueChange) {
    return new UserOverride<>(valueChange.apply(value), reason);
  }

  /**
   * Equal when both the value and the reason are equal
   *
   * @param other to compare with
   * @return true when equal, false otherwise
   */
  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof UserOverride)) {
      return false;
    }
    var otherOverride = (UserOverride<?>) other;
    return value.equals(otherOverride.value) && Objects.equals(reason, otherOverride.reason);
  }

  /**
   * Hash of the value and the reason
   *
   * @return hash code
   */
  @Override
  public int hashCode() {
    return Objects.hash(value, reason);
  }

  /**
   * The value followed by its reason in brackets, or by {@link #REASON_UNKNOWN} when it has none, for the settings log
   *
   * @return the text
   */
  @Override
  public String toString() {
    return value + " (" + getReasonOrUnknown() + ")";
  }
}
