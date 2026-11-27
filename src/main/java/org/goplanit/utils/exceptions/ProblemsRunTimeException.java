package org.goplanit.utils.exceptions;

import org.goplanit.utils.misc.LoggingUtils;
import org.goplanit.utils.problem.Problem;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Thrown when an input has problems that stop it being used, holding every problem found in it
 * <p>
 * For example, catching it to log each problem of a settings file on a line of its own:
 * </p>
 * <pre>
 * try {
 *   settings = loader.create(ExampleNetworkSettings.class);
 * } catch (ProblemsRunTimeException e) {
 *   e.getProblems(SettingsProblemKind.class).forEach(problem -&gt; LOGGER.severe(problem.toString()));
 * }
 * </pre>
 * <p>
 * The input is checked as a whole before it stops, so all mistakes can be fixed in one go. The message of the
 * exception lists the problems, one per line. The problems are named by the kinds of whoever found them, so the
 * exception gives those of one kind type at a time.
 * </p>
 *
 * @author markr
 */
public class ProblemsRunTimeException extends PlanItRunTimeException {

  /** generated UID */
  private static final long serialVersionUID = 1L;

  /** the problems found, in the order found */
  private final List<Problem<?>> problems;

  /**
   * Constructor
   *
   * @param introduction of the message, such as {@code YAML has}
   * @param problems found, at least one
   */
  public ProblemsRunTimeException(String introduction, List<? extends Problem<?>> problems) {
    super(LoggingUtils.problemsMessage(introduction, problems));
    this.problems = List.copyOf(problems);
  }

  /**
   * The problems found of one kind type, such as {@code SettingsProblemKind.class}
   *
   * @param <K> type of the kinds
   * @param kindType the enum of the kinds
   * @return the problems of that kind type, in the order found
   */
  @SuppressWarnings("unchecked")
  public <K extends Enum<K>> List<Problem<K>> getProblems(Class<K> kindType) {
    return problems.stream()
        .filter(problem -> kindType.isInstance(problem.getKind()))
        .map(problem -> (Problem<K>) problem)
        .collect(Collectors.toList());
  }
}
