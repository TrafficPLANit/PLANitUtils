package org.goplanit.utils;

import org.goplanit.utils.settings.SettingsCoverage;
import org.goplanit.utils.settings.annotations.CoveredBySettingsKey;
import org.goplanit.utils.settings.annotations.DelegatedToSyncedInternalSettingsKeys;
import org.goplanit.utils.settings.annotations.MarksGetterOfNestedSettings;
import org.goplanit.utils.settings.annotations.MarksMethodChangingSettings;
import org.goplanit.utils.settings.annotations.SettingsFileReference;
import org.goplanit.utils.settings.annotations.SettingsKeyLevel;
import org.goplanit.utils.settings.annotations.SettingsRead;
import org.goplanit.utils.settings.annotations.SettingsWrite;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests of {@link SettingsCoverage}: settings whose annotations cover them have nothing reported, and each kind of gap
 * is reported by the method or key it is found on
 *
 * @author markr
 */
public class SettingsCoverageTest {

  /** Settings whose annotations cover them: a key with its read, and a convenience covered by that key */
  public static final class TestCoveredSettings {

    /** a name */
    private String name;

    /** @return the name */
    @SettingsRead(key = "name")
    public String getName() {
      return name;
    }

    /** @param name the name */
    @SettingsWrite(key = "name", level = SettingsKeyLevel.STANDARD, description = "A name")
    public void setName(String name) {
      this.name = name;
    }

    /** @param name the name, in capitals */
    @CoveredBySettingsKey("name")
    public void setNameInCapitals(String name) {
      setName(name.toUpperCase());
    }
  }

  /**
   * Settings with one gap of each kind: a setter without a marker, a getter of nested settings without one, a key
   * without a read, and a convenience covered by a key there is not
   */
  public static final class TestUncoveredSettings {

    /** the nested settings */
    private final TestCoveredSettings nested = new TestCoveredSettings();

    /** a number */
    private int number;

    /** @return the nested settings */
    public TestCoveredSettings getNested() {
      return nested;
    }

    /** @param number the number */
    public void setUnmarked(int number) {
      this.number = number;
    }

    /** @param number the number, written but not read */
    @SettingsWrite(key = "number", level = SettingsKeyLevel.STANDARD, description = "A number")
    public void setNumber(int number) {
      this.number = number;
    }

    /** @param text the number, as text */
    @CoveredBySettingsKey("none")
    public void setNumberFromText(String text) {
      setNumber(Integer.parseInt(text));
    }
  }

  /** Settings of an internal part, with an output folder and no country */
  public static final class TestPartSettings {

    /** a folder */
    private String outputDirectory;

    /** @return the folder */
    @SettingsRead(key = "outputDirectory")
    public String getOutputDirectory() {
      return outputDirectory;
    }

    /** @param outputDirectory the folder */
    @SettingsWrite(key = "outputDirectory", level = SettingsKeyLevel.STANDARD, description = "A folder")
    public void setOutputDirectory(String outputDirectory) {
      this.outputDirectory = outputDirectory;
    }
  }

  /**
   * Settings holding settings internally, made from the file a key names, setting their output folder, which they
   * take, and their country, which they do not
   */
  public static final class TestHolderSettings {

    /** the settings held */
    private final TestPartSettings part = new TestPartSettings();

    /** @return the settings held */
    @SettingsFileReference(key = "part", level = SettingsKeyLevel.STANDARD, description = "The file of the part")
    public TestPartSettings getPart() {
      return part;
    }

    /** @param outputDirectory the folder of the settings held */
    @DelegatedToSyncedInternalSettingsKeys("outputDirectory")
    public void setOutputDirectory(String outputDirectory) {
      part.setOutputDirectory(outputDirectory);
    }

    /** @param country ignored, the settings held having none */
    @DelegatedToSyncedInternalSettingsKeys("country")
    public void setCountry(String country) {
      /* nothing to set */
    }
  }

  /** Settings marking a method as delegated to settings held internally, while holding none */
  public static final class TestHolderOfNothingSettings {

    /** @param outputDirectory ignored */
    @DelegatedToSyncedInternalSettingsKeys("outputDirectory")
    public void setOutputDirectory(String outputDirectory) {
      /* nothing to set */
    }
  }

  /** What test settings with a count do, declared by an interface that is not settings */
  public interface TestCounting {

    /** Count again from zero */
    public abstract void restartCounting();
  }

  /**
   * Settings whose methods are told apart without lists of names: a key and a section marked with annotations known by
   * their mark alone, a method of an interface that is not settings, a fluent setter named with any verb, and a logging
   * method of their own, the last two needing a mark like any other method changing settings
   */
  public static final class TestUnlistedSettings implements TestCounting {

    /** the nested settings */
    private final TestCoveredSettings details = new TestCoveredSettings();

    /** a scale */
    private double scale;

    /** a count */
    private int count;

    /** @return the nested settings */
    @TestSection
    public TestCoveredSettings getDetails() {
      return details;
    }

    /**
     * @param name of the nested settings
     * @return the nested settings of that name, these being the only ones, null for any other name
     */
    public TestCoveredSettings getDetailsNamed(String name) {
      return name.equals(details.getName()) ? details : null;
    }

    /** @param count the count */
    @TestKey
    public void setCount(int count) {
      this.count = count;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void restartCounting() {
      count = 0;
    }

    /**
     * @param scale the scale
     * @return these settings
     */
    public TestUnlistedSettings applyScale(double scale) {
      this.scale = scale;
      return this;
    }

    /** Log the scale and the count */
    public void logScaleAndCount() {
      /* nothing to log in a test */
    }
  }

