package org.goplanit.utils.settings.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method of a settings class that changes the settings but is written through no key of a settings file, with
 * the reason it stays out
 * <p>
 * For example, on a method that builds the collapser settings from the user's choices while the network is read:
 * </p>
 * <pre>
 * &#64;NoSettingsKey(reason = "builds the collapser settings during the read")
 * public IntersectionCollapserSettings createIntersectionCollapserSettings(...)
 * </pre>
 * <p>
 * Such a method is an action rather than a choice, such as resetting the settings, or wiring that builds other objects
 * during a run, which a file has no business setting. The mark says that leaving it out is deliberate. The package
 * documentation says how the annotations work together.
 * </p>
 *
 * @author markr
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface NoSettingsKey {

  /**
   * Why the method stays out of the files, in a few words
   *
   * @return the reason
   */
  public String reason();
}
