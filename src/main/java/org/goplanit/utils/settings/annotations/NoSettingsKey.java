package org.goplanit.utils.settings.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method of a settings class that changes the settings but is written through no key of a settings file, with
 * the reason it stays out
 * <p>
 * For example, on the method that puts the settings back to their defaults:
 * </p>
 * <pre>
 * &#64;NoSettingsKey(reason = "puts every setting back to its default")
 * public void reset()
 * </pre>
 * <p>
 * Such a method is an action rather than a choice, such as resetting or logging the settings, or it takes a value a
 * file cannot hold, such as a function. The mark says that leaving it out is deliberate.
 * </p>
 * <p>
 * Which methods need it: a public method that returns nothing, or takes arguments and returns its settings as a fluent
 * setter does, says which key reaches it ({@link SettingsWrite}, {@link CoveredBySettingsKey},
 * {@link DelegatedToSyncedInternalSettingsKeys}) or that none does, with this mark. A method that only reads, such as
 * a lookup by an argument or a copy such as {@code shallowClone()}, needs no mark. The package documentation says how
 * the annotations work together.
 * </p>
 *
 * @author markr
 */
@MarksMethodChangingSettings
@MarksGetterOfNestedSettings
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
