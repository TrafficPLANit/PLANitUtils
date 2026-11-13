package org.goplanit.utils.settings.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method of a settings class that writes the same state as one or more keys of a settings file, in another form
 * <p>
 * For example, on a method that switches consolidation on and gives its settings in one call, where a file writes the
 * key {@code consolidateIntersections} and the section {@code intersectionConsolidator}:
 * </p>
 * <pre>
 * &#64;CoveredBySettingsKey({"consolidateIntersections", "intersectionConsolidator"})
 * public void setConsolidateIntersections(boolean flag, IntersectionConsolidatorSettings consolidatorSettings)
 * </pre>
 * <p>
 * Code is often offered the same change in several forms, such as one taking an array and one taking a list, or one
 * adding a single entry where the key adds a list. A file offers each change one way only, so such a method is not a
 * key of its own. It writes nothing the named keys cannot: a method that sets anything else is a key of its own or
 * {@link NoSettingsKey}. The package documentation says how the annotations work together.
 * </p>
 *
 * @author markr
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CoveredBySettingsKey {

  /**
   * The keys and sections that write the same state. A key of a nested section is written as the section's name, a
   * dot and the key, such as {@code intersectionConsolidator.maximumJoiningReachMeters}
   *
   * @return the keys and sections
   */
  public String[] value();
}
