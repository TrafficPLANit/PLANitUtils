package org.goplanit.utils.test;

import org.goplanit.utils.exceptions.SettingsProblemsException;
import org.goplanit.utils.misc.FileUtils;
import org.goplanit.utils.settings.SettingsFileContentType;
import org.goplanit.utils.settings.SettingsSource;
import org.goplanit.utils.settings.YamlWriteSettingsSupport;
import org.goplanit.utils.settings.problem.SettingsProblem;
import org.goplanit.utils.settings.problem.SettingsProblemKind;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Checks of the settings files a converter's settings write, and of the problems a {@link SettingsProblemsException}
 * holds when making settings throws it, shipped for the tests of every converter
 * <p>
 * A converter's tests hold two settings files per kind of settings: every key not at its default, and the global
 * defaults. These checks make settings from such a file, or at their defaults, write them out and compare the lines
 * written with those of the file, so a key added, renamed, or given another default or description fails a test until
 * the file is updated. A test of a mistake in a file checks the problems the exception holds instead. The checks fail
 * with an {@link AssertionError}, so the library needs no test framework, and a JUnit test calls them as any other
 * assertion.
 * </p>
 *
 * @author markr
 */
public final class SettingsFileAssertionUtils {

  /** the start of the line of the output directory in a settings file */
  private static final String OUTPUT_DIRECTORY_KEY = "outputDirectory: ";

  /**
   * Verify the lines of a file written are those expected
   *
   * @param expectedLines of the file
   * @param writtenFile to read
   * @param whatWasWritten said in the failure, such as "Every key"
   * @param file the lines are expected of, named in the failure
   * @throws IOException when the file written cannot be read
   * @throws AssertionError when the lines differ
   */
  private static void assertLinesWritten(
      List<String> expectedLines, Path writtenFile, String whatWasWritten, Path file) throws IOException {
    var writtenLines = Files.readAllLines(writtenFile);
    if (!expectedLines.equals(writtenLines)) {
      throw new AssertionError(String.format("%s written differs from %s%nexpected:%n%s%nwritten:%n%s",
          whatWasWritten, file.getFileName(), String.join(System.lineSeparator(), expectedLines),
          String.join(System.lineSeparator(), writtenLines)));
    }
  }

  /**
   * Settings made from a file of every key not at its default write that file again: with every key, with the keys
   * differing from the defaults, which are all of them, and when made again from the file they write without
   * descriptions
   *
   * @param fromYaml makes the settings from sources
   * @param everyKeyNotDefaultFile to make the settings from
   * @param folder to write the files to
   * @throws IOException when a file cannot be written or read
   * @throws AssertionError when a file written differs
   */
  public static void assertFileWrittenAgainBySettingsMadeFromIt(
      Function<List<SettingsSource>, ? extends YamlWriteSettingsSupport> fromYaml, Path everyKeyNotDefaultFile,
      Path folder) throws IOException {
    /* a relative output directory is written in full, as the loader reads it against the folder of the file */
    var expectedLines = Files.readAllLines(everyKeyNotDefaultFile).stream()
        .map(line -> !line.startsWith(OUTPUT_DIRECTORY_KEY) ? line :
            OUTPUT_DIRECTORY_KEY + FileUtils.resolveAgainstFolder(
                line.substring(OUTPUT_DIRECTORY_KEY.length()), everyKeyNotDefaultFile.getParent()))
        .collect(Collectors.toList());
    var settings = fromYaml.apply(List.of(SettingsSource.ofFile(everyKeyNotDefaultFile)));

    var everyKey = folder.resolve("every_key.yaml");
    settings.writeSettingsFile(everyKey, SettingsFileContentType.ALL_KEYS, true);
    assertLinesWritten(expectedLines, everyKey, "Every key", everyKeyNotDefaultFile);

    var keysDiffering = folder.resolve("keys_differing.yaml");
    settings.writeSettingsFile(keysDiffering, SettingsFileContentType.KEYS_DIFFERING_FROM_DEFAULTS, true);
    assertLinesWritten(expectedLines, keysDiffering, "The keys differing from the defaults", everyKeyNotDefaultFile);

    var withoutDescriptions = folder.resolve("without_descriptions.yaml");
    settings.writeSettingsFile(withoutDescriptions, SettingsFileContentType.ALL_KEYS, false);
    var madeAgain = fromYaml.apply(List.of(SettingsSource.ofFile(withoutDescriptions)));
    var everyKeyAgain = folder.resolve("every_key_again.yaml");
    madeAgain.writeSettingsFile(everyKeyAgain, SettingsFileContentType.ALL_KEYS, true);
    assertLinesWritten(expectedLines, everyKeyAgain,
        "Every key, of settings made again from a file without descriptions", everyKeyNotDefaultFile);
  }

