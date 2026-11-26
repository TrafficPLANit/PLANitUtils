/**
 * Annotations that let settings classes be filled from settings files, and written out as them
 * <p>
 * A settings file holds keys, each with a value, grouped in sections that mirror the settings classes: a key is one
 * setting, and a section is the nested settings a getter returns. A settings class says what its file looks like by
 * marking its own methods, so the keys follow from the code:
 * </p>
 * <ul>
 * <li>{@link SettingsWrite} on the one method a key is written through, also saying how the key changes the settings
 * ({@link SettingsKeyEffect}) and how often users need it ({@link SettingsKeyLevel});</li>
 * <li>{@link SettingsRead} on the getter giving the key's whole value. A key that adds to or removes from a list,
 * written with {@code +} or {@code -} after the list's name, is read through the getter of the whole list;</li>
 * <li>{@link SettingsSection} on a getter of nested settings, which become a section, or on a settings class, naming
 * the section it fills at the top level of a file;</li>
 * <li>{@link SettingsHeader} on a settings class, or on a class it extends, giving the first line of the file. When the
 * settings fill a section and the class giving the header fills none itself, the keys that class declares sit at the
 * top level of the file and apply to every section, such as {@code retainTags: true} under the header. Settings filling
 * no section take every key of their class at the top level, whichever class declares it, so a file holds one
 * object;</li>
 * <li>{@link SettingsFileReference} on a getter of nested settings, a key naming the file the nested settings are
 * filled from;</li>
 * <li>{@link SettingsTopLevelValue} on every parameter of the one constructor taking values at the top level of a file,
 * such as a country, which only a constructor can set;</li>
 * <li>{@link CoveredBySettingsKey} on a method that writes the same state as a key in other words, such as a form
 * taking an array where the key takes a list;</li>
 * <li>{@link DelegatedToSyncedInternalSettingsKeys} on a method of settings holding nested settings that sets the same
 * keys in all of them, such as one output folder for all the writers an intermodal writer holds;</li>
 * <li>{@link NoSettingsKey} on any other method that changes settings, saying why it stays out of the files.</li>
 * </ul>
 * <p>
 * Files are read by calling the marked write methods, and settings are written out as a file through the marked
 * getters, both in the package above, {@code org.goplanit.utils.settings}. Every public method that changes settings
 * carries exactly one of {@link SettingsWrite}, {@link CoveredBySettingsKey},
 * {@link DelegatedToSyncedInternalSettingsKeys} and {@link NoSettingsKey}, which {@code SettingsCoverage} in that
 * package checks from a test of each converter's settings, so a new setting cannot be left out of the files unnoticed.
 * A method changes settings when it returns nothing, or takes arguments and returns its settings, as a fluent setter
 * does. A method that only reads, such as a lookup by an argument or a copy such as {@code shallowClone()}, carries
 * none of them.
 * </p>
 * <p>
 * The check knows these annotations by a mark on their definitions rather than by a list: {@link SettingsWrite},
 * {@link CoveredBySettingsKey}, {@link DelegatedToSyncedInternalSettingsKeys} and {@link NoSettingsKey} carry
 * {@link MarksMethodChangingSettings}, and {@link SettingsSection}, {@link SettingsFileReference}, {@link SettingsRead}
 * and {@link NoSettingsKey} carry {@link MarksGetterOfNestedSettings}. A new annotation saying how a file reaches a
 * method or nested settings carries the matching mark, and the check takes it into account. The two marks explain why
 * annotations are marked this way.
 * </p>
 */
package org.goplanit.utils.settings.annotations;
