package org.goplanit.utils;

import org.goplanit.utils.exceptions.PlanItRunTimeException;
import org.goplanit.utils.exceptions.SettingsProblemsException;
import org.goplanit.utils.settings.SettingsSchema;
import org.goplanit.utils.settings.problem.SettingsProblem;
import org.goplanit.utils.settings.problem.SettingsProblemKind;
import org.goplanit.utils.settings.SettingsSource;
import org.goplanit.utils.settings.SettingsYamlLoader;
import org.goplanit.utils.settings.UserOverride;
import org.goplanit.utils.settings.annotations.SettingsHeader;
import org.goplanit.utils.settings.annotations.SettingsKeyEffect;
import org.goplanit.utils.settings.annotations.SettingsKeyLevel;
import org.goplanit.utils.settings.annotations.SettingsRead;
import org.goplanit.utils.settings.annotations.SettingsRootValue;
import org.goplanit.utils.settings.annotations.SettingsSection;
import org.goplanit.utils.settings.annotations.SettingsWrite;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests of {@link SettingsYamlLoader}: YAML files and text filling settings through their annotated methods, one test
 * per scenario of the settings-config-files spec, on small settings classes made for the test
 *
 * @author markr
 */
public class SettingsYamlLoaderTest {

  /** Kinds a test setting can take */
  public enum TestKind {
    SIGNAL,
    RAMP_METER
  }

  /** Lanes in two directions: a small section, used as the value of a map key */
  public static final class TestLanes {

    /** lanes forward, null when not given */
    private Integer forward;

    /** lanes backward, null when not given */
    private Integer backward;

    /** Constructor without lanes */
    public TestLanes() {
    }

    /**
     * Constructor
     *
     * @param forward lanes forward
     * @param backward lanes backward
     */
    public TestLanes(Integer forward, Integer backward) {
      this.forward = forward;
      this.backward = backward;
    }

    /** @return lanes forward */
    @SettingsRead(key = "forward")
    public Integer getForward() {
      return forward;
    }

    /** @param forward lanes forward */
    @SettingsWrite(key = "forward", level = SettingsKeyLevel.UNCOMMON, description = "Lanes forward")
    public void setForward(Integer forward) {
      this.forward = forward;
    }

    /** @return lanes backward */
    @SettingsRead(key = "backward")
    public Integer getBackward() {
      return backward;
    }

    /** @param backward lanes backward */
    @SettingsWrite(key = "backward", level = SettingsKeyLevel.UNCOMMON, description = "Lanes backward")
    public void setBackward(Integer backward) {
      this.backward = backward;
    }

    @Override
    public boolean equals(Object other) {
      return other instanceof TestLanes && Objects.equals(forward, ((TestLanes) other).forward) &&
          Objects.equals(backward, ((TestLanes) other).backward);
    }

    @Override
    public int hashCode() {
      return Objects.hash(forward, backward);
    }
  }

  /** Nested settings, the section {@code nested} of the test settings */
  public static final class TestNestedSettings {

    /** number of search steps, at least one */
    private int steps = 3;

    /** @return number of search steps */
    @SettingsRead(key = "steps")
    public int getSteps() {
      return steps;
    }

    /** @param steps number of search steps, at least one */
    @SettingsWrite(key = "steps", level = SettingsKeyLevel.RARE, description = "Search steps")
    public void setSteps(int steps) {
      if (steps < 1) {
        throw new IllegalArgumentException("Steps must be at least one, found " + steps);
      }
      this.steps = steps;
    }
  }

  /** Settings with a key of each kind the loader handles, the section {@code test}, made with a country or without */
  @SettingsHeader(key = "planitTestSettings", version = 1)
  @SettingsSection(key = "test", level = SettingsKeyLevel.STANDARD)
  public static final class TestSettings {

    /** Default of the reach */
    public static final double DEFAULT_MAXIMUM_REACH_METERS = 40;

    /** the country made with, null when made without */
    private String country;

    /** a distance */
    private double maximumReachMeters = DEFAULT_MAXIMUM_REACH_METERS;

