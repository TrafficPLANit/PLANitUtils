package org.goplanit.utils.settings.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks the method of a settings class that a key of a settings file is written through, naming the key and saying how
 * it changes the settings
 * <p>
 * For example, on the setter of a distance:
 * </p>
 * <pre>
 * &#64;SettingsWrite(key = "maximumJoiningReachMeters", level = SettingsKeyLevel.UNCOMMON,
 *     description = "How far apart along the road the parts of one intersection may be, in metres")
 * public void setMaximumJoiningReachMeters(double maximumJoiningReachMeters)
 * </pre>
 * <p>
 * A file then writes {@code maximumJoiningReachMeters: 60}, which calls {@code setMaximumJoiningReachMeters(60)}. The
 * method takes the key's value: the whole value in one call for a replacing key, as here, or one entry per call for
 * the other effects, see {@link SettingsKeyEffect}. It refuses a value it cannot use with an
 * {@link IllegalArgumentException}, which the loader then reports with the file and the key. The description is kept
 * here as well as in the javadoc, because javadoc is not there at run time and writing settings out as a file prints
 * it beside the key. The package documentation says how the annotations work together.
 * </p>
 *
 * @author markr
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SettingsWrite {

  /**
   * The key's name in a settings file: the Java name of the setting, such as {@code maximumReachMeters} for
   * {@code setMaximumReachMeters}
   *
   * @return the key
   */
  public String key();

  /**
   * How applying the key changes the settings
   *
   * @return the effect, replacing by default
   */
  public SettingsKeyEffect effect() default SettingsKeyEffect.REPLACE;

  /**
   * What the key sets, in a sentence, written beside it when settings are written out as a file
   *
   * @return the description
   */
  public String description();

  /**
   * How often users need the key. It orders the keys of a section when settings are written out as a file
   *
   * @return the level
   */
  public SettingsKeyLevel level();

  /**
   * The names the key had before, still accepted, with a warning that gives the current name
   *
   * @return the former names, none by default
   */
  public String[] formerKeys() default {};
}
