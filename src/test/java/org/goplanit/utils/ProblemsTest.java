package org.goplanit.utils;

import org.goplanit.utils.exceptions.ProblemsRunTimeException;
import org.goplanit.utils.problem.Problem;
import org.goplanit.utils.problem.Problems;
import org.goplanit.utils.settings.problem.SettingsProblemKind;
import org.goplanit.utils.yaml.YamlProblemKind;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests of the problems found in an input and kept to be reported together, of whatever kind the caller names them by:
 * a YAML text's, a settings file's, a pricing scheme's
 *
 * @author markr
 */
public class ProblemsTest {

  /**
   * The kinds of a list of problems, in order, so a test compares kinds in one assertion. Public, as the tests of the
   * readers giving problems ask the same of theirs
   *
   * @param <K> type of the kinds
   * @param problems to take the kinds of
   * @return the kinds
   */
  public static <K extends Enum<K>> List<K> kindsOf(List<Problem<K>> problems) {
    return problems.stream().map(Problem::getKind).collect(Collectors.toList());
  }

  /** A problem is added only when it was found, and is then held with what it was given */
  @Test
  public void addIfAddsOnlyWhatWasFound() {
    var problems = new Problems<SettingsProblemKind>();
    problems.addIf(false, SettingsProblemKind.UNKNOWN_KEY, "a.yaml", "network.nmae", "No such key");
    assertTrue(problems.isEmpty());

    problems.addIf(true, SettingsProblemKind.UNKNOWN_KEY, "a.yaml", "network.nmae", "No such key");
    assertFalse(problems.isEmpty());
    assertEquals(1, problems.size());
    var problem = problems.stream().findFirst().orElseThrow();
    assertEquals(SettingsProblemKind.UNKNOWN_KEY, problem.getKind());
    assertEquals("a.yaml", problem.getSource());
    assertEquals("network.nmae", problem.getSubject());
    assertEquals("No such key", problem.getDetail());
  }

  /** has tells whether a problem of one kind was found, hasAny whether one of the kinds meeting a condition was */
  @Test
  public void hasAndHasAnyAskByKind() {
    var problems = new Problems<SettingsProblemKind>();
    problems.add(SettingsProblemKind.WRONG_TYPE, "a.yaml", "lanes", "Expected a number");

    assertTrue(problems.has(SettingsProblemKind.WRONG_TYPE));
    assertFalse(problems.has(SettingsProblemKind.UNKNOWN_KEY));
    assertTrue(problems.hasAny(kind -> kind.name().startsWith("WRONG")));
    assertFalse(problems.hasAny(kind -> kind == SettingsProblemKind.FILE_NOT_READ));
  }

  /** The problems of a later pass are added after those recorded already, in their own order */
  @Test
  public void addAllAddsAnotherPassAfterwards() {
    var problems = new Problems<SettingsProblemKind>();
    problems.add(SettingsProblemKind.WRONG_TYPE, "a.yaml", "lanes", "Expected a number");
    var later = new Problems<SettingsProblemKind>();
    later.add(SettingsProblemKind.UNKNOWN_KEY, "a.yaml", "nmae", "No such key");
    later.add(SettingsProblemKind.FILE_NOT_READ, "b.yaml", "", "Not found");

    problems.addAll(later);

    assertEquals(List.of(SettingsProblemKind.WRONG_TYPE, SettingsProblemKind.UNKNOWN_KEY,
        SettingsProblemKind.FILE_NOT_READ), kindsOf(problems.stream().collect(Collectors.toList())));
    assertEquals(2, later.size());
  }

  /** Without problems nothing is thrown; with problems the exception thrown holds every one, in the order found */
  @Test
  public void throwIfAnyThrowsHoldingEveryProblem() {
    var problems = new Problems<SettingsProblemKind>();
    problems.throwIfAny("Settings files have");

    problems.add(SettingsProblemKind.UNKNOWN_KEY, "a.yaml", "nmae", "No such key");
    problems.add(SettingsProblemKind.WRONG_TYPE, "b.yaml", "lanes", "Expected a number");
    var thrown = assertThrows(ProblemsRunTimeException.class, () -> problems.throwIfAny("Settings files have"));
    assertEquals(List.of(SettingsProblemKind.UNKNOWN_KEY, SettingsProblemKind.WRONG_TYPE),
        kindsOf(thrown.getProblems(SettingsProblemKind.class)));
  }

  /** An exception holding problems of two kind types gives those of the kind type asked for only */
  @Test
  public void getProblemsGivesThoseOfOneKindType() {
    var yamlProblem = new Problem<>(YamlProblemKind.DUPLICATE_KEY, "", "name", "Given twice on line 7");
    var settingsProblem = new Problem<>(SettingsProblemKind.UNKNOWN_KEY, "a.yaml", "nmae", "No such key");
    var exception = new ProblemsRunTimeException("Inputs have", List.of(yamlProblem, settingsProblem));

    assertEquals(List.of(YamlProblemKind.DUPLICATE_KEY), kindsOf(exception.getProblems(YamlProblemKind.class)));
    assertEquals(List.of(SettingsProblemKind.UNKNOWN_KEY), kindsOf(exception.getProblems(SettingsProblemKind.class)));
  }
}