    /** a text */
    private String name = "default";

    /** a yes or no choice */
    private boolean enabled = false;

    /** a whole number */
    private long seed = 42;

    /** an enum value */
    private TestKind kind = TestKind.SIGNAL;

    /** a path */
    private Path inputFile;

    /** a folder */
    private File outputFolder;

    /** a location, on disk or on the web */
    private URL inputSource;

    /** a number, whole or decimal */
    private Number threshold;

    /** a list replaced whole, or added to and removed from */
    private final List<String> types = new ArrayList<>(List.of("primary", "secondary"));

    /** ids added one at a time */
    private final Set<Long> excludedIds = new TreeSet<>();

    /** groups of ids added one group at a time */
    private final List<long[]> groups = new ArrayList<>();

    /** kinds overridden by id */
    private final Map<Long, UserOverride<TestKind>> kindOverrides = new TreeMap<>();

    /** lanes overridden by id */
    private final Map<Long, UserOverride<TestLanes>> lanesOverrides = new TreeMap<>();

    /** nested settings */
    private final TestNestedSettings nested = new TestNestedSettings();

    /** Constructor without a country */
    public TestSettings() {
    }

    /** @param country made with, a value at the root */
    public TestSettings(@SettingsRootValue(key = "country") String country) {
      this.country = country;
    }

    /** @return the country made with, null when made without */
    public String getCountry() {
      return country;
    }

    /** @return the distance */
    @SettingsRead(key = "maximumReachMeters")
    public double getMaximumReachMeters() {
      return maximumReachMeters;
    }

    /** @param maximumReachMeters the distance */
    @SettingsWrite(key = "maximumReachMeters", formerKeys = {"maximumReachInMeters"}, level = SettingsKeyLevel.UNCOMMON,
        description = "A distance in metres")
    public void setMaximumReachMeters(double maximumReachMeters) {
      this.maximumReachMeters = maximumReachMeters;
    }

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

    /** @return the choice */
    @SettingsRead(key = "enabled")
    public boolean isEnabled() {
      return enabled;
    }

    /** @param enabled the choice */
    @SettingsWrite(key = "enabled", level = SettingsKeyLevel.COMMON, description = "A yes or no choice")
    public void setEnabled(boolean enabled) {
      this.enabled = enabled;
    }

    /** @return the whole number */
    @SettingsRead(key = "seed")
    public long getSeed() {
      return seed;
    }

    /** @param seed the whole number */
    @SettingsWrite(key = "seed", level = SettingsKeyLevel.RARE, description = "A whole number")
    public void setSeed(long seed) {
      this.seed = seed;
    }

    /** @return the enum value */
    @SettingsRead(key = "kind")
    public TestKind getKind() {
      return kind;
    }

    /** @param kind the enum value */
    @SettingsWrite(key = "kind", level = SettingsKeyLevel.COMMON, description = "An enum value")
    public void setKind(TestKind kind) {
      this.kind = kind;
    }

    /** @return the path */
    @SettingsRead(key = "inputFile")
    public Path getInputFile() {
      return inputFile;
    }

    /** @param inputFile the path */
    @SettingsWrite(key = "inputFile", level = SettingsKeyLevel.STANDARD, description = "A path")
    public void setInputFile(Path inputFile) {
      this.inputFile = inputFile;
    }

    /** @return the folder */
    @SettingsRead(key = "outputFolder")
    public File getOutputFolder() {
      return outputFolder;
    }

    /** @param outputFolder the folder */
    @SettingsWrite(key = "outputFolder", level = SettingsKeyLevel.STANDARD, description = "A folder")
    public void setOutputFolder(File outputFolder) {
      this.outputFolder = outputFolder;
    }

    /** @return the location */
    @SettingsRead(key = "inputSource")
    public URL getInputSource() {
      return inputSource;
    }

    /** @param inputSource the location */
    @SettingsWrite(key = "inputSource", level = SettingsKeyLevel.STANDARD, description = "A location")
    public void setInputSource(URL inputSource) {
      this.inputSource = inputSource;
    }

