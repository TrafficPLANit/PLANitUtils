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
 * the section it fills at the root of a file;</li>
 * <li>{@link SettingsHeader} on a settings class filling a section at the root, or on a class it extends, giving the
 * first line of the file;</li>
 * <li>{@link SettingsRootValue} on every parameter of the one constructor taking values at the root of a file, such
 * as a country, which only a constructor can set;</li>
 * <li>{@link CoveredBySettingsKey} on a method that writes the same state as a key in other words, such as a form
 * taking an array where the key takes a list;</li>
 * <li>{@link NoSettingsKey} on any other method that changes settings, saying why it stays out of the files.</li>
 * </ul>
 * <p>
 * Files are read by calling the marked write methods, and settings are written out as a file through the marked
 * getters, both in the package above, {@code org.goplanit.utils.settings}. Every public method that changes settings
 * carries exactly one of {@link SettingsWrite}, {@link CoveredBySettingsKey} and {@link NoSettingsKey}, which a test of
 * each settings class checks, so a new setting cannot be left out of the files unnoticed.
 * </p>
 */
package org.goplanit.utils.settings.annotations;
