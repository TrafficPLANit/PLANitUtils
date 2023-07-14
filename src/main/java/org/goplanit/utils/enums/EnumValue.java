package org.goplanit.utils.enums;

/**
 * Allow enum to implement this interface so it is accessible to {@link EnumOf} which in turn uses it
 * to create a factory method allowing one to construct the enum based on this internal value
 *
 * @param <T> type of value captured by enum
 */
public interface EnumValue<T> {

  /**
   * Collect internal value of the enum that implements this interface
   * @return value
   */
  public abstract T getValue();
}
