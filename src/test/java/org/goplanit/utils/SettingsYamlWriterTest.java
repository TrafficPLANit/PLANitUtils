package org.goplanit.utils;

import org.goplanit.utils.SettingsYamlLoaderTest.TestKind;
import org.goplanit.utils.SettingsYamlLoaderTest.TestLanes;
import org.goplanit.utils.SettingsYamlLoaderTest.TestOrderFirstSettings;
import org.goplanit.utils.SettingsYamlLoaderTest.TestOrderHolderSettings;
import org.goplanit.utils.SettingsYamlLoaderTest.TestOrderSecondSettings;
import org.goplanit.utils.SettingsYamlLoaderTest.TestOtherSettings;
import org.goplanit.utils.SettingsYamlLoaderTest.TestRange;
import org.goplanit.utils.SettingsYamlLoaderTest.TestRangeConverter;
import org.goplanit.utils.SettingsYamlLoaderTest.TestRootHolderSettings;
import org.goplanit.utils.SettingsYamlLoaderTest.TestRootNetworkSettings;
import org.goplanit.utils.SettingsYamlLoaderTest.TestRootTransportSettings;
import org.goplanit.utils.SettingsYamlLoaderTest.TestSettings;
import org.goplanit.utils.exceptions.PlanItRunTimeException;
import org.goplanit.utils.settings.*;
import org.goplanit.utils.settings.SettingsFileContentType;
import org.goplanit.utils.settings.SettingsYamlLoader;
import org.goplanit.utils.settings.annotations.SettingsRootValue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests of {@link SettingsYamlWriter}: settings written out as YAML text, every key or only those differing from fresh
 * settings, with descriptions or without, and read back by {@link SettingsYamlLoader} into equal settings, one test
 * per scenario of the settings-config-files spec, on the settings classes of {@link SettingsYamlLoaderTest}
 *
 * @author markr
 */
public class SettingsYamlWriterTest {

  /**
   * Settings filling no section themselves, holding the test settings and passing the country they are made with on
   * to them, with no getter of the country of their own
   */
  public static final class TestCountryHolderSettings {

    /** settings of the section test */
    private final TestSettings test;

    /** Constructor without a country */
    public TestCountryHolderSettings() {
      this.test = new TestSettings();
    }

    /** @param country made with, a value at the root, passed on to the test settings */
    public TestCountryHolderSettings(
        @SettingsRootValue(key = "country", description = "The country the settings are made for") String country) {
      this.test = new TestSettings(country);
    }

    /** @return the settings of the section test */
    public TestSettings getTest() {
      return test;
    }
  }

  /** The schema of the test settings: the sections test and other */
  private static final SettingsSchema TEST_SCHEMA = new SettingsSchema(TestSettings.class, TestOtherSettings.class);

  /** The schema of the root test settings: the sections network and transport, a range at the root */
  private static final SettingsSchema ROOT_TEST_SCHEMA = new SettingsSchema(List.of(new TestRangeConverter()),
      TestRootNetworkSettings.class, TestRootTransportSettings.class);

  /**
   * Change a key of each kind of the test settings from its default, the paths and the number excepted, so the text
   * written is the same in every run
   *
   * @param settings to change
   * @return the settings changed
   */
  private static TestSettings change(TestSettings settings) {
    settings.setName("Sydney");
    settings.activateType("tertiary");
    settings.excludeId(9);
    settings.excludeId(7);
    settings.addGroup(1, 2);
    settings.overrideKind(123, UserOverride.of(TestKind.RAMP_METER));
    settings.overrideKind(456, UserOverride.of(TestKind.SIGNAL, "checked on site"));
    settings.overrideLanes(12, UserOverride.of(new TestLanes(2, 1), "imagery"));
    settings.getNested().setSteps(5);
    return settings;
  }

  /**
   * Settings read back from text written out, which starts with its header
   *
   * @param <T> type of the settings
   * @param schema of the text
   * @param settingsClass to make
   * @param text written out
   * @return the settings
   */
  private static <T> T readBack(SettingsSchema schema, Class<T> settingsClass, String text) {
    return new SettingsYamlLoader(schema, List.of(SettingsSource.ofText(text))).create(settingsClass);
  }

  /** folder for the test files */
  @TempDir
  public Path tempDir;

