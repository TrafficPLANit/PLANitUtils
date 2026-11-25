package org.goplanit.utils;

import org.goplanit.utils.exceptions.PlanItRunTimeException;
import org.goplanit.utils.settings.SettingsSchema;
import org.goplanit.utils.settings.SettingsValueConverter;
import org.goplanit.utils.settings.annotations.SettingsHeader;
import org.goplanit.utils.settings.annotations.SettingsKeyLevel;
import org.goplanit.utils.settings.annotations.SettingsSection;
import org.goplanit.utils.yaml.SimpleYamlReader;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests of {@link SettingsSchema}: the section of settings found from their class, the header from a class they
 * extend, the schema of settings filling no section, the converter of a type, and the classes a schema refuses because
 * it could not tell their section or header
 *
 * @author markr
 */
public class SettingsSchemaTest {

  /** Base of the test settings, giving their header */
  @SettingsHeader(key = "planitTestSettings", version = 1)
  public static abstract class TestBaseSettings {
  }

  /** Settings of the section network */
  @SettingsSection(key = "network", level = SettingsKeyLevel.STANDARD, description = "Settings of the network")
  public static class TestNetworkSettings extends TestBaseSettings {
  }

  /** Settings of the section zoning */
  @SettingsSection(key = "zoning", level = SettingsKeyLevel.COMMON, description = "Settings of the zoning")
  public static class TestZoningSettings extends TestBaseSettings {
  }

  /** A subclass of the network settings, filling the same section */
  public static class TestCityNetworkSettings extends TestNetworkSettings {
  }

  /** A subclass of the network settings naming a section of its own, which a schema with both cannot tell apart */
  @SettingsSection(key = "cityNetwork", level = SettingsKeyLevel.RARE, description = "Settings of the city's network")
  public static class TestOwnSectionCityNetworkSettings extends TestNetworkSettings {
  }

  /** Settings naming the section network as well */
  @SettingsSection(key = "network", level = SettingsKeyLevel.RARE, description = "Settings of the network, named again")
  public static class TestSecondNetworkSettings extends TestBaseSettings {
  }

  /** Settings naming no section */
  public static class TestNoSectionSettings extends TestBaseSettings {
  }

  /** Settings naming a section, without a header */
  @SettingsSection(key = "other", level = SettingsKeyLevel.RARE, description = "Settings of another section")
  public static class TestNoHeaderSettings {
  }

  /** Settings naming a section, with another header */
  @SettingsHeader(key = "planitOtherSettings", version = 1)
  @SettingsSection(key = "other", level = SettingsKeyLevel.RARE, description = "Settings of another section")
  public static class TestOtherHeaderSettings {
  }

  /** A code, the value a converter of a schema makes */
  public static final class TestCode {

    /** the code as text */
    private final String text;

    /** @param text the code as text */
    public TestCode(String text) {
      this.text = text;
    }

    /** @return the code as text */
    public String getText() {
      return text;
    }
  }

  /** Converter of a code written as text */
  public static final class TestCodeConverter implements SettingsValueConverter<TestCode> {

    @Override
    public Class<TestCode> getValueType() {
      return TestCode.class;
    }

    @Override
    public TestCode fromPlainValue(Object plainValue) {
      return new TestCode(SimpleYamlReader.asText(plainValue));
    }

    @Override
    public Object toPlainValue(TestCode code) {
      return code.getText();
    }
  }

  /** A section is found from its class or a subclass, and the header from a class the settings extend */
  @Test
  public void sectionFoundFromItsClass() {
    var schema = new SettingsSchema(TestNetworkSettings.class, TestZoningSettings.class);

    assertEquals("network", schema.findSectionKeyOf(TestNetworkSettings.class));
    assertEquals("network", schema.findSectionKeyOf(TestCityNetworkSettings.class));
    assertEquals("zoning", schema.findSectionKeyOf(TestZoningSettings.class));
    assertNull(schema.findSectionKeyOf(TestNoSectionSettings.class));
    assertTrue(schema.isSectionKey("zoning"));
    assertEquals("planitTestSettings", schema.getHeader().key());
    assertEquals(1, schema.getHeader().version());
  }

  /** Settings filling no section have a schema of their one class: the header it gives, and no section */
  @Test
  public void schemaOfSettingsFillingNoSection() {
    var schema = new SettingsSchema(TestNoSectionSettings.class);

    assertEquals("planitTestSettings", schema.getHeader().key());
    assertTrue(schema.getSectionKeys().isEmpty());
    assertTrue(schema.getSectionSettingsOf(new TestNoSectionSettings()).isEmpty());
  }

  /**
   * Classes whose section or header the schema cannot tell are refused, being a mistake in the code: among them a
   * class filling no section next to classes of sections, and one class giving no header
   */
  @Test
  public void classesItCannotTellRefused() {
    assertThrows(PlanItRunTimeException.class, () -> new SettingsSchema());
    assertThrows(PlanItRunTimeException.class,
        () -> new SettingsSchema(TestNetworkSettings.class, TestNoSectionSettings.class));
    assertThrows(PlanItRunTimeException.class, () -> new SettingsSchema(TestCode.class));
    assertThrows(PlanItRunTimeException.class, () -> new SettingsSchema(TestNoHeaderSettings.class));
    assertThrows(PlanItRunTimeException.class,
        () -> new SettingsSchema(TestNetworkSettings.class, TestOtherHeaderSettings.class));
    assertThrows(PlanItRunTimeException.class,
        () -> new SettingsSchema(TestNetworkSettings.class, TestOwnSectionCityNetworkSettings.class));
    assertThrows(PlanItRunTimeException.class,
        () -> new SettingsSchema(TestNetworkSettings.class, TestSecondNetworkSettings.class));
  }

  /** A converter is found by the type it makes, none for another type; two converters of one type are refused */
  @Test
  public void converterFoundByItsType() {
    var converter = new TestCodeConverter();
    var schema = new SettingsSchema(List.of(converter), TestNetworkSettings.class);

    assertSame(converter, schema.findConverter(TestCode.class));
    assertNull(schema.findConverter(String.class));
    assertThrows(PlanItRunTimeException.class,
        () -> new SettingsSchema(List.of(converter, new TestCodeConverter()), TestNetworkSettings.class));
  }
}
