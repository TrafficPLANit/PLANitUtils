package org.goplanit.utils.settings.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a constructor parameter of a settings class as taking the value of a key at the top level of a settings file
 * <p>
 * For example, on the constructor of settings whose defaults depend on a country:
 * </p>
 * <pre>
 * public ExampleNetworkSettings(
 *     &#64;SettingsTopLevelValue(key = "country", description = "The country whose defaults apply") String countryName)
 * </pre>
 * <p>
 * Settings made from a file holding {@code country: Australia} at its top level are made with this constructor, given
 * {@code "Australia"}; made from a file without it, with the constructor taking nothing. It is for a value only a
 * constructor can set, so it is read before the rest of the file, which then fills the settings made. Every parameter
 * of the constructor carries it, each taking text, and a class has one such constructor at most. Settings written out
 * as a file with descriptions give the description above the key.
 * </p>
 *
 * @author markr
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface SettingsTopLevelValue {

  /**
   * The key at the top level of a settings file whose value the parameter takes, such as {@code country}
   *
   * @return the key
   */
  public String key();

  /**
   * What the value sets, in a sentence, written above its key when settings are written out as a file
   *
   * @return the description
   */
  public String description();
}
