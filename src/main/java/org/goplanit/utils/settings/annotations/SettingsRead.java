package org.goplanit.utils.settings.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks the getter of a settings class that gives the whole value of a key of a settings file
 * <p>
 * For example, on the getter of the distance the {@link SettingsWrite} example sets:
 * </p>
 * <pre>
 * &#64;SettingsRead(key = "maximumJoiningReachMeters")
 * public double getMaximumJoiningReachMeters()
 * </pre>
 * <p>
 * The getter takes no arguments and gives everything the key holds so far, however it was set: one value, or the
 * whole list or map, also where the key's write method takes one entry at a time. Settings are written out as a file
 * through these getters; the package documentation says how the annotations work together.
 * </p>
 *
 * @author markr
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SettingsRead {

  /**
   * The key whose whole value the getter gives, as its {@link SettingsWrite} method names it
   *
   * @return the key
   */
  public String key();
}