  /**
   * Settings at their defaults write the file of the global defaults, with every key, and so do settings made from
   * that file
   *
   * @param fromYaml makes the settings from sources
   * @param atDefaults the settings at their defaults
   * @param globalDefaultsFile expected to be written
   * @param folder to write the files to
   * @throws IOException when a file cannot be written or read
   * @throws AssertionError when a file written differs
   */
  public static void assertFileWrittenBySettingsAtTheirDefaults(
      Function<List<SettingsSource>, ? extends YamlWriteSettingsSupport> fromYaml, YamlWriteSettingsSupport atDefaults,
      Path globalDefaultsFile, Path folder) throws IOException {
    var expectedLines = Files.readAllLines(globalDefaultsFile);

    var atDefaultsFile = folder.resolve("global_defaults.yaml");
    atDefaults.writeSettingsFile(atDefaultsFile, SettingsFileContentType.ALL_KEYS, true);
    assertLinesWritten(expectedLines, atDefaultsFile, "Every key at its default", globalDefaultsFile);

    var madeAgain = fromYaml.apply(List.of(SettingsSource.ofFile(globalDefaultsFile)));
    var madeAgainFile = folder.resolve("global_defaults_again.yaml");
    madeAgain.writeSettingsFile(madeAgainFile, SettingsFileContentType.ALL_KEYS, true);
    assertLinesWritten(expectedLines, madeAgainFile, "Every key, of settings made from the file", globalDefaultsFile);
  }

  /**
   * Run code expected to throw a {@link SettingsProblemsException}, such as making settings from a file with a
   * mistake, and give the problems the exception holds. Making settings throws that exception, holding every problem
   * found in the sources, so a test of a mistake checks those problems.
   * <p>
   * It does what JUnit's {@code assertThrows(SettingsProblemsException.class, ...).getProblems()} does, so the library
   * needs no test framework; a JUnit test can call it as any other assertion, the {@link AssertionError} failing the
   * test. Any other exception the code throws is passed on.
   * </p>
   *
   * @param makeSettings the code expected to throw, such as {@code () -> factory.createFromYaml(sources)}
   * @return the problems the exception holds
   * @throws AssertionError when the code throws nothing
   */
  public static List<SettingsProblem> assertThrowsSettingsProblems(Runnable makeSettings) {
    try {
      makeSettings.run();
    } catch (SettingsProblemsException e) {
      return e.getProblems();
    }
    throw new AssertionError("No SettingsProblemsException was thrown, one was expected");
  }

  /**
   * Run code expected to throw a {@link SettingsProblemsException} holding exactly one problem, of the kind given, as
   * making settings from a file with one mistake does, and give that problem, for a test to check where it was found.
   * As {@link #assertThrowsSettingsProblems(Runnable)}, a JUnit test can call it as any other assertion
   *
   * @param kind of the problem
   * @param makeSettings the code expected to throw
   * @return the problem
   * @throws AssertionError when the code throws nothing, or the exception holds more problems or one of another kind
   */
  public static SettingsProblem assertThrowsOneSettingsProblem(SettingsProblemKind kind, Runnable makeSettings) {
    var problems = assertThrowsSettingsProblems(makeSettings);
    if (problems.size() != 1 || problems.get(0).getKind() != kind) {
      throw new AssertionError(String.format("Expected one settings problem of kind %s, the exception holds %s",
          kind, problems));
    }
    return problems.get(0);
  }
}