    /** @return the number */
    @SettingsRead(key = "threshold")
    public Number getThreshold() {
      return threshold;
    }

    /** @param threshold the number */
    @SettingsWrite(key = "threshold", level = SettingsKeyLevel.UNCOMMON, description = "A whole or decimal number")
    public void setThreshold(Number threshold) {
      this.threshold = threshold;
    }

    /** @return the types */
    @SettingsRead(key = "types")
    public List<String> getTypes() {
      return Collections.unmodifiableList(types);
    }

    /** @param types replacing the types */
    @SettingsWrite(key = "types", level = SettingsKeyLevel.STANDARD, description = "Types, replacing the defaults")
    public void setTypes(List<String> types) {
      this.types.clear();
      this.types.addAll(types);
    }

    /** @param type to add */
    @SettingsWrite(key = "types+", effect = SettingsKeyEffect.ADD_EACH, level = SettingsKeyLevel.STANDARD,
        description = "Types on top")
    public void activateType(String type) {
      if (!types.contains(type)) {
        types.add(type);
      }
    }

    /** @param type to remove */
    @SettingsWrite(key = "types-", effect = SettingsKeyEffect.REMOVE_EACH, level = SettingsKeyLevel.STANDARD,
        description = "Types left out")
    public void deactivateType(String type) {
      types.remove(type);
    }

    /** @return the excluded ids */
    @SettingsRead(key = "excludedIds")
    public Set<Long> getExcludedIds() {
      return Collections.unmodifiableSet(excludedIds);
    }

    /** @param id to exclude */
    @SettingsWrite(key = "excludedIds", effect = SettingsKeyEffect.ADD_EACH, level = SettingsKeyLevel.UNCOMMON,
        description = "Ids left out")
    public void excludeId(long id) {
      excludedIds.add(id);
    }

    /** @return the groups */
    @SettingsRead(key = "groups")
    public List<long[]> getGroups() {
      return Collections.unmodifiableList(groups);
    }

    /** @param ids of one group */
    @SettingsWrite(key = "groups", effect = SettingsKeyEffect.ADD_EACH, level = SettingsKeyLevel.UNCOMMON,
        description = "Groups of ids, one group per entry")
    public void addGroup(long... ids) {
      groups.add(ids);
    }

    /** @return the kinds overridden */
    @SettingsRead(key = "kindOverrides")
    public Map<Long, UserOverride<TestKind>> getKindOverrides() {
      return Collections.unmodifiableMap(kindOverrides);
    }

    /**
     * @param id to override
     * @param kind to give it
     */
    @SettingsWrite(key = "kindOverrides", effect = SettingsKeyEffect.PUT_EACH, level = SettingsKeyLevel.UNCOMMON,
        description = "Kinds by id, in place of what the input says")
    public void overrideKind(long id, UserOverride<TestKind> kind) {
      kindOverrides.put(id, kind);
    }

    /** @return the lanes overridden */
    @SettingsRead(key = "lanesOverrides")
    public Map<Long, UserOverride<TestLanes>> getLanesOverrides() {
      return Collections.unmodifiableMap(lanesOverrides);
    }

    /**
     * @param id to override
     * @param lanes to give it
     */
    @SettingsWrite(key = "lanesOverrides", effect = SettingsKeyEffect.PUT_EACH, level = SettingsKeyLevel.UNCOMMON,
        description = "Lanes by id, in place of what the input says")
    public void overrideLanes(long id, UserOverride<TestLanes> lanes) {
      lanesOverrides.put(id, lanes);
    }

    /** @return the nested settings */
    @SettingsSection(key = "nested", level = SettingsKeyLevel.RARE)
    public TestNestedSettings getNested() {
      return nested;
    }
  }

  /** Settings of the section {@code other}, under the same header */
  @SettingsHeader(key = "planitTestSettings", version = 1)
  @SettingsSection(key = "other", level = SettingsKeyLevel.RARE)
  public static final class TestOtherSettings {

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
  }

  /** Settings filling no section themselves, holding the settings of both sections */
  public static final class TestHolderSettings {

