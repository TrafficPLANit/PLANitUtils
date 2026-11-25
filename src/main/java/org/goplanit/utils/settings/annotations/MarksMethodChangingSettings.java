package org.goplanit.utils.settings.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks the definition of an annotation that, written on a method changing settings, says how a settings file reaches
 * that method: through a key, through a key in other words, through the keys of the parts the settings hold, or not at
 * all with a reason
 * <p>
 * This annotation goes on the definition of another annotation, never on a settings method. For example,
 * {@link SettingsWrite} is defined as
 * </p>
 * <pre>
 * &#64;MarksMethodChangingSettings
 * &#64;Retention(RetentionPolicy.RUNTIME)
 * &#64;Target(ElementType.METHOD)
 * public &#64;interface SettingsWrite { ... }
 * </pre>
 * <p>
 * while a settings method carries {@code @SettingsWrite} alone, as before. Code checking settings classes reads the
 * mark from the annotation's definition, so wherever {@code @SettingsWrite} is found on a method it knows what it
 * means, without a list of annotations of its own to keep up to date.
 * {@link org.goplanit.utils.settings.SettingsCoverage} is that code: it requires every public method changing settings
 * to carry exactly one annotation marked this way, so a new setting cannot be left out of the settings files
 * unnoticed. An annotation for a new way of reaching a method gets this mark where it is defined, and the check takes
 * it into account from then on. Annotations on annotations are how Java defines annotations anyway:
 * {@code @Retention} and {@code @Target} above are written on the definition in the same way.
 * {@link MarksGetterOfNestedSettings} does the same for getters of nested settings.
 * </p>
 *
 * @author markr
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface MarksMethodChangingSettings {
}