  /**
   * With descriptions, each section starts with its description and each key has its description above it, with what
   * it does when it is not a plain replace and the values of an enum it takes; keys and sections go by level, under a
   * heading per level, and by name within a level; a key with no value is commented out at its depth; and the
   * {@code +} and {@code -} forms of a list are left out, as the whole list is written
   */
  @Test
  public void testAllKeysWithDescriptions() {
    var text = new SettingsYamlWriter(TEST_SCHEMA, change(new TestSettings()), SettingsFileContentType.ALL_KEYS,
        true).toString();
    assertEquals("planitTestSettings: 1\n"
        + "# The country the settings are made for\n"
        + "# country:\n"
        + "test:\n"
        + "  # Settings with a key of each kind the loader handles\n"
        + "  # --- standard ---\n"
        + "  # A path\n"
        + "  # inputFile:\n"
        + "  # A location\n"
        + "  # inputSource:\n"
        + "  # A text\n"
        + "  name: Sydney\n"
        + "  # A folder\n"
        + "  # outputFolder:\n"
        + "  # Types, replacing the defaults\n"
        + "  types: [primary, secondary, tertiary]\n"
        + "  # --- common ---\n"
        + "  # A yes or no choice\n"
        + "  enabled: false\n"
        + "  # An enum value\n"
        + "  # Values: SIGNAL, RAMP_METER\n"
        + "  kind: SIGNAL\n"
        + "  # --- uncommon ---\n"
        + "  # Ids left out (adds to the list)\n"
        + "  excludedIds: [7, 9]\n"
        + "  # Groups of ids, one group per entry (adds to the list)\n"
        + "  groups:\n"
        + "    - [1, 2]\n"
        + "  # Kinds by id, in place of what the input says (adds or replaces by key)\n"
        + "  # Values: SIGNAL, RAMP_METER\n"
        + "  kindOverrides:\n"
        + "    123: RAMP_METER\n"
        + "    456: {value: SIGNAL, reason: checked on site}\n"
        + "  # Lanes by id, in place of what the input says (adds or replaces by key)\n"
        + "  lanesOverrides:\n"
        + "    12: {value: {backward: 1, forward: 2}, reason: imagery}\n"
        + "  # A distance in metres\n"
        + "  maximumReachMeters: 40.0\n"
        + "  # A whole or decimal number\n"
        + "  # threshold:\n"
        + "  # --- rare ---\n"
        + "  nested:\n"
        + "    # Settings nested in the test settings\n"
        + "    # --- rare ---\n"
        + "    # Search steps\n"
        + "    steps: 5\n"
        + "  # A whole number\n"
        + "  seed: 42\n", text);
  }

  /** Without descriptions, the keys with a value come in the same order, with no comment at all */
  @Test
  public void testAllKeysWithoutDescriptions() {
    var text = new SettingsYamlWriter(TEST_SCHEMA, change(new TestSettings()), SettingsFileContentType.ALL_KEYS,
        false).toString();
    assertEquals("planitTestSettings: 1\n"
        + "test:\n"
        + "  name: Sydney\n"
        + "  types: [primary, secondary, tertiary]\n"
        + "  enabled: false\n"
        + "  kind: SIGNAL\n"
        + "  excludedIds: [7, 9]\n"
        + "  groups:\n"
        + "    - [1, 2]\n"
        + "  kindOverrides:\n"
        + "    123: RAMP_METER\n"
        + "    456: {value: SIGNAL, reason: checked on site}\n"
        + "  lanesOverrides:\n"
        + "    12: {value: {backward: 1, forward: 2}, reason: imagery}\n"
        + "  maximumReachMeters: 40.0\n"
        + "  nested:\n"
        + "    steps: 5\n"
        + "  seed: 42\n", text);
  }

  /**
   * Settings with one key changed from its default give that key and nothing else besides the header; with
   * descriptions, only the heading of its level; unchanged settings give the header alone
   */
  @Test
  public void testOnlyTheChoicesMade() {
    var settings = new TestSettings();
    settings.setName("Sydney");
    assertEquals("planitTestSettings: 1\ntest:\n  name: Sydney\n",
        new SettingsYamlWriter(TEST_SCHEMA, settings, SettingsFileContentType.KEYS_DIFFERING_FROM_DEFAULTS, false)
            .toString());
    assertEquals("planitTestSettings: 1\n"
            + "test:\n"
            + "  # Settings with a key of each kind the loader handles\n"
            + "  # --- standard ---\n"
            + "  # A text\n"
            + "  name: Sydney\n",
        new SettingsYamlWriter(TEST_SCHEMA, settings, SettingsFileContentType.KEYS_DIFFERING_FROM_DEFAULTS, true)
            .toString());
    assertEquals("planitTestSettings: 1\n",
        new SettingsYamlWriter(TEST_SCHEMA, new TestSettings(), SettingsFileContentType.KEYS_DIFFERING_FROM_DEFAULTS,
            true).toString());
  }

  /**
   * The values the constructor took follow the header, and the fresh settings compared with are made with them, so a
   * key whose default depends on them is compared with the right default
   */
  @Test
  public void testRootValues() {
    var settings = new TestSettings("Australia");
    settings.setEnabled(true);
    assertEquals("planitTestSettings: 1\ncountry: Australia\ntest:\n  enabled: true\n",
        new SettingsYamlWriter(TEST_SCHEMA, settings, SettingsFileContentType.KEYS_DIFFERING_FROM_DEFAULTS, false)
            .toString());
  }

