package org.goplanit.utils.settings.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks the definition of an annotation that, written on a getter returning nested settings, says how a settings file
 * reaches those settings: as a section, as a file of their own, as the value of a key, or not at all with a reason
 * <p>
 * This annotation goes on the definition of another annotation, never on a settings method. For example,
 * {@link SettingsSection} is defined as
 * </p>
 * <pre>
 * &#64;MarksGetterOfNestedSettings
 * &#64;Retention(RetentionPolicy.RUNTIME)
 * &#64;Target({ElementType.METHOD, ElementType.TYPE})
 * public &#64;interface SettingsSection { ... }
 * </pre>
 * <p>
 * while a getter carries {@code @SettingsSection} alone, as before. Code checking settings classes reads the mark from
 * the annotation's definition, so wherever {@code @SettingsSection} is found on a getter it knows what it means,
 * without a list of annotations of its own to keep up to date. {@link org.goplanit.utils.settings.SettingsCoverage}
 * is that code: it requires every getter returning nested settings to carry an annotation marked this way, so nested
 * settings cannot hide from the settings files, and it follows such getters to find every settings class to check. An
 * annotation for a new way of reaching nested settings gets this mark where it is defined, and the check takes it into
 * account from then on. {@link MarksMethodChangingSettings} does the same for methods changing settings, where the
 * reasons to mark annotations this way are explained.
 * </p>
 *
 * @author markr
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface MarksGetterOfNestedSettings {
}
