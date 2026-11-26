package org.goplanit.utils;

import org.goplanit.utils.SettingsYamlLoaderTest.TestFlatNestedSettings;
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
 * each of the nested settings they hold, which the loader reads back
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

  /** Settings that have settings files, holding nested settings whose settings file a key names */
  public static final class TestYamlHolderSettings implements YamlWriteSettingsSupport {

    /** the layout of the settings files of these settings and their nested settings */
    private static final SettingsSchema SCHEMA = new SettingsSchema(TestFlatSettings.class);

    /** the nested network settings */
    private final TestFlatNestedSettings network = new TestFlatNestedSettings();

    /** @return the nested network settings */
    @SettingsFileReference(key = "network", level = SettingsKeyLevel.STANDARD,
        description = "The settings file of the nested network settings")
    public TestFlatNestedSettings getNetwork() {
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
   * Settings holding nested settings write their file naming the file of the nested settings, and that file next to
   * it, named after the file naming it followed by the key, holding the key changed, which the loader reads back
   */
  @Test
  public void nestedSettingsWrittenNextToTheFileNamingThem() throws IOException {
    var settings = new TestYamlHolderSettings();
    settings.getNetwork().setFileName("roads.xml");
    var file = tempDir.resolve("holder.yaml");
    settings.writeSettingsFile(file, SettingsFileContentType.KEYS_DIFFERING_FROM_DEFAULTS, false);
    assertEquals("planitFlatTestSettings: 1\nnetwork: holder_network.yaml\n", Files.readString(file));
    assertEquals("planitFlatTestSettings: 1\nfileName: roads.xml\n",
        Files.readString(file.resolveSibling("holder_network.yaml")));

    var readBack = new SettingsYamlLoader(settings.getSettingsSchema(), List.of(SettingsSource.ofFile(file)))
        .create(TestYamlHolderSettings.class);
    assertEquals("roads.xml", readBack.getNetwork().getFileName());
  }
}
