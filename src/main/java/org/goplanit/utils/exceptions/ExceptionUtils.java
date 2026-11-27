package org.goplanit.utils.exceptions;

/**
 * Throwing an {@link IllegalArgumentException} in one call when a method refuses an argument, or an
 * {@link IllegalStateException} when it refuses a call its object is not ready for
 * <p>
 * Settings and utilities refuse a value that makes no sense, such as a cycle time of 0 s, before they store or use it.
 * Java's own {@link IllegalArgumentException} says the caller passed it, so a caller can tell it from a failure; the
 * settings loader reports it as a value refused. Likewise {@link IllegalStateException} says the call came at the
 * wrong time, such as closing a section of a writer that has none open. One call per check keeps a method about its
 * work, and the setting form gives every refused setting the same message
 * </p>
 *
 * @author markr
 */
public class ExceptionUtils {

  /**
   * Throw an {@link IllegalArgumentException} when the condition holds. The message arguments are worked out whether
   * or not it holds, so each must be safe to work out when nothing is refused
   *
   * @param condition true to throw
   * @param message of the exception, a format string as for {@link String#format(String, Object...)}
   * @param messageArgs to format the message with
   * @throws IllegalArgumentException with the formatted message, when the condition holds
   */
  public static void throwIllegalArgumentIf(boolean condition, String message, Object... messageArgs) {
    if (condition) {
      throw new IllegalArgumentException(String.format(message, messageArgs));
    }
  }

  /**
   * Throw an {@link IllegalStateException} when the condition holds. The message arguments are worked out whether or
   * not it holds, so each must be safe to work out when nothing is refused
   *
   * @param condition true to throw
   * @param message of the exception, a format string as for {@link String#format(String, Object...)}
   * @param messageArgs to format the message with
   * @throws IllegalStateException with the formatted message, when the condition holds
   */
  public static void throwIllegalStateIf(boolean condition, String message, Object... messageArgs) {
    if (condition) {
      throw new IllegalStateException(String.format(message, messageArgs));
    }
  }

  /**
   * Throw an {@link IllegalArgumentException} for a refused value of a setting, such as "Cycle time of 0.0 refused, it
   * must be above 0 s; the setting is unchanged". Call it before the value is stored, so the setting keeps the value
   * it had
   *
   * @param refused true to throw
   * @param setting name of the setting, starting with a capital
   * @param value refused
   * @param validValues what a value must be, such as "above 0 s"
   * @throws IllegalArgumentException naming the setting, the value and what it must be, when refused
   */
  public static void throwIllegalSettingValueIf(boolean refused, String setting, Object value, String validValues) {
    throwIllegalArgumentIf(refused, "%s of %s refused, it must be %s; the setting is unchanged", setting, value,
        validValues);
  }
}
