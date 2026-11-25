package org.goplanit.utils;

import org.goplanit.utils.SettingsYamlLoaderTest.TestFlatPartSettings;
import org.goplanit.utils.SettingsYamlLoaderTest.TestFlatSettings;
import org.goplanit.utils.settings.SettingsFileContentType;
import org.goplanit.utils.settings.SettingsSchema;
import org.goplanit.utils.settings.SettingsSource;
import org.goplanit.utils.settings.SettingsYamlLoader;
import org.goplanit.utils.settings.YamlWriteSettingsSupport;
import org.goplanit.utils.settings.annotations.SettingsFileReference;
import org.goplanit.utils.settings.annotations.SettingsHeader;
import org.goplanit.utils.settings.annotations.SettingsKeyLevel;
import org.goplanit.utils.settings.annotations.SettingsRead;
import org.goplanit.utils.settings.annotations.SettingsSection;
import org.goplanit.utils.settings.annotations.SettingsWrite;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests of {@link YamlWriteSettingsSupport}: settings that have settings files write one, with a file of its own for
 * each part they hold, which the loader reads back
 *
 * @author markr
 */
public class YamlWriteSettingsSupportTest {

  /** Settings that have settings files, with one key */
  @SettingsHeader(key = "planitYamlSupportTestSettings", version = 1)
  @SettingsSection(key = "test", level = SettingsKeyLevel.STANDARD, description = "Settings that have settings files")
  public static final class TestYamlSettings implements YamlWriteSettingsSupport {

    /** the layout of the settings files of these settings */
    private static final SettingsSchema SCHEMA = new SettingsSchema(TestYamlSettings.class);

    /** a text */
    private String name = "default";

    /** @return the text */
    @SettingsRead(key = "name")
    public String getName() {
      return name;
    }

    /** @param name the text */
    @SettingsWrite(key = "name", level = SettingsKeyLevel.STANDARD, description = "A text")
    public void setName(String name) {
      this.name = name;
    }

    @Override
    public SettingsSchema getSettingsSchema() {
      return SCHEMA;
    }
  }

  /** Settings that have settings files, holding a part whose settings file a key names */
  public static final class TestYamlHolderSettings implements YamlWriteSettingsSupport {

    /** the layout of the settings files of these settings and their part */
    private static final SettingsSchema SCHEMA = new SettingsSchema(TestFlatSettings.class);

    /** settings of the network part */
    private final TestFlatPartSettings network = new TestFlatPartSettings();

    /** @return the settings of the network part */
    @SettingsFileReference(key = "network", level = SettingsKeyLevel.STANDARD,
        description = "The settings file of the network part")
    public TestFlatPartSettings getNetwork() {
      return network;
    }

    @Override
    public SettingsSchema getSettingsSchema() {
      return SCHEMA;
    }
  }

  /** folder for the test files */
  @TempDir
  public Path tempDir;

  /** Settings that have settings files write one holding the key changed, which the loader reads back */
  @Test
  public void settingsFileWrittenAndReadBack() throws IOException {
    var settings = new TestYamlSettings();
    settings.setName("north");
    var file = tempDir.resolve("settings.yaml");
    settings.writeSettingsFile(file, SettingsFileContentType.KEYS_DIFFERING_FROM_DEFAULTS, false);
    assertEquals("planitYamlSupportTestSettings: 1\ntest:\n  name: north\n", Files.readString(file));

    var readBack = new SettingsYamlLoader(settings.getSettingsSchema(), List.of(SettingsSource.ofFile(file)))
        .create(TestYamlSettings.class);
    assertEquals("north", readBack.getName());
  }

  /**
   * Settings holding a part write their file naming the part's file, and the part's file next to it holding the key
   * changed, which the loader reads back
   */
  @Test
  public void partWrittenNextToTheFileNamingIt() throws IOException {
    var settings = new TestYamlHolderSettings();
    settings.getNetwork().setFileName("roads.xml");
    var file = tempDir.resolve("holder.yaml");
    settings.writeSettingsFile(file, SettingsFileContentType.KEYS_DIFFERING_FROM_DEFAULTS, false);
    assertEquals("planitFlatTestSettings: 1\nnetwork: network.yaml\n", Files.readString(file));
    assertEquals("planitFlatTestSettings: 1\nfileName: roads.xml\n",
        Files.readString(file.resolveSibling("network.yaml")));

    var readBack = new SettingsYamlLoader(settings.getSettingsSchema(), List.of(SettingsSource.ofFile(file)))
        .create(TestYamlHolderSettings.class);
    assertEquals("roads.xml", readBack.getNetwork().getFileName());
  }
}