  /**
   * Settings holding the settings of the sections, which they pass the values they are made with on to, have their
   * values at the root read from the settings of the first section, like their keys at the root
   */
  @Test
  public void testRootValuesOfSettingsHoldingTheSections() {
    var settings = new TestCountryHolderSettings("Australia");
    settings.getTest().setEnabled(true);
    var text = new SettingsYamlWriter(TEST_SCHEMA, settings, SettingsFileContentType.KEYS_DIFFERING_FROM_DEFAULTS, false)
        .toString();
    assertEquals("planitTestSettings: 1\ncountry: Australia\ntest:\n  enabled: true\n", text);
    assertEquals("Australia", readBack(TEST_SCHEMA, TestCountryHolderSettings.class, text).getTest().getCountry());
  }

  /**
   * Settings changed in code, written out with every key, with and without descriptions, or with only the keys that
   * differ, and read back into fresh settings, give the values written out
   */
  @Test
  public void testRoundTrip() throws IOException {
    var settings = change(new TestSettings("Australia"));
    settings.setInputFile(tempDir.resolve("data/x.pbf"));
    settings.setOutputFolder(tempDir.resolve("out").toFile());
    settings.setInputSource(new URL("https://example.org/x"));
    settings.setThreshold(7.5);
    var allKeys = new SettingsYamlWriter(TEST_SCHEMA, settings, SettingsFileContentType.ALL_KEYS, false).toString();
    for (var content : SettingsFileContentType.values()) {
      for (var withDescriptions : List.of(true, false)) {
        var text = new SettingsYamlWriter(TEST_SCHEMA, settings, content, withDescriptions).toString();
        var readBack = readBack(TEST_SCHEMA, TestSettings.class, text);
        assertEquals(allKeys,
            new SettingsYamlWriter(TEST_SCHEMA, readBack, SettingsFileContentType.ALL_KEYS, false).toString());
        assertEquals("Australia", readBack.getCountry());
        assertEquals(settings.getInputFile(), readBack.getInputFile());
        assertEquals(settings.getOutputFolder(), readBack.getOutputFolder());
        assertEquals(settings.getInputSource(), readBack.getInputSource());
        assertEquals(7.5, readBack.getThreshold());
        assertEquals(List.of("primary", "secondary", "tertiary"), readBack.getTypes());
        assertEquals(Set.of(7L, 9L), readBack.getExcludedIds());
        assertArrayEquals(new long[] {1, 2}, readBack.getGroups().get(0));
        assertEquals(settings.getKindOverrides(), readBack.getKindOverrides());
        assertEquals(settings.getLanesOverrides(), readBack.getLanesOverrides());
        assertEquals(5, readBack.getNested().getSteps());
      }
    }
  }

  /**
   * A key at the root is written once, after the header, through the converter of its type, and read back into the
   * settings of every section; a section with no key of its own is left out
   */
  @Test
  public void testRootKeysAndConverters() {
    var settings = new TestRootHolderSettings();
    settings.getNetwork().setRange(new TestRange(1, 5));
    settings.getTransport().setRange(new TestRange(1, 5));
    settings.getNetwork().setName("north");
    var text = new SettingsYamlWriter(ROOT_TEST_SCHEMA, settings, SettingsFileContentType.ALL_KEYS, false).toString();
    var lines = text.lines().collect(Collectors.toList());
    assertEquals(4, lines.size());
    assertEquals(List.of("planitRootTestSettings: 1", "network:", "  name: north"),
        List.of(lines.get(0), lines.get(2), lines.get(3)));
    assertTrue(lines.get(1).startsWith("range: {"));

    var readBack = readBack(ROOT_TEST_SCHEMA, TestRootHolderSettings.class, text);
    assertEquals(new TestRange(1, 5), readBack.getNetwork().getRange());
    assertEquals(new TestRange(1, 5), readBack.getTransport().getRange());
    assertEquals("north", readBack.getNetwork().getName());
  }

  /** Settings with a key that has no getter of its whole value cannot be written out, a mistake in the code */
  @Test
  public void testKeyWithoutReadRefused() {
    var schema = new SettingsSchema(TestOrderFirstSettings.class, TestOrderSecondSettings.class);
    assertThrows(PlanItRunTimeException.class,
        () -> new SettingsYamlWriter(schema, new TestOrderHolderSettings(), SettingsFileContentType.ALL_KEYS, false));
  }

  /** A file holds the text, read back by the loader */
  @Test
  public void testFileWritten() throws IOException {
    var file = tempDir.resolve("settings.yaml");
    new SettingsYamlWriter(TEST_SCHEMA, change(new TestSettings()), SettingsFileContentType.KEYS_DIFFERING_FROM_DEFAULTS,
        true).write(file);
    var readBack = new SettingsYamlLoader(TEST_SCHEMA, List.of(SettingsSource.ofFile(file))).create(TestSettings.class);
    assertEquals("Sydney", readBack.getName());
    assertEquals(Map.of(12L, UserOverride.of(new TestLanes(2, 1), "imagery")), readBack.getLanesOverrides());
  }
}