    /** settings of the section test */
    private final TestSettings test = new TestSettings();

    /** settings of the section other */
    private final TestOtherSettings other = new TestOtherSettings();

    /** @return the settings of the section test */
    public TestSettings getTest() {
      return test;
    }

    /** @return the settings of the section other */
    public TestOtherSettings getOther() {
      return other;
    }
  }

  /** Settings with two constructors taking root values, which is refused */
  public static final class TestTwoRootValueConstructors {

    /** @param country a root value */
    public TestTwoRootValueConstructors(@SettingsRootValue(key = "country") String country) {
    }

    /**
     * @param country a root value
     * @param region another root value
     */
    public TestTwoRootValueConstructors(@SettingsRootValue(key = "country") String country,
        @SettingsRootValue(key = "region") String region) {
    }
  }

  /** Settings with a constructor taking a root value and another value, which is refused */
  public static final class TestPartlyRootValueConstructor {

    /**
     * @param country a root value
     * @param region not a root value
     */
    public TestPartlyRootValueConstructor(@SettingsRootValue(key = "country") String country, String region) {
    }
  }

  /** Settings with a constructor taking a root value that is not text, which is refused */
  public static final class TestNonTextRootValue {

    /** @param year a root value as a number */
    public TestNonTextRootValue(@SettingsRootValue(key = "year") int year) {
    }
  }

  /** The schema of the test sources: the sections test and other */
  private static final SettingsSchema TEST_SCHEMA = new SettingsSchema(TestSettings.class, TestOtherSettings.class);

  /** The first line of every test source */
  private static final String HEADER = "planitTestSettings: 1\n";

  /**
   * A source of YAML text under the test header
   *
   * @param body the lines after the header
   * @return the source
   */
  private static SettingsSource text(String body) {
    return SettingsSource.ofText(HEADER + body);
  }

  /**
   * Test settings made from the sources, the section {@code test}
   *
   * @param sources in order
   * @return the settings
   */
  private static TestSettings load(SettingsSource... sources) {
    return new SettingsYamlLoader(TEST_SCHEMA, List.of(sources)).create(TestSettings.class);
  }

  /**
   * The problems making test settings from the sources stops with
   *
   * @param sources in order
   * @return the problems
   */
  private static List<SettingsProblem> problemsOf(SettingsSource... sources) {
    var loader = new SettingsYamlLoader(TEST_SCHEMA, List.of(sources));
    return assertThrows(SettingsProblemsException.class, () -> loader.create(TestSettings.class)).getProblems();
  }

  /**
   * Count the problems of one kind
   *
   * @param problems to count in
   * @param kind to count
   * @return the count
   */
  private static long count(List<SettingsProblem> problems, SettingsProblemKind kind) {
    return problems.stream().filter(problem -> problem.getKind() == kind).count();
  }

  /**
   * A source of a file written under the test header
   *
   * @param relativePath of the file in the test folder
   * @param body the lines after the header
   * @return the source
   * @throws IOException when the file cannot be written
   */
  private SettingsSource file(String relativePath, String body) throws IOException {
    Path path = tempDir.resolve(relativePath);
    Files.createDirectories(path.getParent());
    Files.writeString(path, HEADER + body);
    return SettingsSource.ofFile(path);
  }

  /** folder for the test files */
  @TempDir
  public Path tempDir;

  /** A key left out of every source keeps the default of the settings */
  @Test
  public void testKeyLeftOutKeepsDefault() {
    var settings = load(text("test:\n  name: x\n"));
    assertEquals(TestSettings.DEFAULT_MAXIMUM_REACH_METERS, settings.getMaximumReachMeters());
    assertEquals("x", settings.getName());
  }

  /** Code that changes the settings after the files replaces what the files gave */
  @Test
  public void testChangedInCodeAfterTheFiles() {
    var settings = load(text("test:\n  maximumReachMeters: 50\n"));
    settings.setMaximumReachMeters(80);
    assertEquals(80, settings.getMaximumReachMeters());
  }

