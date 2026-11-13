package org.goplanit.utils;

import org.goplanit.utils.exceptions.PlanItRunTimeException;
import org.goplanit.utils.settings.SettingsSchema;
import org.goplanit.utils.settings.annotations.SettingsHeader;
import org.goplanit.utils.settings.annotations.SettingsKeyLevel;
import org.goplanit.utils.settings.annotations.SettingsSection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests of {@link SettingsSchema}: the section of settings found from their class, the header from a class they
 * extend, and the classes a schema refuses because it could not tell their section or header
 *
 * @author markr
 */
public class SettingsSchemaTest {

  /** Base of the test settings, giving their header */
  @SettingsHeader(key = "planitTestSettings", version = 1)
  public static abstract class TestBaseSettings {
  }

  /** Settings of the section network */
  @SettingsSection(key = "network", level = SettingsKeyLevel.STANDARD)
  public static class TestNetworkSettings extends TestBaseSettings {
  }

  /** Settings of the section zoning */
  @SettingsSection(key = "zoning", level = SettingsKeyLevel.COMMON)
  public static class TestZoningSettings extends TestBaseSettings {
  }

  /** A subclass of the network settings, filling the same section */
  public static class TestCityNetworkSettings extends TestNetworkSettings {
  }

  /** A subclass of the network settings naming a section of its own, which a schema with both cannot tell apart */
  @SettingsSection(key = "cityNetwork", level = SettingsKeyLevel.RARE)
  public static class TestOwnSectionCityNetworkSettings extends TestNetworkSettings {
  }

  /** Settings naming the section network as well */
  @SettingsSection(key = "network", level = SettingsKeyLevel.RARE)
  public static class TestSecondNetworkSettings extends TestBaseSettings {
  }

  /** Settings naming no section */
  public static class TestNoSectionSettings extends TestBaseSettings {
  }

  /** Settings naming a section, without a header */
  @SettingsSection(key = "other", level = SettingsKeyLevel.RARE)
  public static class TestNoHeaderSettings {
  }

  /** Settings naming a section, with another header */
  @SettingsHeader(key = "planitOtherSettings", version = 1)
  @SettingsSection(key = "other", level = SettingsKeyLevel.RARE)
  public static class TestOtherHeaderSettings {
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

  /** Classes whose section or header the schema cannot tell are refused, being a mistake in the code */
  @Test
  public void classesItCannotTellRefused() {
    assertThrows(PlanItRunTimeException.class, () -> new SettingsSchema());
    assertThrows(PlanItRunTimeException.class, () -> new SettingsSchema(TestNoSectionSettings.class));
    assertThrows(PlanItRunTimeException.class, () -> new SettingsSchema(TestNoHeaderSettings.class));
    assertThrows(PlanItRunTimeException.class,
        () -> new SettingsSchema(TestNetworkSettings.class, TestOtherHeaderSettings.class));
    assertThrows(PlanItRunTimeException.class,
        () -> new SettingsSchema(TestNetworkSettings.class, TestOwnSectionCityNetworkSettings.class));
    assertThrows(PlanItRunTimeException.class,
        () -> new SettingsSchema(TestNetworkSettings.class, TestSecondNetworkSettings.class));
  }
}