  /** A key annotation of the tests' own, known to the checks by its mark alone */
  @MarksMethodChangingSettings
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.METHOD)
  public @interface TestKey {
  }

  /** A section annotation of the tests' own, known to the checks by its mark alone */
  @MarksGetterOfNestedSettings
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.METHOD)
  public @interface TestSection {
  }

  /**
   * The annotation types declared in the package of a class, read from the folder of its compiled classes, as the tests
   * run them
   *
   * @param member a class of the package
   * @return the annotation types, in no particular order
   * @throws IOException when the folder cannot be listed
   * @throws URISyntaxException never, the folder being found by the class loader
   */
  private static List<Class<?>> findAnnotationTypesInPackageOf(Class<?> member) throws IOException, URISyntaxException {
    var packageName = member.getPackageName();
    var folder = Path.of(member.getClassLoader().getResource(packageName.replace('.', '/')).toURI());
    try (var files = Files.list(folder)) {
      return files.map(file -> file.getFileName().toString())
          .filter(name -> name.endsWith(".class") && !name.contains("$"))
          .map(name -> loadClass(packageName + "." + name.substring(0, name.length() - ".class".length())))
          .filter(Class::isAnnotation).collect(Collectors.toList());
    }
  }

  /**
   * A class by its name, as the class loader of the tests has it
   *
   * @param className in full
   * @return the class
   */
  private static Class<?> loadClass(String className) {
    try {
      return Class.forName(className);
    } catch (ClassNotFoundException e) {
      throw new IllegalStateException(className, e);
    }
  }

  /**
   * Verify whether an annotation type can be written on methods: its target includes methods, or it has no target,
   * which allows any declaration
   *
   * @param annotationType to check
   * @return true when it can
   */
  private static boolean isWrittenOnMethods(Class<?> annotationType) {
    var target = annotationType.getAnnotation(Target.class);
    return target == null || Arrays.asList(target.value()).contains(ElementType.METHOD);
  }

  /** Settings whose annotations cover them have nothing reported */
  @Test
  public void coveredSettingsHaveNothingReported() {
    var coverage = SettingsCoverage.of(TestCoveredSettings.class);
    assertEquals(List.of(), coverage.findUnmarkedMethodsChangingSettings());
    assertEquals(List.of(), coverage.findUnmarkedGettersOfNestedSettings());
    assertEquals(List.of(), coverage.findKeysWithoutRead());
    assertEquals(List.of(), coverage.findUnknownCoveringKeys());
  }

  /** Each kind of gap is reported once, by the method or key it is found on */
  @Test
  public void eachGapReported() {
    var coverage = SettingsCoverage.of(TestUncoveredSettings.class);
    assertEquals(List.of("TestUncoveredSettings#setUnmarked(int)"), coverage.findUnmarkedMethodsChangingSettings());
    assertEquals(List.of("TestUncoveredSettings#getNested()"), coverage.findUnmarkedGettersOfNestedSettings());
    assertEquals(List.of("TestUncoveredSettings: number"), coverage.findKeysWithoutRead());
    assertEquals(List.of("TestUncoveredSettings#setNumberFromText(String)"), coverage.findUnknownCoveringKeys());
  }

  /**
   * A getter naming the file of settings held internally marks that getter, and a method delegated to their keys is
   * covered when every one of them takes the key; a key they do not take, or holding no such settings, is reported
   */
  @Test
  public void delegatedToSettingsHeldInternally() {
    var coverage = SettingsCoverage.of(TestHolderSettings.class, TestHolderOfNothingSettings.class);
    assertEquals(List.of(), coverage.findUnmarkedMethodsChangingSettings());
    assertEquals(List.of(), coverage.findUnmarkedGettersOfNestedSettings());
    assertEquals(
        List.of("TestHolderOfNothingSettings#setOutputDirectory(String)", "TestHolderSettings#setCountry(String)"),
        coverage.findUnknownCoveringKeys());
  }

  /**
   * Methods are told apart without lists of names: annotations count by their mark, a method of an interface that is
   * not settings is no key, a getter looking up nested settings by an argument changes nothing, and a fluent setter
   * named with any verb and a logging method of the settings' own are reported until marked
   */
  @Test
  public void methodsToldApartWithoutListsOfNames() {
    var coverage = SettingsCoverage.of(TestUnlistedSettings.class);
    assertEquals(List.of("TestUnlistedSettings#applyScale(double)", "TestUnlistedSettings#logScaleAndCount()"),
        coverage.findUnmarkedMethodsChangingSettings());
    assertEquals(List.of(), coverage.findUnmarkedGettersOfNestedSettings());
  }

  /**
   * Every annotation of the settings annotations package that can be written on methods carries the mark of what it
   * says, {@link MarksMethodChangingSettings} or {@link MarksGetterOfNestedSettings}, as the checks know annotations by
   * those marks alone: an annotation added there without a mark fails this test by its name, so whoever adds it decides
   * which mark it carries. Annotations of classes or parameters, and the marks themselves, are left out by their target
   *
   * @throws IOException when the package's classes cannot be listed
   * @throws URISyntaxException never, the package being found by the class loader
   */
  @Test
  public void everySettingsAnnotationOfMethodsCarriesAMark() throws IOException, URISyntaxException {
    var unmarked = findAnnotationTypesInPackageOf(SettingsWrite.class).stream()
        .filter(annotationType -> isWrittenOnMethods(annotationType) &&
            !annotationType.isAnnotationPresent(MarksMethodChangingSettings.class) &&
            !annotationType.isAnnotationPresent(MarksGetterOfNestedSettings.class))
        .map(Class::getSimpleName).sorted().collect(Collectors.toList());
    assertEquals(List.of(), unmarked);
  }
}