  /** YAML text works as a source as a file with the same content does */
  @Test
  public void testYamlTextAsASource() throws IOException {
    assertEquals(50,
        load(text("test:\n  maximumReachMeters: 50\n")).getMaximumReachMeters());
    assertEquals(50,
        load(file("a.yaml", "test:\n  maximumReachMeters: 50\n")).getMaximumReachMeters());
  }

  /** Each value is converted to the type its method takes */
  @Test
  public void testValuesConverted() {
    var settings = load(text("test:\n  name: some text\n  enabled: true\n  seed: 12345678901\n  " +
        "kind: RAMP_METER\n" +
        "  maximumReachMeters: 60.5\n  nested:\n    steps: 5\n"));
    assertEquals("some text", settings.getName());
    assertTrue(settings.isEnabled());
    assertEquals(12345678901L, settings.getSeed());
    assertEquals(TestKind.RAMP_METER, settings.getKind());
    assertEquals(60.5, settings.getMaximumReachMeters());
    assertEquals(5, settings.getNested().getSteps());
    assertEquals(7L, load(text("test:\n  threshold: 7\n")).getThreshold());
    assertEquals(7.5, load(text("test:\n  threshold: 7.5\n")).getThreshold());
  }

  /** A replacing key given by two files takes the second file's value */
  @Test
  public void testReplacingKeyInTwoFiles() throws IOException {
    var settings = load(file("a.yaml", "test:\n  maximumReachMeters: 50\n"),
        file("b.yaml", "test:\n  maximumReachMeters: 80\n"));
    assertEquals(80, settings.getMaximumReachMeters());
  }

  /** An adding key given by two files adds the entries of both */
  @Test
  public void testAddingKeyInTwoFiles() throws IOException {
    var settings = load(file("a.yaml", "test:\n  excludedIds: [1, 2]\n"),
        file("b.yaml", "test:\n  excludedIds: [3]\n"));
    assertEquals(Set.of(1L, 2L, 3L), settings.getExcludedIds());
  }

  /** An adding key whose method takes several values makes one call per entry, each entry a list */
  @Test
  public void testAddingKeyOfLists() {
    var groups = load(text("test:\n  groups: [[1, 2], [3, 4, 5]]\n")).getGroups();
    assertEquals(2, groups.size());
    assertArrayEquals(new long[] {1, 2}, groups.get(0));
    assertArrayEquals(new long[] {3, 4, 5}, groups.get(1));
  }

  /** A map key given by two files sets each entry, the second file replacing an entry of the same key */
  @Test
  public void testMapKeyInTwoFiles() throws IOException {
    var settings = load(file("a.yaml", "test:\n  kindOverrides: {10: SIGNAL, 11: SIGNAL}\n"),
        file("b.yaml", "test:\n  kindOverrides: {11: RAMP_METER}\n"));
    assertEquals(UserOverride.of(TestKind.SIGNAL), settings.getKindOverrides().get(10L));
    assertEquals(UserOverride.of(TestKind.RAMP_METER), settings.getKindOverrides().get(11L));
  }

  /** Within a section the whole list applies first, then the removals, then the additions, whatever the order */
  @Test
  public void testWholeListThenMinusThenPlus() {
    var settings = load(text("test:\n  types+: [busway]\n  types-: [secondary]\n" +
        "  types: [primary, secondary, tertiary]\n"));
    assertEquals(List.of("primary", "tertiary", "busway"), settings.getTypes());
  }

  /** A value both added and removed in one source is a problem */
  @Test
  public void testValueAddedAndRemovedIsAProblem() {
    var problems = problemsOf(text("test:\n  types+: [busway]\n  types-: [busway]\n"));
    assertEquals(1, problems.size());
    assertEquals(1, count(problems, SettingsProblemKind.ADDED_AND_REMOVED));
  }

