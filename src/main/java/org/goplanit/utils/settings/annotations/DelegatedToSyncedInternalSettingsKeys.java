package org.goplanit.utils.settings.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method of settings holding other settings internally, each made from a settings file a key names, that sets
 * the same keys in every one of them, keeping them in sync
 * <p>
 * For example, on the method of intermodal writer settings giving all the writers they hold one output folder, where
 * the settings file of each of those writers gives its own {@code outputDirectory}:
 * </p>
 * <pre>
 * &#64;DelegatedToSyncedInternalSettingsKeys("outputDirectory")
 * public void setOutputDirectory(String outputDirectory)
 * </pre>
 * <p>
 * Settings holding other settings internally, such as those of an intermodal writer, often give code one call for what
 * the file of each settings held sets on its own. The file of the holding settings names the files of the settings
 * held and holds none of their keys, so such a method is no key of it. The settings held are those whose getter
 * carries {@link SettingsFileReference}, and every one of them takes the keys named. Like
 * {@link CoveredBySettingsKey}, the mark changes nothing when settings are read or written; the checks that every
 * method changing settings is covered use it. The package documentation says how the annotations work together.
 * </p>
 *
 * @author markr
 */
@MarksMethodChangingSettings
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DelegatedToSyncedInternalSettingsKeys {

  /**
   * The keys every settings held takes, such as {@code outputDirectory}
   *
   * @return the keys
   */
  public String[] value();
}
