package org.goplanit.utils.exceptions;

import org.goplanit.utils.misc.LoggingUtils;
import org.goplanit.utils.settings.problem.SettingsProblem;

import java.util.List;

/**
 * Thrown when settings files cannot be applied, holding every problem found in them
 * <p>
 * For example, catching it to log each problem on a line of its own:
 * </p>
 * <pre>
 * try {
 *   settings = loader.create(ExampleNetworkSettings.class);
 * } catch (SettingsProblemsException e) {
 *   e.getProblems().forEach(problem -&gt; LOGGER.severe(problem.toString()));
 * }
 * </pre>
 * <p>
 * The loader checks every file before it stops, so a user fixes all their mistakes in one go rather than one per run.
 * The message of the exception lists the problems, one per line.
 * </p>
 *
 * @author markr
 */
public class SettingsProblemsException extends PlanItRunTimeException {

  /** generated UID */
  private static final long serialVersionUID = 1L;

  /** the problems found */
  private final List<SettingsProblem> problems;

  /**
   * Constructor
   *
   * @param problems found, at least one
   */
  public SettingsProblemsException(List<SettingsProblem> problems) {
    super(LoggingUtils.problemsMessage("Settings files have", problems));
    this.problems = List.copyOf(problems);
  }

  /**
   * The problems found, in the order found
   *
   * @return the problems
   */
  public List<SettingsProblem> getProblems() {
    return problems;
  }
}