  /** A source headed for other settings, without a header, or of a version not known, is refused */
  @Test
  public void testHeaderChecked() {
    assertEquals(1,
        count(problemsOf(SettingsSource.ofText("planitOtherSettings: 1\ntest:\n  name: x\n")),
        SettingsProblemKind.WRONG_HEADER));
    assertEquals(1,
        count(problemsOf(SettingsSource.ofText("test:\n  name: x\n")), SettingsProblemKind.MISSING_HEADER));
    assertEquals(1,
        count(problemsOf(SettingsSource.ofText("planitTestSettings: 2\ntest:\n  name: x\n")),
        SettingsProblemKind.UNKNOWN_VERSION));
  }

  /** Problems across several files are reported together, each naming its source */
  @Test
  public void testSeveralProblemsTogether() throws IOException {
    var first = file("a.yaml", "test:\n  nmae: x\n  maximumReach: 3\n");
    var second = file("b.yaml", "test:\n  kind: signal\n  nested:\n    stepz: 2\n");
    var problems = problemsOf(first, second);
    assertEquals(4, problems.size());
    assertEquals(3, count(problems, SettingsProblemKind.UNKNOWN_KEY));
    assertEquals(1, count(problems, SettingsProblemKind.UNKNOWN_ENUM_VALUE));
    assertEquals(2,
        problems.stream().filter(problem -> problem.getSource().equals(first.getDescription())).count());
  }

  /** A value of the wrong type is a problem; a yes or no value is true or false only, so YAML 1.1 forms are refused */
  @Test
  public void testWrongTypeIsAProblem() {
    assertEquals(1, count(problemsOf(text("test:\n  seed: many\n")), SettingsProblemKind.WRONG_TYPE));
    assertEquals(1, count(problemsOf(text("test:\n  enabled: NO\n")), SettingsProblemKind.WRONG_TYPE));
    assertEquals(1,
        count(problemsOf(text("test:\n  nested:\n    steps: 1.5\n")), SettingsProblemKind.WRONG_TYPE));
    assertEquals(1,
        count(problemsOf(text("test:\n  inputSource: nosuch://x\n")), SettingsProblemKind.WRONG_TYPE));
  }

  /** A value the method refuses is a problem naming its source and the path of its key */
  @Test
  public void testRefusedValueIsAProblem() throws IOException {
    var source = file("a.yaml", "test:\n  nested:\n    steps: 0\n");
    var problems = problemsOf(source);
    assertEquals(1, count(problems, SettingsProblemKind.VALUE_REFUSED));
    assertEquals(source.getDescription(), problems.get(0).getSource());
    assertEquals("test.nested.steps", problems.get(0).getKeyPath());
  }

  /** A key given twice in one section is a problem */
  @Test
  public void testDuplicateKeyIsAProblem() {
    assertEquals(1,
        count(problemsOf(text("test:\n  name: a\n  name: b\n")), SettingsProblemKind.DUPLICATE_KEY));
  }

  /** A value tagged with a Java type is not turned into that type, and is a problem */
  @Test
  public void testJavaTypeTagIsAProblem() {
    assertEquals(1, count(problemsOf(text("test:\n  name: !!java.io.File x\n")),
        SettingsProblemKind.UNREADABLE_TEXT));
  }

  /** A file that cannot be read is a problem */
  @Test
  public void testMissingFileIsAProblem() {
    assertEquals(1, count(problemsOf(SettingsSource.ofFile(tempDir.resolve("none.yaml"))),
        SettingsProblemKind.FILE_NOT_READ));
  }

  /**
   * A root section the settings made do not take is a problem, and so is a key outside the schema; a root value their
   * constructor takes is not
   */
  @Test
  public void testRootKeyNotTakenIsAProblem() {
    var problems = problemsOf(text("country: Australia\nother:\n  name: x\nmissing: 1\n"));
    assertEquals(2, problems.size());
    assertEquals(2, count(problems, SettingsProblemKind.UNKNOWN_KEY));
  }

  /** Settings holding the settings of the sections, rather than filling one, have each filled through its getter */
  @Test
  public void testSectionsThroughGetters() {
    var holder = new SettingsYamlLoader(TEST_SCHEMA, List.of(text("test:\n  name: x\nother:\n  name: y\n")))
        .create(TestHolderSettings.class);
    assertEquals("x", holder.getTest().getName());
    assertEquals("y", holder.getOther().getName());
  }

