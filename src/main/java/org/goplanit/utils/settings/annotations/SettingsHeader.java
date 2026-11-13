package org.goplanit.utils.settings.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Gives the header of the settings files whose root sections a settings class fills: their first line, a key naming
 * the kind of settings, and the version of their keys
 * <p>
 * For example, on the base class of the settings of some readers:
 * </p>
 * <pre>
 * &#64;SettingsHeader(key = "planitExampleSettings", version = 1)
 * public abstract class ExampleReaderSettings
 * </pre>
 * <p>
 * Its subclasses marked with the section they fill at the root ({@link SettingsSection}) then take files starting
 * with {@code planitExampleSettings: 1}. It is inherited, so it is given once for every class sharing the header. Each
 * kind of settings has a header key of its own, so text meant for other settings is refused before any of its keys is
 * read, and the keys of one kind can change without touching another. The version goes up only for a change that
 * renaming a key cannot express, such as a key split in two, so text of an older version is refused rather than read
 * wrongly.
 * </p>
 *
 * @author markr
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SettingsHeader {

  /**
   * The key of the first line, naming the kind of settings
   *
   * @return the key
   */
  public String key();

  /**
   * The version of the keys, the value of the first line
   *
   * @return the version
   */
  public int version();
}
