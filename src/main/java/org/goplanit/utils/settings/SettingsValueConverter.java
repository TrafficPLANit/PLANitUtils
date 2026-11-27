package org.goplanit.utils.settings;

import org.goplanit.utils.yaml.SimpleYamlReader;

/**
 * Turns the value of a settings key into a value of a type the settings take, and back, for a type that is neither a
 * plain value nor a small section of keys: one that cannot change once made, or comes in several forms
 * <p>
 * For example, a range written as {@code {min: 1, max: 5}}, made once and without setters, gets a converter
 * </p>
 * <pre>
 * public final class RangeConverter implements SettingsValueConverter&lt;Range&gt; {
 *   public Class&lt;Range&gt; getValueType() { return Range.class; }
 *   public Range fromPlainValue(Object plainValue) { ... return new Range(min, max); }
 *   public Object toPlainValue(Range range) { return Map.of("min", ..., "max", ...); }
 * }
 * </pre>
 * <p>
 * which, registered on the schema with {@code new SettingsSchema(List.of(new RangeConverter()), ...)}, makes the
 * value of every key whose method takes a {@code Range}. A small section, made with its constructor without arguments
 * and filled through annotated setters, suits most values; a converter is for the rest. It keeps the type free of
 * settings code, so a type of another library can be converted too, and sits next to the type it converts.
 * </p>
 *
 * @param <T> the type converted
 * @author markr
 */
public interface SettingsValueConverter<T> {

  /**
   * The type converted
   *
   * @return the type
   */
  public abstract Class<T> getValueType();

  /**
   * A value of the type from the value of a key, as {@link SimpleYamlReader} gives it
   *
   * @param plainValue text, a list, or keys with values, whose values are text, lists or keys with values again
   * @return the value
   * @throws IllegalArgumentException saying why, when the value does not fit; the loader reports it as a value refused
   */
  public abstract T fromPlainValue(Object plainValue);

  /**
   * The value of a key from a value of the type, for writing settings out as a file, read back by
   * {@link #fromPlainValue(Object)} as an equal value
   *
   * @param value to write
   * @return text, a list, or keys with values, as {@link #fromPlainValue(Object)} takes them
   */
  public abstract Object toPlainValue(T value);
}