  /**
   * A class the schema has no section for, or whose constructors take root values in a way the annotation does not
   * allow, is refused, being a mistake in the code
   */
  @Test
  public void testClassesItCannotMakeRefused() {
    var loader = new SettingsYamlLoader(TEST_SCHEMA, List.of(text("test: {}\n")));
    assertThrows(PlanItRunTimeException.class, () -> loader.create(TestNestedSettings.class));
    assertThrows(PlanItRunTimeException.class, () -> loader.create(TestTwoRootValueConstructors.class));
    assertThrows(PlanItRunTimeException.class, () -> loader.create(TestPartlyRootValueConstructor.class));
    assertThrows(PlanItRunTimeException.class, () -> loader.create(TestNonTextRootValue.class));
  }

  /** A relative path resolves against the folder of the file it is in, and in text against the working folder */
  @Test
  public void testRelativePaths() throws IOException {
    var fromFile = load(file("config/a.yaml", "test:\n  inputFile: data/x.pbf\n")).getInputFile();
    assertEquals(tempDir.resolve("config/data/x.pbf").normalize(), fromFile);
    var fromText = load(text("test:\n  inputFile: data/x.pbf\n")).getInputFile();
    assertEquals(Path.of("").toAbsolutePath().resolve("data/x.pbf").normalize(), fromText);
    var settings =
        load(file("config/b.yaml", "test:\n  outputFolder: out\n  inputSource: data/x.pbf\n"));
    assertEquals(tempDir.resolve("config/out").normalize().toFile(), settings.getOutputFolder());
    assertEquals(tempDir.resolve("config/data/x.pbf").normalize().toUri().toURL(), settings.getInputSource());
  }

  /** A location with a scheme, such as https, is taken as written */
  @Test
  public void testLocationWithScheme() {
    var settings = load(text("test:\n  inputSource: https://example.org/x.pbf\n"));
    assertEquals("https://example.org/x.pbf", settings.getInputSource().toString());
  }

  /** An override entry is written plainly or with a reason, also when its value is a small section */
  @Test
  public void testOverrideEntries() {
    var settings = load(text("test:\n" +
        "  kindOverrides:\n    10: SIGNAL\n    11: {value: RAMP_METER, reason: \"on site 2026-10-30\"}\n" +
        "  lanesOverrides:\n    7: {forward: 1}\n" +
        "    8: {value: {forward: 2, backward: 2}, reason: \"checked on imagery\"}\n"));
    assertEquals(UserOverride.of(TestKind.SIGNAL), settings.getKindOverrides().get(10L));
    assertEquals(
        UserOverride.of(TestKind.RAMP_METER, "on site 2026-10-30"), settings.getKindOverrides().get(11L));
    assertEquals(UserOverride.of(new TestLanes(1, null)), settings.getLanesOverrides().get(7L));
    assertEquals(
        UserOverride.of(new TestLanes(2, 2), "checked on imagery"),
        settings.getLanesOverrides().get(8L));
  }

  /** A key's former name is still applied, with one warning */
  @Test
  public void testFormerKeyApplied() {
    var loader = new SettingsYamlLoader(TEST_SCHEMA, List.of(text("test:\n  maximumReachInMeters: 55\n")));
    var settings = loader.create(TestSettings.class);
    assertEquals(55, settings.getMaximumReachMeters());
    assertEquals(1, count(loader.getWarnings(), SettingsProblemKind.FORMER_KEY_USED));
  }

  /**
   * A value at the root goes to the constructor taking it, the last source giving it winning; without it, the
   * constructor taking nothing makes the settings
   */
  @Test
  public void testRootValueGoesToTheConstructor() {
    var settings = load(text("country: Australia\n"), text("country: Germany\n"), text("test:\n  name: x\n"));
    assertEquals("Germany", settings.getCountry());
    assertEquals("x", settings.getName());
    assertNull(load(text("test:\n  name: x\n")).getCountry());
  }
}
